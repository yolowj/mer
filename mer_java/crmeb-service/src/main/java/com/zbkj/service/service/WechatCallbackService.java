package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.WechatCallback;

/**
 * 微信小程序回调Service
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface WechatCallbackService extends IService<WechatCallback> {

    /**
     * 微信回调
     * @param request request
     * @return String
     */
    String callback(String request);

//    /**
//     * 自定义交易组件 通过回调 同步支付结果
//     * @param jsonObject 仅仅传递call的内容
//     */
//    void payComponentsSyncPaidStatus(JSONObject jsonObject);

//    /**
//     * 自定义交易组件 微信强制或风控下架
//     * @param jsonObject 仅仅传递call的内容
//     */
//    void payComponentsSyncRiskPutDownByWeiChat(JSONObject jsonObject);
}
