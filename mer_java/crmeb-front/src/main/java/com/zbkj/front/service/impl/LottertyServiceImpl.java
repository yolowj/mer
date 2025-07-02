package com.zbkj.front.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.prize.LotteryRecord;
import com.zbkj.common.model.prize.PrizeDraw;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserAddress;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.request.CreateFreeOrderRequest;
import com.zbkj.common.request.OrderMerchantRequest;
import com.zbkj.common.response.OrderNoResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.vo.*;
import com.zbkj.front.service.FrontOrderService;
import com.zbkj.front.service.LotteryService;
import com.zbkj.service.dao.PrizeDrawDao;
import com.zbkj.service.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class LottertyServiceImpl implements LotteryService {

    @Resource
    private PrizeDrawDao prizeDrawDao;
    @Autowired
    private ProductService productService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private LotteryRecordService lotteryRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private CartService cartService;
    @Autowired
    private AsyncService asyncService;

    private FrontOrderService frontOrderService;


    @Override
    public void supply(Integer id) {
        LotteryRecord lotteryRecord = lotteryRecordService.getOne(new LambdaQueryWrapper<LotteryRecord>().eq(LotteryRecord::getPrizeId, id));

        if (ObjectUtil.isNotNull(lotteryRecord)) {
            if (lotteryRecord.getStatus() == 1) {
                throw new CrmebException("奖品已被领取");
            }
            if (lotteryRecord.getStatus() == 2) {
                throw new CrmebException("奖品已过期");
            }

            PrizeDraw prizeDraw = prizeDrawDao.selectById(lotteryRecord.getPrizeId());
            Boolean execute;

            // 1：商品，2：优惠券，3：积分，4：谢谢惠顾
            if (prizeDraw.getType() == 1) {
                Product product = productService.getById(lotteryRecord.getPrizeId());
                if (product.getStock() <= 0) {
                    throw new CrmebException("商品库存不足，请联系管理员补库存~！");
                }
                LambdaQueryWrapper<UserAddress> lqw = Wrappers.lambdaQuery();
                lqw.select(UserAddress::getUid);
                lqw.eq(UserAddress::getIsDefault, Boolean.TRUE);

                UserAddress userAddress = userAddressService.getDefaultByUid(lotteryRecord.getUserId());
                if (ObjectUtil.isNull(userAddress) || userAddress.getIsDel()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "默认收货地址有误");
                }

                //商品领取
                // handleProduct(prizeDraw,lotteryRecord);
            } else if (prizeDraw.getType() == 2) {
                Coupon coupon = couponService.getById(lotteryRecord.getPrizeId());
                if (coupon.getLastTotal() <= 0) {
                    throw new CrmebException("优惠券库存不足，请联系管理员补库存~！");
                }
                //优惠券领取
                execute = couponUserService.receiveCouponLot(coupon.getId());


            } else if (prizeDraw.getType() == 3) {
                User user = userService.getById(lotteryRecord.getUserId());
                //积分领取
                execute = transactionTemplate.execute(e -> {
                    userService.updateIntegral(user.getIntegral(), prizeDraw.getValue(), Constants.OPERATION_TYPE_ADD);
                    UserIntegralRecord integralRecord = new UserIntegralRecord();
                    integralRecord.setUid(user.getId());
                    integralRecord.setLinkId("0");
                    integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_LOTTERY);
                    integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                    integralRecord.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_LOTTERY);
                    integralRecord.setMark(StrUtil.format("抽奖奖励{}积分", prizeDraw.getValue()));
                    integralRecord.setIntegral(prizeDraw.getValue());
                    integralRecord.setBalance(prizeDraw.getValue() + user.getIntegral());
                    integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
                    userIntegralRecordService.save(integralRecord);
                    return true;
                });
            } else if (prizeDraw.getType() == 4) {
                // 4：谢谢惠顾
            }


        } else {
            throw new CrmebException("抽奖记录不存在");
        }
    }


    /**
     * 创建抽奖订单
     *
     * @param orderRequest 下单请求对象
     * @param orderInfoVo  预下单缓存对象
     * @param user         用户信息
     */

    public OrderNoResponse createOrderFree(CreateFreeOrderRequest orderRequest, FreeOrderInfoVo orderInfoVo, User user) {


        UserAddress userAddress = null;
        OrderMerchantRequest orderMerchantRequest = orderRequest.getOrderMerchantRequest();

        if (orderMerchantRequest.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS)) {
            if (ObjectUtil.isNull(orderRequest.getAddressId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择收货地址");
            }
            userAddress = userAddressService.getById(orderRequest.getAddressId());
            if (ObjectUtil.isNull(userAddress) || userAddress.getIsDel()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "收货地址有误");
            }
        }
        if (orderMerchantRequest.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
            Merchant merchant = merchantService.getByIdException(orderMerchantRequest.getMerId());
            if (!merchant.getIsTakeTheir()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}没有配置店铺地址，请联系客服", merchant.getName()));
            }
        }


        PreMerchantOrderVo merchantOrderVo = orderInfoVo.getMerchantOrderVo();
        merchantOrderVo.setShippingType(orderMerchantRequest.getShippingType());
        merchantOrderVo.setUserCouponId(orderMerchantRequest.getUserCouponId());
        merchantOrderVo.setCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setMerCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setUseIntegral(0);
        merchantOrderVo.setIntegralPrice(BigDecimal.ZERO);
        merchantOrderVo.setCouponFee(BigDecimal.ZERO);
        PreOrderInfoDetailVo detailVo = merchantOrderVo.getOrderInfoList().get(0);

        // 校验商品库存
        MyRecord skuRecord = validateProductStock(orderInfoVo);

        orderRequest.getOrderMerchantRequest().setShippingType(orderMerchantRequest.getShippingType());
        orderRequest.getOrderMerchantRequest().setMerId(orderMerchantRequest.getMerId());



        // 计算订单各种价格
        //  frontOrderService.getFreightFee_V_1_8(orderInfoVo, userAddress, false);


        // 平台订单
        Order order = new Order();
        String orderNo = CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_PLATFORM);
        order.setOrderNo(orderNo);
        order.setMerId(0);
        order.setUid(user.getId());
        order.setTotalNum(orderInfoVo.getOrderProNum());
        order.setProTotalPrice(orderInfoVo.getProTotalFee());
        order.setTotalPostage(orderInfoVo.getFreightFee());
        order.setTotalPrice(order.getProTotalPrice().add(order.getTotalPostage()));
        order.setCouponPrice(orderInfoVo.getCouponFee());
        order.setUseIntegral(merchantOrderVo.getUseIntegral());
        order.setIntegralPrice(merchantOrderVo.getIntegralPrice());
        order.setPayPrice(order.getProTotalPrice().add(order.getTotalPostage()).subtract(order.getCouponPrice()).subtract(order.getIntegralPrice()));
        order.setPayPostage(order.getTotalPostage());
        order.setPaid(false);
        order.setCancelStatus(OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
        order.setLevel(OrderConstants.ORDER_LEVEL_PLATFORM);
        order.setType(orderInfoVo.getType());
        order.setSecondType(orderInfoVo.getSecondType());
        order.setSystemFormId(orderInfoVo.getSystemFormId());
        order.setOrderExtend(StrUtil.isNotBlank(orderRequest.getOrderExtend()) ? systemAttachmentService.clearPrefix(orderRequest.getOrderExtend(), systemAttachmentService.getCdnUrl()) : "");

        // 商户订单
        // 秒杀只会有一个商户订单
        MerchantOrder merchantOrder = new MerchantOrder();
        merchantOrder.setOrderNo(order.getOrderNo());
        merchantOrder.setMerId(merchantOrderVo.getMerId());
        merchantOrder.setUid(user.getId());
        if (StrUtil.isNotBlank(orderMerchantRequest.getRemark())) {
            merchantOrder.setUserRemark(orderMerchantRequest.getRemark());
        }
        merchantOrder.setShippingType(orderMerchantRequest.getShippingType());
        if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)
                || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) {
            merchantOrder.setRealName(user.getNickname());
            merchantOrder.setUserPhone(user.getPhone());
            merchantOrder.setUserAddress("");
        } else if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
            merchantOrder.setUserAddress(merchantOrderVo.getMerName());
            merchantOrder.setVerifyCode(String.valueOf(CrmebUtil.randomCount(1111111111, 999999999)));
        } else {
            merchantOrder.setRealName(userAddress.getRealName());
            merchantOrder.setUserPhone(userAddress.getPhone());
            String userAddressStr = userAddress.getProvince() + userAddress.getCity() + userAddress.getDistrict() + userAddress.getStreet() + userAddress.getDetail();
            merchantOrder.setUserAddress(userAddressStr);
        }
        merchantOrder.setTotalNum(merchantOrderVo.getProTotalNum());
        merchantOrder.setProTotalPrice(merchantOrderVo.getProTotalFee());
        merchantOrder.setTotalPostage(merchantOrderVo.getFreightFee());
        merchantOrder.setTotalPrice(merchantOrder.getProTotalPrice().add(merchantOrder.getTotalPostage()));
        merchantOrder.setPayPostage(merchantOrder.getTotalPostage());
        merchantOrder.setUseIntegral(merchantOrderVo.getUseIntegral());
        merchantOrder.setIntegralPrice(merchantOrderVo.getIntegralPrice());
        merchantOrder.setCouponId(merchantOrderVo.getUserCouponId());
        merchantOrder.setCouponPrice(merchantOrderVo.getCouponFee());
        merchantOrder.setPayPrice(merchantOrder.getTotalPrice().subtract(merchantOrder.getCouponPrice()).subtract(merchantOrder.getIntegralPrice()));
        merchantOrder.setGainIntegral(0);
        merchantOrder.setType(order.getType());
        merchantOrder.setSecondType(order.getSecondType());


        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderNo(order.getOrderNo());
        orderDetail.setMerId(merchantOrder.getMerId());
        orderDetail.setUid(user.getId());
        orderDetail.setProductId(detailVo.getProductId());
        orderDetail.setProductName(detailVo.getProductName());
        orderDetail.setImage(detailVo.getImage());
        orderDetail.setAttrValueId(detailVo.getAttrValueId());
        orderDetail.setSku(detailVo.getSku());
        orderDetail.setPrice(detailVo.getPrice());
        orderDetail.setPayNum(detailVo.getPayNum());
        orderDetail.setWeight(detailVo.getWeight());
        orderDetail.setVolume(detailVo.getVolume());
        orderDetail.setProductType(detailVo.getProductType());
        orderDetail.setProductMarketingType(detailVo.getProductMarketingType());
        orderDetail.setSubBrokerageType(detailVo.getSubBrokerageType());
        orderDetail.setBrokerage(detailVo.getBrokerage());
        orderDetail.setBrokerageTwo(detailVo.getBrokerageTwo());
        orderDetail.setFreightFee(detailVo.getFreightFee());
        orderDetail.setCouponPrice(detailVo.getCouponPrice());
        orderDetail.setUseIntegral(detailVo.getUseIntegral());
        orderDetail.setIntegralPrice(detailVo.getIntegralPrice());
        orderDetail.setPayPrice(BigDecimal.ZERO);
        BigDecimal detailPayPrice = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getPayNum().toString())).add(orderDetail.getFreightFee()).subtract(orderDetail.getCouponPrice()).subtract(orderDetail.getIntegralPrice());
        if (detailPayPrice.compareTo(BigDecimal.ZERO) >= 0) {
            orderDetail.setPayPrice(detailPayPrice);
        }
        orderDetail.setProRefundSwitch(detailVo.getProRefundSwitch());
        order.setCreateTime(DateUtil.date());

        Boolean execute = transactionTemplate.execute(e -> {
            // 普通商品口库存
            Boolean result = productService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
            if (!result) {
                e.setRollbackOnly();
                log.error("生成订单扣减商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                return result;
            }
            // 普通商品规格扣库存
            result = productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("type"), skuRecord.getInt("attrValueVersion"));
            if (!result) {
                e.setRollbackOnly();
                log.error("生成订单扣减商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                return result;
            }
            orderService.save(order);
            merchantOrderService.save(merchantOrder);
            orderDetailService.save(orderDetail);
            // 扣除用户积分
            if (order.getUseIntegral() > 0) {
                result = userService.updateIntegral(user.getId(), order.getUseIntegral(), Constants.OPERATION_TYPE_SUBTRACT);
                if (!result) {
                    e.setRollbackOnly();
                    log.error("生成订单扣除用户积分失败,预下单号：{}", orderRequest.getPreOrderNo());
                    return result;
                }
                UserIntegralRecord userIntegralRecord = frontOrderService.initOrderUseIntegral(user.getId(), order.getUseIntegral(), user.getIntegral(), order.getOrderNo());
                userIntegralRecordService.save(userIntegralRecord);
            }

            // 生成订单日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_CREATE, OrderStatusConstants.ORDER_LOG_MESSAGE_CREATE);
            // 清除购物车数据
            if (CollUtil.isNotEmpty(orderInfoVo.getCartIdList())) {
                cartService.deleteCartByIds(orderInfoVo.getCartIdList());
            }
            // 积分订单预扣除积分
            if (order.getRedeemIntegral() > 0) {
                result = userService.updateIntegral(user.getId(), order.getRedeemIntegral(), Constants.OPERATION_TYPE_SUBTRACT);
                if (!result) {
                    e.setRollbackOnly();
                    log.error("生成积分订单预扣除用户积分失败,预下单号：{}", orderRequest.getPreOrderNo());
                    return result;
                }
                UserIntegralRecord userIntegralRecord = frontOrderService.initIntegralOrderUseIntegral(user.getId(), order.getRedeemIntegral(), user.getIntegral(), order.getOrderNo());
                userIntegralRecordService.save(userIntegralRecord);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("订单生成失败");
        }


        Boolean result = asyncService.manyMerchantOrderProcessing(order, Arrays.asList(merchantOrder));
        if (!result) {
            throw new CrmebException("订单生成失败");
        }


        OrderNoResponse response = new OrderNoResponse();
        response.setOrderNo(order.getOrderNo());
        response.setPayPrice(order.getPayPrice());
        return response;
    }



    private void handleProduct(PrizeDraw prizeDraw, LotteryRecord lotteryRecord, Product product) {
        CreateFreeOrderRequest orderRequest = new CreateFreeOrderRequest();
        //创建订单
        orderRequest.setAddressId(userAddressService.getDefaultByUid(lotteryRecord.getUserId()).getId());


        OrderMerchantRequest merchantRequest = new OrderMerchantRequest();
        merchantRequest.setMerId(0); // 平台订单

        if (product.getDeliveryMethod().equals("2")) {
            merchantRequest.setShippingType(2);
        } else {
            merchantRequest.setShippingType(1);
        }
        orderRequest.setOrderMerchantRequest(merchantRequest);
        orderRequest.setIsUseIntegral(false);
        orderRequest.setPlatUserCouponId(0);
        orderRequest.setSystemFormId(product.getSystemFormId());
        orderRequest.setOrderExtend(null);


        FreeOrderInfoVo orderInfoVo = new FreeOrderInfoVo();
        User user = new User();
        createOrderFree(orderRequest, orderInfoVo, user);
    }


    private MyRecord validateProductStock(FreeOrderInfoVo orderInfoVo) {

        // 普通商品
        PreMerchantOrderVo merchantOrderVo = orderInfoVo.getMerchantOrderVo();

        Merchant merchant = merchantService.getByIdException(merchantOrderVo.getMerId());
        if (!merchant.getIsSwitch()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户已关闭，请重新下单");
        }

        PreOrderInfoDetailVo info = merchantOrderVo.getOrderInfoList().get(0);

        // 查询商品信息
        Product product = productService.getById(info.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品信息不存在");
        }
        if (!product.getIsShow()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品已下架");
        }
        if (product.getStock().equals(0) || info.getPayNum() > product.getStock()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
        }
        // 查询商品规格属性值信息
        ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(
                info.getAttrValueId(), info.getProductId(), product.getType(), product.getMarketingType());
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息不存在");
        }
        if (!attrValue.getIsShow()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息无效");
        }
        if (attrValue.getStock() < info.getPayNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
        }

        MyRecord record = new MyRecord();
        record.set("productId", info.getProductId());
        record.set("num", info.getPayNum());
        record.set("attrValueId", info.getAttrValueId());
        record.set("attrValueVersion", attrValue.getVersion());
        record.set("type", attrValue.getType());
        record.set("marketingType", attrValue.getMarketingType());
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)) {
            record.set("expand", attrValue.getExpand());
        }
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
            record.set("cdkeyId", attrValue.getCdkeyId());
        }
        record.set("isPaidMember", product.getIsPaidMember());
        record.set("vipPrice", attrValue.getVipPrice());


        return record;
    }

}

