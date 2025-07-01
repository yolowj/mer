package com.zbkj.front.controller;

import com.zbkj.common.model.sgin.UserSignRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SupSignRequest;
import com.zbkj.common.response.SignPageInfoResponse;
import com.zbkj.common.response.SupSignResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.service.service.SignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 签到控制器
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
@Slf4j
@RestController
@RequestMapping("api/front/sign")
@Api(tags = "签到控制器")
public class SignController {

    @Autowired
    private SignService signService;

    @ApiOperation(value = "签到记录列表")
    @RequestMapping(value = "/record/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSignRecord>> getList(@Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(CommonPage.restPage(signService.findFrontSignRecordList(pageParamRequest)));
    }

    @ApiOperation(value = "签到页信息")
    @RequestMapping(value = "/page/info", method = RequestMethod.GET)
    public CommonResult<SignPageInfoResponse> getPageInfo(@RequestParam(value = "month", defaultValue = "", required = false) String month) {
        return CommonResult.success(signService.getPageInfo(month));
    }

    @ApiOperation(value = "执行签到")
    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public CommonResult<SignPageInfoResponse> execute() {
        return CommonResult.success(signService.execute());
    }

    @ApiOperation(value = "补签")
    @RequestMapping(value = "/retroactiveSign", method = RequestMethod.POST)
    public CommonResult<SupSignResponse> retroactiveSign(@RequestBody @Validated SupSignRequest request) {
        return CommonResult.success(signService.retroactiveSign(request.getDay(), request.getMonth()));
    }

}



