package com.zbkj.front.service;

import com.zbkj.common.model.user.User;
import com.zbkj.common.request.CreateFreeOrderRequest;
import com.zbkj.common.response.OrderNoResponse;
import com.zbkj.common.vo.FreeOrderInfoVo;

public interface LotteryService {
    void supply(Integer id);
}
