package com.zbkj.admin.controller.platform;

import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.prize.PrizeDraw;
import com.zbkj.common.model.prize.PrizeDrawRequest;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.CouponService;
import com.zbkj.service.service.PrizeDrawService;
import com.zbkj.service.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * 抽奖设置 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/platform/prize")
@Api(tags = "抽奖设置") //配合swagger使用

public class PrizeDrawController {

    @Autowired
    private PrizeDrawService prizeDrawService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CouponService couponService;

    /**
     * 分页显示抽奖设置
     * @param prizeDrawRequest 分页参数
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<PrizeDraw>>  getList(@Validated PrizeDrawRequest prizeDrawRequest) {
        CommonPage<PrizeDraw> prizeDrawCommonPage = CommonPage.restPage(prizeDrawService.getList(prizeDrawRequest));
        prizeDrawCommonPage.getList().forEach(prizeDraw -> {
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
            }
        });
        return CommonResult.success(prizeDrawCommonPage);
    }

    /**
     * 新增抽奖设置
     * @param prizeDrawRequest 新增参数
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated PrizeDrawRequest prizeDrawRequest) {
        PrizeDraw prizeDraw = new PrizeDraw();
        BeanUtils.copyProperties(prizeDrawRequest, prizeDraw);
        prizeDraw.setCreateTime(new Date());
        if(prizeDrawService.save(prizeDraw)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 删除抽奖设置
     * @param id Integer
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id) {
        if(prizeDrawService.removeById(id)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改抽奖设置
     * @param id integer id
     * @param prizeDrawRequest 修改参数
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated PrizeDrawRequest prizeDrawRequest) {
        PrizeDraw prizeDraw = new PrizeDraw();
        BeanUtils.copyProperties(prizeDrawRequest, prizeDraw);
        prizeDraw.setId(id);

        if(prizeDrawService.updateById(prizeDraw)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 查询抽奖设置信息
     * @param id Integer
     * @author berton
     * @since 2025-06-26
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<PrizeDraw> info(@RequestParam(value = "id") Integer id) {
        PrizeDraw prizeDraw = prizeDrawService.getById(id);
        return CommonResult.success(prizeDraw);
    }
}



