package com.zbkj.common.model.prize;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PrizeDrawRequest extends PageParamRequest {

    private Integer id;

    @ApiModelProperty(value = "奖品")
    private Integer value;

    @ApiModelProperty(value = "中奖概率")
    private BigDecimal probability;

    @ApiModelProperty(value = "奖品类型(1：商品，2：优惠券，3：积分，4：谢谢惠顾)")
    private Integer type;
}
