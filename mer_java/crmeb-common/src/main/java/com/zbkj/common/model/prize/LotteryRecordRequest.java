package com.zbkj.common.model.prize;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LotteryRecordRequest {

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "奖品ID")
    private Integer addId;

}
