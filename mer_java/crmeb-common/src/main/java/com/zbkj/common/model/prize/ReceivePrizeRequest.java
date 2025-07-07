package com.zbkj.common.model.prize;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ReceivePrizeRequest", description = "领取请求对象")
public class ReceivePrizeRequest implements Serializable {

    @ApiModelProperty(value = "中奖记录id")
    private Integer id;

}
