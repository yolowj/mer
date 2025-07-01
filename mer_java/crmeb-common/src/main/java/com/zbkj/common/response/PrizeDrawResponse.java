package com.zbkj.common.response;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单号响应对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PrizeDrawResponse对象", description="抽奖响应对象")
public class PrizeDrawResponse implements Serializable {

    private static final long serialVersionUID = 1L;


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

    @ApiModelProperty(value = "数量")
    private Integer num;


    @TableField(exist = false)
    private String productName;

    @TableField(exist = false)
    private String couponName;
}
