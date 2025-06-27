package com.zbkj.common.request.page;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: 大粽子
 * @Date: 2023/5/16 11:44
 * @Description: 页面分类和DIY用到的实体参数
 */
@Data
public class PageDiyRequest {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "版本号")
    private String version;

    @ApiModelProperty(value = "页面名称")
    private String name;

    @ApiModelProperty(value = "网站标题")
    private String title;

    @ApiModelProperty(value = "封面图")
    private String coverImage;

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "页面数据")
    private JSONObject value;

    @ApiModelProperty(value = "默认数据")
    private String defaultValue;

    @ApiModelProperty(value = "添加时间")
    private Date addTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否使用")
    private Integer status;

    @ApiModelProperty(value = "页面类型")
    private Integer type;

    @ApiModelProperty(value = "显示首页")
    private Integer isShow;

    @ApiModelProperty(value = "颜色是否选中")
    private Integer isBgColor;

    @ApiModelProperty(value = "背景图是否选中")
    private Integer isBgPic;

    @ApiModelProperty(value = "是否是diy数据")
    private Integer isDiy;

    @ApiModelProperty(value = "背景颜色")
    private String colorPicker;

    @ApiModelProperty(value = "背景图")
    private String bgPic;

    @ApiModelProperty(value = "背景图图片样式")
    private Integer bgTabVal;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @ApiModelProperty(value = "返回地址")
    private String returnAddress;

    @ApiModelProperty(value = "标题背景色")
    private String titleBgColor;

    @ApiModelProperty(value = "标题颜色")
    private String titleColor;

    @ApiModelProperty(value = "商家样式")
    private Integer serviceStatus;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "默认模板1-默认，0-不默认")
    private Integer isDefault;
}
