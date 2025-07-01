package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.IntegralRecordConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.prize.LotteryRecord;
import com.zbkj.common.model.prize.PrizeDraw;
import com.zbkj.common.model.prize.PrizeDrawRequest;
import com.zbkj.common.model.prize.UserRequest;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.utils.RedisDistributedLock;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.dao.PrizeDrawDao;
import com.zbkj.service.dao.UserDao;
import com.zbkj.service.service.LotteryRecordService;
import com.zbkj.service.service.PrizeDrawService;
import com.zbkj.service.service.UserIntegralRecordService;
import com.zbkj.service.service.UserService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
* @author berton
* @description PrizeDrawServiceImpl 接口实现
* @date 2025-06-26
*/
@Service
public class PrizeDrawServiceImpl extends ServiceImpl<PrizeDrawDao, PrizeDraw> implements PrizeDrawService {

    @Resource
    private PrizeDrawDao dao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    @Autowired
    private LotteryRecordService lotteryRecordService;


    /**
    * 分页
    * @param prizeDrawRequest 分页类参数
    * @author berton
    * @since 2025-06-26
    * @return List<PrizeDraw>
    */
    @Override
    public List<PrizeDraw> getList(PrizeDrawRequest prizeDrawRequest) {
        PageHelper.startPage(prizeDrawRequest.getPage(), prizeDrawRequest.getLimit());

        //带 PrizeDraw 类的多条件查询
        LambdaQueryWrapper<PrizeDraw> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        PrizeDraw model = new PrizeDraw();
        BeanUtils.copyProperties(prizeDrawRequest, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public PrizeDraw draw(Integer userId) {
        User user = userDao.selectById(userId);
        // 1. 检查用户积分是否足够
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }

        // 4. 检查积分是否足够(需要10积分补签)
        int needIntegral = 10;
        if (user.getIntegral() < needIntegral) {
            throw new CrmebException("积分不足，抽奖需要" + needIntegral + "积分");
        }

        PrizeDraw prize = doDraw();
        Boolean result = transactionTemplate.execute(e -> {
            // 扣除补签所需积分
            userService.updateIntegral(user.getId(), needIntegral, Constants.OPERATION_TYPE_SUBTRACT);

            UserIntegralRecord deductRecord = new UserIntegralRecord();
            deductRecord.setUid(user.getId());
            deductRecord.setLinkId("0");
            deductRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
            deductRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
            deductRecord.setTitle("补签扣除");
            deductRecord.setMark(StrUtil.format("抽奖扣除{}积分", needIntegral));
            deductRecord.setIntegral(needIntegral);
            deductRecord.setBalance(user.getIntegral() - needIntegral);
            deductRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            userIntegralRecordService.save(deductRecord);


            // 5. 记录抽奖结果
            LotteryRecord record = new LotteryRecord();
            record.setUserId(userId);
            if (prize.getType() != 4) { // 非谢谢惠顾
                record.setPrizeId(prize.getId());
            }
            record.setPrizeType(prize.getType());
            record.setPrizeValue(prize.getValue());
            record.setCostPoints(needIntegral);
            record.setCreateTime(new Date());
            lotteryRecordService.save(record);

            // 6. 如果是积分奖品，增加用户积分
            if (prize.getType() == 3) { // 3:积分
                if (prize.getValue() > 0) {
                    userService.updateIntegral(user.getId(), prize.getValue(), Constants.OPERATION_TYPE_ADD);
                    UserIntegralRecord integralRecord = new UserIntegralRecord();
                    integralRecord.setUid(user.getId());
                    integralRecord.setLinkId("0");
                    integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
                    integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                    integralRecord.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_SIGN);
                    integralRecord.setMark(StrUtil.format("补签奖励{}积分", prize.getValue()));
                    integralRecord.setIntegral(prize.getValue());
                    integralRecord.setBalance(user.getIntegral() - needIntegral + prize.getValue());
                    integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
                    userIntegralRecordService.save(integralRecord);
                }
            }

            return Boolean.TRUE;
        });


        return prize;
    }

