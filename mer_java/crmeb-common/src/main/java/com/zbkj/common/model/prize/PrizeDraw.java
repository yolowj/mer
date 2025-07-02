package com.zbkj.common.model.prize;

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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 抽奖设置
 * </p>
 *
 * @author berton
 * @since 2025-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_prize_draw")
@ApiModel(value="PrizeDraw对象", description="抽奖设置")
public class PrizeDraw implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "1：商品，2：优惠券，3：积分，4：谢谢惠顾")
    private Integer type;

    @ApiModelProperty(value = "积分或者奖品id")
    private Integer value;

    @ApiModelProperty(value = "中奖概率")
    private BigDecimal probability;

    @ApiModelProperty(value = "奖品图片")
    private String img;

    @ApiModelProperty(value = "是否显示")
    private Integer status;

    @ApiModelProperty(value = "奖品数量")
    private Integer num;

    @ApiModelProperty(value = "领取条件：0、任意；1、积分；3、支付领取")
    private Integer cond;

    @ApiModelProperty(value = "积分分数/支付金额")
    private BigDecimal mon;

    private Date createTime;


    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String couponName;
}
