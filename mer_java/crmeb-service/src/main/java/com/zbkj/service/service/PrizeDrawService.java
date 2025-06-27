package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.prize.PrizeDrawRequest;
import com.zbkj.common.model.prize.PrizeDraw;

import java.util.List;

/**
* @author berton
* @description PrizeDrawService 接口
* @date 2025-06-26
*/
public interface PrizeDrawService extends IService<PrizeDraw> {

    List<PrizeDraw> getList(PrizeDrawRequest prizeDrawRequest);

    PrizeDraw draw(Integer userId);
}