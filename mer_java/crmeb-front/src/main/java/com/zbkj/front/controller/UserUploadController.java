package com.zbkj.front.controller;

import com.zbkj.common.request.BaseUploadRequest;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.FileResultVo;
import com.zbkj.service.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * 上传文件 前端控制器 -- 用户端
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
@RequestMapping("api/front/upload")
@Api(tags = "用户上传文件")
public class UserUploadController {

    @Autowired
    private UploadService uploadService;

    @ApiOperation(value = "图片上传")
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "model", value = "模块 用户user,商品product,微信wechat,news文章"),
            @ApiImplicitParam(name = "pid", value = "分类ID 0编辑器,1商品图片,2拼团图片,3砍价图片,4秒杀图片,5文章图片,6组合数据图,7前台用户,8微信系列 ", allowableValues = "range[0,1,2,3,4,5,6,7,8]")
    })
    public CommonResult<FileResultVo> image(MultipartFile multipart, @RequestParam(value = "model") String model,
                                            @RequestParam(value = "pid") Integer pid) {

        return CommonResult.success(uploadService.imageUpload(multipart, model, pid, null));
    }

    @ApiOperation(value = "文件上传")
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "model", value = "模块 用户user,商品product,微信wechat,news文章"),
            @ApiImplicitParam(name = "pid", value = "分类ID 0编辑器,1商品图片,2拼团图片,3砍价图片,4秒杀图片,5文章图片,6组合数据图,7前台用户,8微信系列 ", allowableValues = "range[0,1,2,3,4,5,6,7,8]")
    })
    public CommonResult<FileResultVo> file(MultipartFile multipart, @RequestParam(value = "model") String model,
                                           @RequestParam(value = "pid") Integer pid) {
        return CommonResult.success(uploadService.fileUpload(multipart, model, pid, null));
    }

    @ApiOperation(value = "base64图片上传")
    @RequestMapping(value = "/base64", method = RequestMethod.POST)
    public CommonResult<FileResultVo> base64Upload(@RequestBody @Validated BaseUploadRequest request) {
        return CommonResult.success(uploadService.base64Upload(request.getBase64Url(), request.getModel(), request.getPid()));
    }
}



