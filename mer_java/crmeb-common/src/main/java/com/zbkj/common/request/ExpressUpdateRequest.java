package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 快递公司编辑请求对象
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
@ApiModel(value = "ExpressUpdateRequest对象", description = "快递公司编辑请求对象")
public class ExpressUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "快递公司id")
    @NotNull(message = "快递公司id不能为空")
    private Integer id;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "网点名称")
    private String netName;

    @ApiModelProperty(value = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @ApiModelProperty(value = "是否可用")
    @NotNull(message = "是否可用不能为空")
    private Boolean status;
}
