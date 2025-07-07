package com.zbkj.front.controller;

import com.zbkj.common.annotation.LogControllerAnnotation;
import com.zbkj.common.enums.MethodType;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.prize.*;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.response.LotteryResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.front.service.LotteryService;
import com.zbkj.service.service.CouponService;
import com.zbkj.service.service.PrizeDrawService;
import com.zbkj.service.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 抽奖设置 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/front/prize")
@Api(tags = "抽奖设置") //配合swagger使用

public class LotteryController {

    @Autowired
    private PrizeDrawService prizeDrawService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CouponService couponService;

    @Autowired
    private LotteryService lotteryService;

    /**
     * 分页显示抽奖记录表
     * @param prizeDrawRequest 分页参数
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "抽奖记录") //配合swagger使用
    @RequestMapping(value = "/record", method = RequestMethod.POST)
    public CommonResult<CommonPage<LotteryRecord>> getList(@Validated PrizeDrawRequest prizeDrawRequest) {
        CommonPage<LotteryRecord> lotteryRecordCommonPage = CommonPage.restPage(prizeDrawService.getLotteryRecordList(prizeDrawRequest));
        lotteryRecordCommonPage.getList().forEach(lotteryRecord -> {

            if (lotteryRecord.getPrizeType() == 1) {
                Product product = productService.getById(lotteryRecord.getPrizeValue());
                if (product != null) {
                    lotteryRecord.setProductName(product.getName());
                }
            }
            if (lotteryRecord.getPrizeType() == 2) {
                Coupon coupon = couponService.getById(lotteryRecord.getPrizeValue());
                if (coupon != null) {
                    lotteryRecord.setCouponName(coupon.getName());
                }
            } else if (lotteryRecord.getPrizeType() == 3) {
                lotteryRecord.setProductName(lotteryRecord.getPrizeValue() + "积分");
            }else if (lotteryRecord.getPrizeType() == 3) {
                lotteryRecord.setProductName("谢谢惠顾");
            }
        });
        return CommonResult.success(lotteryRecordCommonPage);
    }

      @ApiOperation(value = "奖品类标配")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<List<PrizeDraw>> getPageInfo() {
          List<PrizeDraw> prizeDrawList = prizeDrawService.getList();
          prizeDrawList.forEach(prizeDraw -> {
              if(prizeDraw.getType() == 1) {
                  Product product = productService.getById(prizeDraw.getValue());
                  if(product != null) {
                      prizeDraw.setProductName(product.getName());
                  }
              }
              if(prizeDraw.getType() == 2) {
                  Coupon coupon = couponService.getById(prizeDraw.getValue());
                  if(coupon != null) {
                      prizeDraw.setCouponName(coupon.getName());
                  }
              }else if (prizeDraw.getType() == 3) {
                  prizeDraw.setProductName(prizeDraw.getValue() + " 积分");
              }else if (prizeDraw.getType() == 3) {
                  prizeDraw.setProductName("谢谢惠顾");
              }
          });
        return CommonResult.success(prizeDrawList);
    }


    @ApiOperation(value = "开始抽奖")
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public CommonResult<LotteryResponse> execute() {
        LotteryResponse response = prizeDrawService.execute();

        return CommonResult.success(response);
    }



    @LogControllerAnnotation(intoDB = true, methodType = MethodType.UPDATE, description = "领奖")
    @ApiOperation(value = "领奖")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public CommonResult<Object> receivePrizeDraw(@RequestBody ReceivePrizeRequest request) {
          lotteryService.supply(request.getId());
          return CommonResult.success().setMessage("领取成功");
    }


}



