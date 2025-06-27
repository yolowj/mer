package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠卷请求对象
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
@ApiModel(value="CouponRequest对象", description="优惠卷请求对象")
public class CouponRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "优惠券名称", required = true)
    @NotBlank(message = "请填写优惠券名称")
    @Length(max = 64, message = "优惠券名称长度不能超过64个字符")
    private String name;

    @ApiModelProperty(value = "类别 1-商家券, 2-商品券, 3-平台券", required = true)
    @NotNull(message = "请选择优惠券类别")
    @Range(min = 1, max = 3, message = "未知的优惠券类别")
    private Integer category;

    @ApiModelProperty(value = "领取类型 1-手动领取,2-商品买赠送券", required = true)
    @NotNull(message = "请选择优惠券领取类型")
    @Range(min = 1, max = 2, message = "未知的优惠券领取方式")
    private Integer receiveType;

    @ApiModelProperty(value = "优惠金额", required = true)
    @NotNull(message = "请填写优惠金额")
    @Range(min = 1, max = 99999, message = "优惠金额范围为1~99999")
    private Long money;

    @ApiModelProperty(value = "最低消费，0代表不限制", required = true)
    @NotNull(message = "最低消费不能为空")
    @Min(value = 0, message = "最小值不能小于0")
    private Long minPrice;

    @ApiModelProperty(value = "是否限量, 默认0 否， 1是", required = true)
    @NotNull(message = "请设置是否限量")
    private Boolean isLimited;

    @ApiModelProperty(value = "发放总数")
    private Integer total;

    @ApiModelProperty(value = "领取是否限时, 默认0-不限时，1-限时", required = true)
    @NotNull(message = "请选择领取是否限时")
    private Boolean isTimeReceive;

    @ApiModelProperty(value = "可领取开始时间")
    private Date receiveStartTime;

    @ApiModelProperty(value = "可领取结束时间")
    private Date receiveEndTime;

    @ApiModelProperty(value = "请设置是否固定使用时间, 默认0 否， 1是", required = true)
    @NotNull(message = "请设置是否固定使用时间")
    private Boolean isFixedTime;

    @ApiModelProperty(value = "可使用时间范围 开始时间")
    private Date useStartTime;

    @ApiModelProperty(value = "可使用时间范围 结束时间")
    private Date useEndTime;

    @ApiModelProperty(value = "天数")
    @Max(value = 999, message = "天数不能超过999天")
    private Integer day;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序不能小于0")
    private Integer sort;

    @ApiModelProperty(value = "状态（0：关闭，1：开启）")
    @NotNull(message = "优惠券状态不能为空")
    private Boolean status;

    @ApiModelProperty(value = "商品id，英文逗号分隔，商品券专用字段")
    private String productIds;

}
