package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.prize.LotteryRecord;
import com.zbkj.common.model.prize.LotteryRecordRequest;
import com.zbkj.common.model.prize.PrizeDrawRequest;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CouponService;
import com.zbkj.service.service.LotteryRecordService;
import com.zbkj.service.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 抽奖记录表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/lotteryRecord")
@Api(tags = "抽奖记录表") //配合swagger使用

public class LotteryRecordController {

    @Autowired
    private LotteryRecordService lotteryRecordService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CouponService couponService;

    /**
     * 分页显示抽奖记录表
     * @param prizeDrawRequest 分页参数
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<LotteryRecord>> getList(@Validated PrizeDrawRequest prizeDrawRequest) {
        CommonPage<LotteryRecord> lotteryRecordCommonPage = CommonPage.restPage(lotteryRecordService.getList(prizeDrawRequest));
        lotteryRecordCommonPage.getList().forEach(lotteryRecord -> {

            if(lotteryRecord.getPrizeType() == 1) {
                Product product = productService.getById(lotteryRecord.getPrizeValue());
                if(product != null) {
                    lotteryRecord.setProductName(product.getName());
                }
            }
            if(lotteryRecord.getPrizeType() == 2) {
                Coupon coupon = couponService.getById(lotteryRecord.getPrizeValue());
                if(coupon != null) {
                    lotteryRecord.setCouponName(coupon.getName());
                }
            }
        });
        return CommonResult.success(lotteryRecordCommonPage);
    }

    /**
     * 新增抽奖记录表
     * @param lotteryRecordRequest 新增参数
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated LotteryRecordRequest lotteryRecordRequest) {
        LotteryRecord lotteryRecord = new LotteryRecord();
        BeanUtils.copyProperties(lotteryRecordRequest, lotteryRecord);

        if(lotteryRecordService.save(lotteryRecord)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除抽奖记录表
     * @param id Integer
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if(lotteryRecordService.removeById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改抽奖记录表
     * @param id integer id
     * @param lotteryRecordRequest 修改参数
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated LotteryRecordRequest lotteryRecordRequest) {
        LotteryRecord lotteryRecord = new LotteryRecord();
        BeanUtils.copyProperties(lotteryRecordRequest, lotteryRecord);
        lotteryRecord.setId(id);

        if(lotteryRecordService.updateById(lotteryRecord)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询抽奖记录表信息
     * @param id Integer
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<LotteryRecord> info(@RequestParam(value = "id") Integer id) {
        LotteryRecord lotteryRecord = lotteryRecordService.getById(id);
        return CommonResult.success(lotteryRecord);
    }
}



