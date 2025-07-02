package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.prize.LotteryRecord;
import com.zbkj.common.model.prize.PrizeDraw;
import com.zbkj.common.model.prize.PrizeDrawRequest;
import com.zbkj.common.model.user.User;
import com.zbkj.common.response.LotteryResponse;
import com.zbkj.common.utils.RedisDistributedLock;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.dao.LotteryRecordDao;
import com.zbkj.service.dao.PrizeDrawDao;
import com.zbkj.service.service.LotteryRecordService;
import com.zbkj.service.service.PrizeDrawService;
import com.zbkj.service.service.UserIntegralRecordService;
import com.zbkj.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author berton
 * @description PrizeDrawServiceImpl 接口实现
 * @date 2025-06-26
 */
@Slf4j
@Service
public class PrizeDrawServiceImpl extends ServiceImpl<PrizeDrawDao, PrizeDraw> implements PrizeDrawService {

    @Resource
    private PrizeDrawDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

 /*   @Autowired
    private UserIntegralRecordService userIntegralRecordService;*/

    @Autowired
    private LotteryRecordService lotteryRecordService;

    @Resource
    private LotteryRecordDao lotteryRecordDao;


    /**
     * 分页
     * @param prizeDrawRequest 分页类参数
     * @author berton
     * @since 2025-06-26
     * @return List<PrizeDraw>
     */
    @Override
    public List<LotteryRecord> getLotteryRecordList(PrizeDrawRequest prizeDrawRequest) {
        PageHelper.startPage(prizeDrawRequest.getPage(), prizeDrawRequest.getLimit());

        //带 PrizeDraw 类的多条件查询
        LambdaQueryWrapper<LotteryRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        LotteryRecord model = new LotteryRecord();
        BeanUtils.copyProperties(prizeDrawRequest, model);
        lambdaQueryWrapper.orderByDesc(LotteryRecord::getCreateTime);
        lambdaQueryWrapper.setEntity(model);
        return lotteryRecordDao.selectList(lambdaQueryWrapper);
    }


    // 全局锁的key
    private static final String GLOBAL_LOCK_KEY = "global_lottery_lock";
    // 最大重试次数
    private static final int MAX_RETRIES = 10;
    // 库存缓存前缀
    private static final String STOCK_PREFIX = "prize_stock:";


    @Override
    public LotteryResponse  execute() {
        User user = userService.getInfo();
        // 1. 检查用户积分是否足够
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }

        // 4. 检查积分是否足够(需要10积分补签)
        int needIntegral = 10;
        if (user.getIntegral() < needIntegral) {
            throw new CrmebException("积分不足，抽奖需要" + needIntegral + "积分");
        }
        // 1. 使用缓存快速检查总库存
        if (getCachedTotalStock() <= 0) {
            // log.info("所有奖品库存为零，用户 {} 未抽中奖品", userId);
            return null;
        }

        // 2. 获取分布式锁
        String lockIdentifier = redisLock.tryLock(GLOBAL_LOCK_KEY);
        if (lockIdentifier == null) {
            log.warn("用户 {} 获取锁失败", user.getId());
            // 获取锁失败时重试一次
            lockIdentifier = redisLock.tryLock(GLOBAL_LOCK_KEY);
            if (lockIdentifier == null) {
                log.error("用户 {} 二次获取锁失败", user.getId());
                return null;
            }
        }

