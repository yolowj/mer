package com.zbkj.common.model.sgin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.product.Product;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 签到设置表
 * </p>
 *
 * @author HZW
 * @since 2022-09-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_sign_config")
@ApiModel(value = "SignConfig对象", description = "签到设置表")
public class SignConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "连续签到天数，0-每天签到奖励")
    private Integer day;

    @ApiModelProperty(value = "是否奖励积分")
    private Boolean isIntegral;

    @ApiModelProperty(value = "签到积分")
    private Integer integral;

    @ApiModelProperty(value = "是否奖励经验")
    private Boolean isExperience;

    @ApiModelProperty(value = "签到经验")
    private Integer experience;

    @ApiModelProperty(value = "备注说明")
    private String mark;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "是否优惠券", required = true)
    private Boolean isCoupon = false;

    @ApiModelProperty(value = "签到积分", required = true)
    private Integer couponId;

    @ApiModelProperty(value = "是否奖励商品", required = true)
    private Boolean isProduct = false;;

    @ApiModelProperty(value = "签到经验", required = true)
    private Integer productId;

    @ApiModelProperty(value = "是否补签扣除积分", required = true)
    private Boolean isSupIntegral = false;

    @ApiModelProperty(value = "补签扣除积分", required = true)
    private Integer supIntegral;

    @ApiModelProperty(value = "是否现在周补签次数", required = true)
    private Boolean isSupNum;

    @ApiModelProperty(value = "补签次数", required = true)
    private Integer supNum;

    @TableField(exist = false)
    private List<Coupon> coupons;

    @TableField(exist = false)
    private List<Product> products;

    @TableField(exist = false)
    private String couponName;
}