    private PrizeDraw doDraw() {
        List<PrizeDraw> prizes = dao.selectList(new QueryWrapper<PrizeDraw>());
        double totalProbability = prizes.stream().mapToDouble(p -> p.getProbability().doubleValue()).sum();

        Random random = new Random();
        double randomValue = random.nextDouble() * totalProbability;

        double tempSum = 0.0;
        for (PrizeDraw prize : prizes) {
            tempSum += prize.getProbability().doubleValue();
            if (randomValue <= tempSum) {
                return prize;
            }
        }

        return createDefaultNoPrize();
    }

    private PrizeDraw createDefaultNoPrize() {
        PrizeDraw noPrize = new PrizeDraw();
        noPrize.setType(4); // 谢谢惠顾
        noPrize.setValue(0);
        noPrize.setProbability(BigDecimal.ZERO);
        noPrize.setImg("");
        return noPrize;
    }



    // 全局锁的key
    private static final String GLOBAL_LOCK_KEY = "global_lottery_lock";
    // 最大重试次数
    private static final int MAX_RETRIES = 10;
    // 库存缓存前缀
    private static final String STOCK_PREFIX = "prize_stock:";


    private void beforecheck(Integer userId){
        User user = userDao.selectById(userId);
        // 1. 检查用户积分是否足够
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }

        // 4. 检查积分是否足够(需要10积分补签)
        int needIntegral = 10;
        if (user.getIntegral() < needIntegral) {
            throw new CrmebException("积分不足，抽奖需要" + needIntegral + "积分");
        }
    }


    @Override
    public PrizeDraw drawNew(Integer userId) {
        beforecheck(userId);
        // 1. 使用缓存快速检查总库存
        if (getCachedTotalStock() <= 0) {
           // log.info("所有奖品库存为零，用户 {} 未抽中奖品", userId);
            return null;
        }

        // 2. 获取分布式锁
        String lockIdentifier = redisLock.tryLock(GLOBAL_LOCK_KEY);
        if (lockIdentifier == null) {
            //log.warn("用户 {} 获取锁失败", userId);
            // 获取锁失败时重试一次
            lockIdentifier = redisLock.tryLock(GLOBAL_LOCK_KEY);
            if (lockIdentifier == null) {
               // log.error("用户 {} 二次获取锁失败", userId);
                return null;
            }
        }

        try {
            // 3. 执行抽奖事务（保证必中）
           return  executeGuaranteedLottery(userId);
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
           // log.info("无可用奖品");
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
                // 3.3 库存预减成功
                selectedPrize = candidate;

                // 3.4 更新数据库库存
                updateDatabaseStock(candidate.getId());

                // 3.5 记录抽奖结果
                recordLotteryResult(userId, selectedPrize);

                //log.info("用户 {} 抽中奖品: {} (重试次数: {})",
                //        userId, selectedPrize.getCouponName(), retryCount);
            } else {
                // 3.6 库存不足处理
                if (remainStock != null && remainStock < 0) {
                    // 回滚预减操作
                    redisUtil.incr(STOCK_PREFIX + candidate.getId(), 1);
                }

               // log.warn("奖品 {} 库存不足，尝试重新选择 (重试次数: {})",
               //         candidate.getCouponName(), retryCount);

                // 从列表中移除该奖品
                validPrizes.remove(candidate);

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
           // log.error("用户 {} 抽奖失败，达到最大重试次数 {}", userId, MAX_RETRIES);
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
                .setSql("age = age - 1 ");
        update(wrapper);
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
        LambdaQueryWrapper<PrizeDraw> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(PrizeDraw::getNum);
        PrizeDraw result =  dao.selectOne(queryWrapper);
        return result != null ? result.getNum() : 0;
    }

    /**
     * 查询有效奖品列表（库存>0）
     */
    private List<PrizeDraw> getValidPrizes() {
        return new ArrayList<>();
    }




     @Override
    public List<LotteryRecord> getLotteryRecordList(Integer userId) {

        LambdaQueryWrapper<LotteryRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryRecord::getUserId, userId);
        lqw.orderByDesc(LotteryRecord::getCreateTime);
        return lotteryRecordService.list(lqw);

    }


}

