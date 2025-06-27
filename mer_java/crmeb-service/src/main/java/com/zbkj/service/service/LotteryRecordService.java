package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.prize.LotteryRecord;
import com.zbkj.common.model.prize.PrizeDrawRequest;

import java.util.List;

/**
* @author berton
* @description LotteryRecordService 接口
* @date 2025-06-26
*/
public interface LotteryRecordService extends IService<LotteryRecord> {

    List<LotteryRecord> getList(PrizeDrawRequest prizeDrawRequest);
}