        try {
            // 3. 执行抽奖事务（保证必中）
            PrizeDraw prizeDraw = executeGuaranteedLottery(user.getId());
            if(ObjectUtil.isNotNull(prizeDraw)){
                LotteryResponse response = new LotteryResponse();
                response.setId(prizeDraw.getId());
                response.setCurrentPoints(user.getIntegral()-10);
                return  response;
            }
            return null;

        } finally {
            // 4. 释放分布式锁
            redisLock.releaseLock(GLOBAL_LOCK_KEY, lockIdentifier);
        }


    }


    @Autowired
    private RedisDistributedLock redisLock;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 执行保证必中的抽奖逻辑
     */
    @Transactional
    protected PrizeDraw executeGuaranteedLottery(Integer userId) {
        // 1. 获取有效奖品列表
        List<PrizeDraw> validPrizes = getValidPrizes();
        if (validPrizes.isEmpty()) {
            log.info("无可用奖品");
            return null;
        }

        // 2. 准备抽奖
        Random random = new Random();
        int retryCount = 0;
        PrizeDraw selectedPrize = null;

        // 3. 抽奖循环（保证必中）
        while (selectedPrize == null && retryCount < MAX_RETRIES && !validPrizes.isEmpty()) {
            retryCount++;

            // 3.1 随机选择奖品
            PrizeDraw candidate = selectRandomPrize(validPrizes, random);

            // 3.2 使用Redis原子操作预减库存
            Long remainStock = redisUtil.decr(STOCK_PREFIX + candidate.getId(), 1);

            if (remainStock != null && remainStock >= 0) {
                updateUserStock(userId,10);
                // 3.3 库存预减成功
                selectedPrize = candidate;

                // 3.4 更新数据库库存
                updateDatabaseStock(candidate.getId());

                // 3.5 记录抽奖结果
                recordLotteryResult(userId, selectedPrize);


                // 3.5 记录抽奖结果
                recordLotteryResult(userId, selectedPrize);

                log.info("用户 {} 抽中奖品: {} (重试次数: {})",
                        userId, selectedPrize.getCouponName(), retryCount);
            } else {
                // 3.6 库存不足处理
                if (remainStock != null && remainStock < 0) {
                    // 回滚预减操作
                    redisUtil.incr(STOCK_PREFIX + candidate.getId(), 1);
                }

                log.warn("奖品 {} 库存不足，尝试重新选择 (重试次数: {})",
                        candidate.getCouponName(), retryCount);

                // 从列表中移除该奖品
                validPrizes.remove(candidate);
                redisUtil.set("prizes:List",validPrizes);
                // 短暂等待（指数退避）
                try {
                    Thread.sleep(calculateBackoffTime(retryCount));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (selectedPrize == null) {
            log.error("用户 {} 抽奖失败，达到最大重试次数 {}", userId, MAX_RETRIES);
        }

        return selectedPrize;
    }



    /**
     * 随机选择奖品（权重算法）
     */
    private PrizeDraw selectRandomPrize(List<PrizeDraw> prizes, Random random) {

        BigDecimal total = prizes.parallelStream()  // 并行流
                .map(PrizeDraw::getProbability)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal randomPoint = new BigDecimal(random.nextDouble() * total.doubleValue());
        BigDecimal currentWeight = BigDecimal.ZERO;

        for (PrizeDraw prize : prizes) {
            currentWeight = currentWeight.add(prize.getProbability());
            if (randomPoint.compareTo(currentWeight)<=0) {
                return prize;
            }
        }

        // 权重算法异常时返回第一个奖品
        return prizes.get(0);
    }

    /**
     * 更新数据库库存
     */
    private void updateDatabaseStock(Integer prizeId) {
        LambdaUpdateWrapper<PrizeDraw> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(PrizeDraw::getId, prizeId) // WHERE id = ?
                .setSql("num = num - 1 ");
        update(wrapper);
    }

    /**
     * 更新数据库库存
     */
    private void updateUserStock(Integer userId, Integer integral) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId) // WHERE id = ?
                .setSql("integral = integral - " +integral);
        userService.update(wrapper);
    }

    /**
     * 记录抽奖结果
     */
    private void recordLotteryResult(Integer userId, PrizeDraw prize) {
        LotteryRecord record = new LotteryRecord();
        record.setUserId(userId);
        record.setPrizeId(prize.getId());
        record.setPrizeType(prize.getType());
        record.setPrizeValue(prize.getValue());
        //record.setCostPoints(prize.getCostPoints());
        record.setStatus(1);
        record.setCreateTime(new Date());
        lotteryRecordService.save(record);
    }

    /**
     * 计算指数退避时间
     */
    private long calculateBackoffTime(int retryCount) {
        // 基础等待时间50ms，指数增长 (50, 100, 200, 400...)
        return (long) (50 * Math.pow(2, Math.min(retryCount, 5)));
    }

    /**
     * 获取缓存的总库存
     */
    private int getCachedTotalStock() {
        // 使用Redis缓存总库存
        Object stock = redisUtil.get("prizes:total_stock");
        if (stock != null) {
            return Integer.parseInt(stock.toString());
        }

        // 缓存不存在时查询数据库
        int totalStock = getTotalPrizeStock();
        Long l = 5l;
        redisUtil.set("prizes:total_stock", totalStock, l, TimeUnit.SECONDS);
        return totalStock;
    }

    /**
     * 获取奖品总库存
     */
    private int getTotalPrizeStock() {

        QueryWrapper<PrizeDraw> wrapper = new QueryWrapper<>();
        wrapper.select("SUM(num) as tol");

        // 4. 执行查询（返回 Map 列表）
        List<Map<String, Object>> result = dao.selectMaps(wrapper);

        // 5. 处理结果
        if (result != null && !result.isEmpty()) {
            Object sum = result.get(0).get("tol");
            return sum == null ? 0 : Integer.parseInt( sum.toString());
        }
        return 0;
    }

    /**
     * 查询有效奖品列表（库存>0）
     */
    private List<PrizeDraw> getValidPrizes() {

        List<PrizeDraw>  prizeDrawList = redisUtil.get("prizes:List");
        if(ObjectUtil.isNotNull(prizeDrawList)) return  prizeDrawList;
        LambdaQueryWrapper<PrizeDraw> lqw = Wrappers.lambdaQuery();
        lqw.gt(PrizeDraw::getNum, 0);

        prizeDrawList = dao.selectList(lqw);

        log.info("初始化奖品库存缓存，共{}个奖品", prizeDrawList.size());
        for (PrizeDraw prize : prizeDrawList) {
            String key = STOCK_PREFIX + prize.getId();
            // 使用RedisUtil设置缓存，不设置过期时间
            redisUtil.set(key, prize.getNum());
            log.debug("奖品ID: {}, 库存: {}", prize.getId(), prize.getNum());
        }
        // 同时初始化总库存缓存
        int totalStock = prizeDrawList.stream().mapToInt(PrizeDraw::getNum).sum();
        redisUtil.set("prizes:total_stock", totalStock);
        log.info("总库存初始化: {}", totalStock);

        redisUtil.set("prizes:List",prizeDrawList);

        return prizeDrawList;
    }




    @Override
    public List<PrizeDraw> getList() {
        return  dao.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public List<PrizeDraw> getList(PrizeDrawRequest prizeDrawRequest) {
        return  dao.selectList(new LambdaQueryWrapper<>());
    }




}

