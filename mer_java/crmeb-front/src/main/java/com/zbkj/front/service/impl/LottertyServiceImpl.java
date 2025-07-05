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
                User user = userService.getById(lotteryRecord.getUserId());
                orderService.orderFree(product, user);

            } else if (prizeDraw.getType() == 2) {
                Coupon coupon = couponService.getById(lotteryRecord.getPrizeId());
                if (coupon.getLastTotal() <= 0) {
                    throw new CrmebException("优惠券库存不足，请联系管理员补库存~！");
                }
                couponUserService.receiveCouponLot(coupon.getId());
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

}

