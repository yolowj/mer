package com.zbkj.common.response;

import com.zbkj.common.model.coupon.Coupon;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 新人礼响应对象
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
@ApiModel(value = "MembersDayPresentResponse对象", description = "会员日响应对象")
public class MembersDayPresentResponse implements Serializable {

    private static final long serialVersionUID = -4585094537501770138L;

    @ApiModelProperty(value = "会员日响应")
    private Boolean membersDaySwitch;


    @ApiModelProperty(value = "会员日")
    private Integer selectedWeek;

    @ApiModelProperty(value = "会员周")
    private Integer selectedDay;

    @ApiModelProperty(value = "新人礼优惠券列表")
    private List<Coupon> couponList;

}
