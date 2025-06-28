package com.zbkj.common.model.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.common.model.coupon.Coupon;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 普通会员等级
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_system_user_level")
@ApiModel(value="SystemUserLevel对象", description="系统用户等级")
public class SystemUserLevel implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "会员名称")
    private String name;

    @ApiModelProperty(value = "积分比例")
    private BigDecimal integralRatio;

    @ApiModelProperty(value = "首次下单比例")
    private BigDecimal firstOrderRatio;

    @ApiModelProperty(value = "达到多少升级经验")
    private Integer experience;

    @ApiModelProperty(value = "是否显示 1=显示,0=隐藏")
    private Boolean isShow;

    @ApiModelProperty(value = "会员等级")
    private Integer grade;

    @ApiModelProperty(value = "享受折扣")
    private Integer discount;

    @ApiModelProperty(value = "会员图标")
    private String icon;

    @ApiModelProperty(value = "是否删除.1=删除,0=未删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "用户等级背景图")
    private String backImage;

    @ApiModelProperty(value = "用户等级文字背景色")
    private String backColor;

    @ApiModelProperty(value = "优惠券IDs")
    private String couponIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "优惠券IDs")
    private List<Coupon> coupons;

}
