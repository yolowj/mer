package com.zbkj.service.util;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.merchant.MerchantPrint;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.response.YlyAccessTokenResponse;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.vo.PrintContentVo;
import com.zbkj.service.service.*;
import com.zbkj.service.util.yly.RequestMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;


/** 易联云 工具类
 * +----------------------------------------------------------------------
 *  * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  * +----------------------------------------------------------------------
 *  * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  * +----------------------------------------------------------------------
 *  * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  * +----------------------------------------------------------------------
 *  * | Author: CRMEB Team <admin@crmeb.com>
 *  * +----------------------------------------------------------------------
 **/
@Component
public class PrintUtil {
    private static final Logger logger = LoggerFactory.getLogger(PrintUtil.class);
    @Autowired
    private MerchantPrintService merchantPrintService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private feiEYun feiEYun;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 初始化易联云打印机并链接
     * 添加打印机
     * 参数：* @param machine_code 易联云打印机终端号
     *      * @param msign 易联云打印机终端密钥
     *      * @param access_token 授权的token 必要参数，有效时间35天
     */
    public YlyAccessTokenResponse instantYly(MerchantPrint print) {
        YlyAccessTokenResponse ylyAccessTokenResponse = null;

        try {

            // 初始化易联云
            RequestMethod.init(print.getPrintYlyAppid(), print.getPrintYlySec());
                ylyAccessTokenResponse = JSON.parseObject(RequestMethod.getAccessToken(),YlyAccessTokenResponse.class);
        }catch (Exception e){
            logger.error("添加易联云打印机失败"+e.getMessage());
            logger.error("易联云 配置参数 {}", JSON.toJSONString(print));
        }
        return ylyAccessTokenResponse;
    }

    /**
     * 文本打印
     * 参数：* @param access_token 授权的token 必要参数
     *      * @param machine_code 易联云打印机终端号
     *      * @param content 打印内容(需要urlencode)，排版指令详见打印机指令
     *      * @param origin_id 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母 ，且在同一个client_id下唯一。详见商户订单号
     *      * @param op 小票打印开关：0关闭，1=手动打印，2=自动打印，3=自动和手动
     *   String printContent = "一段美好的文字";
     */
    public void printTicket(MerchantOrder merchantOrder) {
        Merchant merchant = merchantService.getByIdException(merchantOrder.getMerId());
        // 在这里初始化商家打印机配置信息
        List<MerchantPrint> byMerIdAndStatusOn = merchantPrintService.getByMerIdAndStatusOn(merchant.getId());
        // 循环获取用户多个打印配置
        for (MerchantPrint merchantPrint : byMerIdAndStatusOn) {
            // 判断是否当前商户的小票打印
            if(!merchantOrder.getMerId().equals(merchantPrint.getMerId())) continue;
            if (merchantPrint.getPrintType() == 0) {
                // 易联云配置
                ylyPrint(merchantOrder, merchantPrint, merchant);
            } else if (merchantPrint.getPrintType() == 1) {
                // 飞蛾云
                feiEYun.print(merchantOrder, merchantPrint, merchant);
            }
        }
    }

    public void batchPrintTicket(List<MerchantOrder> merchantOrderList) {
        for (MerchantOrder order : merchantOrderList) {
            Merchant merchant = merchantService.getByIdException(order.getMerId());
            // 在这里初始化商家打印机配置信息
            List<MerchantPrint> byMerIdAndStatusOn = merchantPrintService.getByMerIdAndStatusOn(merchant.getId());
            // 循环获取用户多个打印配置
            for (MerchantPrint merchantPrint : byMerIdAndStatusOn) {
                // 判断是否当前商户的下票打印
                if(!order.getMerId().equals(merchantPrint.getMerId())) continue;
                if (merchantPrint.getPrintType() == 0) {
                    // 易联云配置
                    ylyPrint(order, merchantPrint, merchant);
                } else if (merchantPrint.getPrintType() == 1) {
                    // 飞蛾云
                    feiEYun.print(order, merchantPrint, merchant);
                }
            }
        }
    }

