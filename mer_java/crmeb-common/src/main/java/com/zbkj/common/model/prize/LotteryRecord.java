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
import java.util.Date;

/**
 * <p>
 * 抽奖记录表
 * </p>
 *
 * @author berton
 * @since 2025-06-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_lottery_record")
@ApiModel(value="LotteryRecord对象", description="抽奖记录表")
public class LotteryRecord implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "奖品ID")
    private Integer prizeId;

    @ApiModelProperty(value = "奖品类型")
    private Integer prizeType;

    @ApiModelProperty(value = "奖品值")
    private Integer prizeValue;

    @ApiModelProperty(value = "消耗积分")
    private Integer costPoints;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "0:未领取 1:已领取 2:已过期")
    private Integer status;

    @ApiModelProperty(value = "用户名")
    @TableField(exist = false)
    private String userName;


    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String couponName;
}
