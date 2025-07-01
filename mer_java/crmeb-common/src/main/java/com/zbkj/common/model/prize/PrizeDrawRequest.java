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
}
