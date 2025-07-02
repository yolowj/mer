package com.zbkj.common.model.prize;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PrizeDrawRequest extends PageParamRequest {

    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "奖品类型")
    private Integer type;

    @ApiModelProperty(value = "奖品值")
    private Integer value;

    @ApiModelProperty(value = "中奖概率")
    private BigDecimal probability;

    @ApiModelProperty(value = "状态：0、关闭；1、启用；")
    private Integer status;

    @ApiModelProperty(value = "奖品数量")
    private Integer num;

    @ApiModelProperty(value = "领取条件：0、任意；1、积分；3、支付领取")
    private Integer cond;

    @ApiModelProperty(value = "积分分数/支付金额")
    private BigDecimal mon;


}
