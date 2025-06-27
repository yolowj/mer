package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.ExperienceRecordConstants;
import com.zbkj.common.constants.IntegralRecordConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.prize.LotteryRecord;
import com.zbkj.common.model.prize.PrizeDrawRequest;
import com.zbkj.common.model.prize.PrizeDraw;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.service.dao.PrizeDrawDao;
import com.zbkj.service.dao.UserDao;
import com.zbkj.service.service.LotteryRecordService;
import com.zbkj.service.service.PrizeDrawService;
import com.zbkj.service.service.UserIntegralRecordService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

}

