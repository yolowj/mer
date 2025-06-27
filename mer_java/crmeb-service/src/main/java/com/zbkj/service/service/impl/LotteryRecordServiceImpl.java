package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.model.prize.LotteryRecord;
import com.zbkj.common.model.prize.PrizeDrawRequest;
import com.zbkj.service.dao.LotteryRecordDao;
import com.zbkj.service.service.LotteryRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author berton
* @description LotteryRecordServiceImpl 接口实现
* @date 2025-06-26
*/
@Service
public class LotteryRecordServiceImpl extends ServiceImpl<LotteryRecordDao, LotteryRecord> implements LotteryRecordService {

    @Resource
    private LotteryRecordDao dao;


    /**
    * 列表
    * @param prizeDrawRequest 分页类参数
    * @author berton
    * @since 2025-06-26
    * @return List<LotteryRecord>
    */
    @Override
    public List<LotteryRecord> getList(PrizeDrawRequest prizeDrawRequest) {
        PageHelper.startPage(prizeDrawRequest.getPage(), prizeDrawRequest.getLimit());

        //带 LotteryRecord 类的多条件查询
        LambdaQueryWrapper<LotteryRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        LotteryRecord model = new LotteryRecord();
        BeanUtils.copyProperties(prizeDrawRequest, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

}

