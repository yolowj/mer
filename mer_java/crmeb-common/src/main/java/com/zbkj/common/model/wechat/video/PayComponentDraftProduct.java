package com.zbkj.common.model.wechat.video;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 组件商品草稿表
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
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_pay_component_draft_product")
@ApiModel(value="PayComponentDraftProduct对象", description="组件商品草稿表")
public class PayComponentDraftProduct implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品ID，商家自定义商品ID")
    private Integer productId;

    @ApiModelProperty(value = "主商品ID")
    private Integer primaryProductId;

    @ApiModelProperty(value = "交易组件平台内部商品ID")
    private Integer componentProductId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "绑定的小程序商品路径")
    private String path;

    @ApiModelProperty(value = "轮播图,多张")
    private String headImg;

    @ApiModelProperty(value = "商品资质图片,多张")
    private String qualificationPics;

    @ApiModelProperty(value = "第三级类目ID")
    private Integer thirdCatId;

    @ApiModelProperty(value = "第三级类目名称")
    @TableField(exist = false)
    private String thirdCatName;

    @ApiModelProperty(value = "品牌id")
    private Integer brandId;

    @ApiModelProperty(value = "预留字段，用于版本控制")
    private String infoVersion;

    @ApiModelProperty(value = "审核结果文字")
    private String reject_reason;

    @ApiModelProperty(value = "微信侧状态:0-初始值，5-上架，11-自主下架，13-违规下架/风控系统下架")
    private Integer status;

    @ApiModelProperty(value = "微信侧审核状态:1-未审核，2-审核中，3-审核失败，4-审核成功")
    private Integer editStatus;

    @ApiModelProperty(value = "平台状态:0初始值 5商家上架 6平台上架 11商家下架 12平台下架")
    private Integer platformStatus;

    @ApiModelProperty(value = "平台审核状态:1-未审核，2-平台审核中，3-平台审核失败，4-平台审核成功")
    private Integer platformEditStatus;

    @ApiModelProperty(value = "平台自定义状态审核原因")
    private String platformStatusReason;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "反多少积分")
    private Integer giveIntegral;

    @ApiModelProperty(value = "规格 0单 1多")
    private Boolean specType;

    @ApiModelProperty(value = "虚拟销量")
    private Integer ficti;

    @ApiModelProperty(value = "desc详情")
    private String descInfo;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "attr(前端用)")
    private String attr;

    @ApiModelProperty(value = "attrValue(前端用)")
    private String attrValue;

    @ApiModelProperty(value = "是否删除 0未删除 1删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date addTime;

    @ApiModelProperty(value = "类目信息")
    @TableField(exist = false)
    private PayComponentCat catInfo;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

}