    /**
     * 易联云打印
     * @param order     订单信息
     * @param merchantPrint 商户打印配置信息
     */
    public void ylyPrint(MerchantOrder order, MerchantPrint merchantPrint, Merchant merchant) {
        // 初始化易联云打印机配置信息
        YlyAccessTokenResponse tokenResponse = instantYly(merchantPrint);
        try {
            // 组装打印内容
            StringBuilder printSb = new StringBuilder();
            if (StrUtil.isBlank(merchantPrint.getContent())) {
                printSb.append("<FH><FB><center>"+merchant.getName()+"</center></FB></FH>");
                printSb.append("<FH>订单编号:" + order.getOrderNo()+"\n");
                printSb.append("日   期:" + CrmebDateUtil.dateToStr(order.getCreateTime(), DateConstants.DATE_FORMAT)+"\n");
                printSb.append("电   话:" + (ObjectUtil.isEmpty(order.getUserPhone()) ?"-":order.getUserPhone())+"\n");
                printSb.append("姓   名:" + (ObjectUtil.isEmpty(order.getRealName())?"-":order.getRealName())+"\n");
                if(order.getShippingType().equals(1)){ // 仅核销订单不打印地址
                    printSb.append("地   址:" + order.getUserAddress()+"\n");
                }
                printSb.append("订单备注:"+ order.getUserRemark()+"</FH>\n");
                printSb.append("********************************");
                printSb.append("<FH>商品名称 单价 数量 金额\n");
                printSb.append("********************************");
                printSb.append("<FH>"+printFormatGoodsList(orderDetailService.getShipmentByOrderNo(order.getOrderNo()))+"</FH>");
                printSb.append("********************************\n");
                printSb.append("<FH>");
                printSb.append("<LR>赠送积分:"+order.getGainIntegral()+"</LR>");
                printSb.append("<LR>合计:"+ order.getProTotalPrice()+"元，优惠:"+order.getCouponPrice()+"元</LR>");
                printSb.append("<LR>邮费:"+order.getTotalPostage()+"元，抵扣:"+order.getIntegralPrice()+"元</LR>");
                printSb.append("</FH>");
                printSb.append("<FH><right>实际支付:"+ order.getPayPrice() +"元</right></FH>");
                printSb.append("<FB><FB><center>完</center></FB></FB>");
            } else {
                PrintContentVo printContentVo = JSON.parseObject(merchantPrint.getContent(), PrintContentVo.class);
                if (printContentVo.getSmallTickerHeader().equals(1)) {
                    printSb.append("<FH><FB><center>"+merchant.getName()+"</center></FB></FH>");
                    printSb.append("———————————————\n");
                }
                if (printContentVo.getDeliveryInfo().equals(1)) {
                    if(order.getShippingType().equals(1)){ // 仅核销订单不打印地址
                        printSb.append("配送方式:商家配送\n");
                    } else if (order.getShippingType().equals(2)) {
                        printSb.append("配送方式:门店自提\n");
                    } else {
                        printSb.append("配送方式:虚拟发货\n");
                    }
                    printSb.append("客户姓名:" + (ObjectUtil.isEmpty(order.getRealName())?"-":order.getRealName())+"\n");
                    printSb.append("客户电话:" + (ObjectUtil.isEmpty(order.getUserPhone()) ?"-":order.getUserPhone())+"\n");
                    if(order.getShippingType().equals(1)){ // 仅核销订单不打印地址
                        printSb.append("收货地址:" + order.getUserAddress()+"\n");
                    }
                    printSb.append("———————————————\n");
                }
                if (printContentVo.getBuyerRemark().equals(1)) {
                    printSb.append("买家备注:" + order.getUserRemark() +"\n");
                    printSb.append("———————————————\n");
                }
                if (printContentVo.getProductInfo().equals(1)) {
                    printSb.append("********************************");
                    printSb.append("<FH>商品名称 单价 数量 金额\n");
                    printSb.append("********************************");
                    printSb.append("<FH>"+printFormatGoodsList(orderDetailService.getShipmentByOrderNo(order.getOrderNo()))+"</FH>");
                    printSb.append("********************************\n");
                }
                if (printContentVo.getFreightInfo().equals(1)) {
                    printSb.append("<right>运费:"+ order.getPayPostage().toString() +"元\n</right>");
                }
                if (printContentVo.getDiscountInfo().equals(1)) {
                    printSb.append("<right>优惠:-"+ order.getCouponPrice().add(order.getSvipDiscountPrice()) +"元\n</right>");
                    printSb.append("<right>抵扣:-"+ order.getIntegralPrice().toString() +"元\n</right>");
                    if (order.getIsSvip() && order.getSvipDiscountPrice().compareTo(BigDecimal.ZERO) > 0) {
                        printSb.append("<right>会员优惠:-"+ order.getSvipDiscountPrice() +"元\n</right>");
                    }
                    printSb.append("———————————————\n");
                }
                if (CollUtil.isNotEmpty(printContentVo.getPayInfo())) {
                    if (printContentVo.getPayInfo().contains(1)) {
                        Order merchatOrder = orderService.getByOrderNo(order.getOrderNo());
                        String payType = "";
                        switch (merchatOrder.getPayType()) {
                            case "weixin":
                                payType = "微信支付";
                                break;
                            case "alipay":
                                payType = "支付宝支付";
                                break;
                            case "yue":
                                payType = "余额支付";
                                break;
                        }
                        printSb.append("<right>支付方式:"+ payType +"\n</right>");
                    }
                    if (printContentVo.getPayInfo().contains(2)) {
                        printSb.append("<right>实收金额:"+ order.getPayPrice().toString() +"元\n</right>");
                    }
                    printSb.append("———————————————\n");
                }
                if (CollUtil.isNotEmpty(printContentVo.getOrderInfo())) {
                    if (printContentVo.getOrderInfo().contains(1)) {
                        printSb.append("订单编号:" + order.getOrderNo() +"\n");
                    }
                    if (printContentVo.getOrderInfo().contains(2)) {
                        printSb.append("下单时间:" + DateUtil.format(order.getCreateTime(), DateConstants.DATE_FORMAT) +"\n");
                    }
                    if (printContentVo.getOrderInfo().contains(3)) {
                        Order merchatOrder = orderService.getByOrderNo(order.getOrderNo());
                        printSb.append("支付时间:" + DateUtil.format(merchatOrder.getPayTime(), DateConstants.DATE_FORMAT) +"\n");
                    }
                    if (printContentVo.getOrderInfo().contains(4)) {
                        printSb.append("打印时间:" + DateUtil.date().toString("yyyy-MM-dd HH:mm:ss") +"\n");
                    }
                    printSb.append("———————————————\n");
                }
                if (printContentVo.getCodeUrlSwitch().equals(1) && StrUtil.isNotBlank(printContentVo.getCodeUrl())) {
                    String codeUrl = printContentVo.getCodeUrl();
                    if (codeUrl.length() > 5) {
                        String substring = codeUrl.substring(0, 5);
                        if (substring.contains("page")) {
                            codeUrl = "http://" + systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_SITE_URL) + codeUrl;
                        }
                    }
                    printSb.append("<QR>" + codeUrl + "</QR>");
                }
                if (printContentVo.getBottomNoticeSwitch().equals(1) && StrUtil.isNotBlank(printContentVo.getBottomNotice())) {
                    printSb.append("<CA>" + printContentVo.getBottomNotice() + "</CA>");
                }
            }
            RequestMethod.getInstance().printIndex(
                    tokenResponse.getBody().getAccess_token(),
                    merchantPrint.getPrintYlyMerchineNo(),
                    URLEncoder.encode(printSb.toString(), "utf-8"),"order111");
        } catch (Exception e) {
            logger.error("易联云打印小票失败:"+ e);
        }
    }

    /**
     * 格式化商品详情打印格式
     * @param detailList 待格式化的商品详情
     * @return 格式化后的商品详情
     */
    public String printFormatGoodsList(List<OrderDetail> detailList){
        StringBuilder printGoodsString = new StringBuilder();
        for (OrderDetail orderDetail : detailList) {
            String LastGoodsName = orderDetail.getProductName();
            if(StringUtils.isNotBlank(LastGoodsName) && LastGoodsName.length() > 10){
                LastGoodsName = LastGoodsName.substring(0,8);
            }
            printGoodsString.append(LastGoodsName);
            printGoodsString.append(" ").append(orderDetail.getPrice());
            printGoodsString.append(" ").append(orderDetail.getPayNum());
            printGoodsString.append(" ").append(orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getPayNum()))).append("\n");
        }
        return printGoodsString.toString();
    }

}
