package com.zbkj.common.request;

import com.zbkj.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 新增用户地址对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserAddressRequest对象", description="新增用户地址对象")
public class UserAddressRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户地址id,新增时不填")
    private Integer id;

    @ApiModelProperty(value = "收货人姓名", required = true)
    @NotBlank(message = "收货人姓名不能为空")
    @Length(max = 64, message = "Consignee name cannot exceed 64 characters")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "请输入正确的手机号")
    private String phone;

    @ApiModelProperty(value = "收货人所在省")
    @NotBlank(message = "收货人所在省不能为空")
    private String province;

    @ApiModelProperty(value = "省份ID")
    @NotNull(message = "省份ID不能为空")
    private Integer provinceId;

    @ApiModelProperty(value = "收货人所在市")
    @NotBlank(message = "收货人所在市不能为空")
    private String city;

    @ApiModelProperty(value = "城市id")
    @NotNull(message = "城市id不能为空")
    private Integer cityId;

    @ApiModelProperty(value = "收货人所在区/县")
    @NotBlank(message = "收货人所在区/县不能为空")
    private String district;

    @ApiModelProperty(value = "区/县id")
    @NotNull(message = "区/县id不能为空")
    private Integer districtId;

    @ApiModelProperty(value = "收货人所在街道")
//    @NotBlank(message = "收货人所在街道不能为空")
    private String street;

    @ApiModelProperty(value = "收货人详细地址", required = true)
    @NotBlank(message = "收货人详细地址不能为空")
    @Length(max = 500, message = "Consignee's full address cannot exceed 500 characters")
    private String detail;

    @ApiModelProperty(value = "是否默认", example = "false", required = true)
    private Boolean isDefault;

}
