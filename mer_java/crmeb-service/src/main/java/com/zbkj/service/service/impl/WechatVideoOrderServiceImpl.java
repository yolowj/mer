package com.zbkj.service.service.impl;

import com.zbkj.service.service.WechatVideoOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Service
public class WechatVideoOrderServiceImpl implements WechatVideoOrderService {

    private static final Logger logger = LoggerFactory.getLogger(WechatVideoOrderServiceImpl.class);

//    @Autowired
//    private RestTemplateUtil restTemplateUtil;
//
//    @Autowired
//    private WechatService wechatService;

//    /**
//     * 检查场景值是否在支付校验范围内
//     * @return is_matched 0: 不在支付校验范围内，1: 在支付校验范围内
//     */
//    @Override
//    public Integer shopSceneCheck(Integer scene) {
//        if (ObjectUtil.isNull(scene)) {
//            throw new CrmebException("场景值不能为空");
//        }
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_SCENE_CHECK_URL, miniAccessToken);
//        Map<String, Object> map = new HashMap<>();
//        map.put("scene", scene);
//        String mapData = restTemplateUtil.postMapData(url, map);
//        JSONObject jsonObject = JSONObject.parseObject(mapData);
//        WxUtil.checkResult(jsonObject);
//        return jsonObject.getInteger("is_matched");
//    }
//
//    /**
//     * 生成订单并获取ticket
//     * 注意：该接口可重入，如果order_id或out_order_id已存在，会直接更新整个订单数据
//     * 请求成功后将会创建一个status=10的订单
//     * 每个ticket只能消费一次，创建订单接口可以多次调，但是不是生成新ticket要视情况而定
//     * 场景A: 第一次生成ticketA，拉起收银台消费ticketA后ticketA就失效了 第二次再调就生成新的ticketB了
//     * 场景B: 第一次生成ticketA，不调收银台消费这个ticket，那么24小时内再调生成的还是ticketA，超过24小时生成的是新的ticketA
//     * @return ShopOrderAddResultVo
//     */
//    @Override
//    public ShopOrderAddResultVo shopOrderAdd(ShopOrderAddVo shopOrderAddVo) {
//        // 订单类型 一般默认都是=1
//        shopOrderAddVo.setFund_type(1);
//        Map<String, Object> orderAddMap = assembleOrderAddMap(shopOrderAddVo);
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_ORDER_ADD_URL, miniAccessToken);
//        String postStringData = restTemplateUtil.postStringData(url, JSONObject.toJSONString(orderAddMap));
//        JSONObject jsonObject = JSONObject.parseObject(postStringData);
//        WxUtil.checkResult(jsonObject);
//        ShopOrderAddResultVo resultVo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ShopOrderAddResultVo.class);
//        return resultVo;
//    }
//
//
//    /**
//     * 自定义交易组件 获取支付参数
//     *
//     * @param requestVo 参数
//     * @return 获取的支付参数
//     */
//    @Override
//    public ShopOrderGetPaymentParamsRersponseVo shopOrderGetPaymentParams(ShopOrderGetPaymentParamsRequestVo requestVo) {
//
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_ORDER_PAYMENT_PARAMS_URL, miniAccessToken);
//        String postStringData = restTemplateUtil.postStringData(url, JSONObject.toJSONString(requestVo));
//        JSONObject jsonObject = JSONObject.parseObject(postStringData);
//        WxUtil.checkResult(jsonObject);
//        ShopOrderGetPaymentParamsRersponseVo result  = JSONObject.parseObject(jsonObject.getJSONObject("payment_params").toJSONString(), ShopOrderGetPaymentParamsRersponseVo.class);
//        return result;
//    }
//
//    private Map<String, Object> assembleOrderAddMap(ShopOrderAddVo shopOrderAddVo) {
//        Map<String, Object> map = CollUtil.newHashMap();
//        map.put("create_time", shopOrderAddVo.getCreateTime());
//        map.put("out_order_id", shopOrderAddVo.getOutOrderId());
//        map.put("openid", shopOrderAddVo.getOpenid());
//        map.put("path", shopOrderAddVo.getPath());
//        map.put("fund_type", shopOrderAddVo.getFund_type());
//        map.put("expire_time", shopOrderAddVo.getExpire_time());
//        // 订单详情
//        ShopOrderDetailAddVo orderDetail = shopOrderAddVo.getOrderDetail();
//        Map<String, Object> detailMap = CollUtil.newHashMap();
//        // ——商品详情数组
//        List<ShopOrderProductInfoAddVo> productInfos = orderDetail.getProductInfos();
//        List<Map<String, Object>> proInfoMapList = productInfos.stream().map(productInfo -> {
//            Map<String, Object> proInfoMap = CollUtil.newHashMap();
//            proInfoMap.put("out_product_id", productInfo.getOutProductId());
//            proInfoMap.put("out_sku_id", productInfo.getOutSkuId());
////            proInfoMap.put("sku_real_price", productInfo.getSkuRealPrice()); //暂时先设置 sku_real_price sale_price 一致
//            proInfoMap.put("sku_real_price", productInfo.getSalePrice()); //暂时先设置 sku_real_price sale_price 一致
//            proInfoMap.put("sale_price", productInfo.getSalePrice());
//            proInfoMap.put("product_cnt", productInfo.getProductCnt());
//            proInfoMap.put("head_img", productInfo.getHeadImg());
//            proInfoMap.put("title", productInfo.getTitle());
//            proInfoMap.put("path", productInfo.getPath());
//            return proInfoMap;
//        }).collect(Collectors.toList());
//        detailMap.put("product_infos", proInfoMapList);
//        // ——支付详情
//        ShopOrderPayInfoAddVo payInfo = orderDetail.getPayInfo();
//        Map<String, Object> payInfoMap = CollUtil.newHashMap();
//        payInfoMap.put("pay_method", payInfo.getPayMethod());
//        payInfoMap.put("pay_method_type", payInfo.getPayMethodType());
//        payInfoMap.put("prepay_id", payInfo.getPrepayId());
//        payInfoMap.put("prepay_time", payInfo.getPrepayTime());
//        detailMap.put("pay_info", payInfoMap);
//        // ——价格详情
//        ShopOrderPriceInfoVo priceInfo = orderDetail.getPriceInfo();
//        Map<String, Object> priceInfoMap = CollUtil.newHashMap();
//        priceInfoMap.put("order_price", priceInfo.getOrderPrice());
//        priceInfoMap.put("freight", priceInfo.getFreight());
//        if (ObjectUtil.isNotNull(priceInfo.getDiscountedPrice()) && priceInfo.getDiscountedPrice() > 0) {
//            priceInfoMap.put("discounted_price", priceInfo.getDiscountedPrice());
//        }
//        detailMap.put("price_info", priceInfoMap);
//        map.put("order_detail", detailMap);
//
//        // 交付详情
//        ShopOrderDeliveryDetailAddVo deliveryDetail = shopOrderAddVo.getDeliveryDetail();
//        Map<String, Object> deliveryMap = CollUtil.newHashMap();
//        deliveryMap.put("delivery_type", deliveryDetail.getDeliveryType());
//        map.put("delivery_detail", deliveryMap);
//
//        // 地址详情
//        ShopOrderAddressInfoAddVo addressInfo = shopOrderAddVo.getAddressInfo();
//        Map<String, Object> addressMap = CollUtil.newHashMap();
//        addressMap.put("receiver_name", addressInfo.getReceiverName());
//        addressMap.put("detailed_address", addressInfo.getDetailedAddress());
//        addressMap.put("tel_number", addressInfo.getTelNumber());
//        map.put("address_info", addressMap);
//
//        return map;
//    }
//
//    /**
//     * 同步订单支付结果 视频号场景忽略此接口 微信方更新
//     * @return Boolean
//     */
//    @Override
//    public Boolean shopOrderPay(ShopOrderPayVo shopOrderPayVo) {
//        if (ObjectUtil.isNull(shopOrderPayVo.getOrderId()) && StrUtil.isBlank(shopOrderPayVo.getOutOrderId())) {
//            throw new CrmebException("订单ID不能为空");
//        }
//        Map<String, Object> payMap = assembleOrderPayMap(shopOrderPayVo);
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_ORDER_PAY_URL, miniAccessToken);
//        String postStringData = restTemplateUtil.postStringData(url, JSONObject.toJSONString(payMap));
//        JSONObject jsonObject = JSONObject.parseObject(postStringData);
//        WxUtil.checkResult(jsonObject);
//        return Boolean.TRUE;
//    }
//
//    private Map<String, Object> assembleOrderPayMap(ShopOrderPayVo shopOrderPayVo) {
//        Map<String, Object> map = CollUtil.newHashMap();
//        if (ObjectUtil.isNotNull(shopOrderPayVo.getOrderId())) {
//            map.put("order_id", shopOrderPayVo.getOrderId());
//        }
//        if (StrUtil.isNotBlank(shopOrderPayVo.getOutOrderId())) {
//            map.put("out_order_id", shopOrderPayVo.getOutOrderId());
//        }
//        map.put("openid", shopOrderPayVo.getOpenid());
//        map.put("action_type", shopOrderPayVo.getActionType());
//        if (StrUtil.isNotBlank(shopOrderPayVo.getActionRemark())) {
//            map.put("action_remark", shopOrderPayVo.getActionRemark());
//        }
//        if (shopOrderPayVo.getActionType().equals(1)) {
//            map.put("transaction_id", shopOrderPayVo.getTransactionId());
//            map.put("pay_time", shopOrderPayVo.getPayTime());
//        }
//        return map;
//    }
//
//    /**
//     * 获取订单详情
//     * @return ShopOrderVo
//     */
//    @Override
//    public ShopOrderVo shopOrderGet(ShopOrderCommonVo shopOrderCommonVo) {
//        if (ObjectUtil.isNull(shopOrderCommonVo.getOrderId()) && StrUtil.isBlank(shopOrderCommonVo.getOutOrderId())) {
//            throw new CrmebException("订单ID不能为空");
//        }
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_ORDER_GET_URL, miniAccessToken);
//        Map<String, Object> map = new HashMap<>();
//        if (ObjectUtil.isNotNull(shopOrderCommonVo.getOrderId())) {
//            map.put("order_id", shopOrderCommonVo.getOrderId());
//        }
//        if (StrUtil.isNotBlank(shopOrderCommonVo.getOutOrderId())) {
//            map.put("out_order_id", shopOrderCommonVo.getOutOrderId());
//        }
//        map.put("openid", shopOrderCommonVo.getOpenid());
//        String mapData = restTemplateUtil.postMapData(url, map);
//        JSONObject jsonObject = JSONObject.parseObject(mapData);
//        WxUtil.checkResult(jsonObject);
//        ShopOrderVo shopOrderVo = JSONObject.parseObject(jsonObject.getJSONObject("order").toJSONString(), ShopOrderVo.class);
//        return shopOrderVo;
//    }
}
