package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 系统通知详情请求对象
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
@ApiModel(value="NotificationInfoRequest对象", description="系统通知详情请求对象")
public class NotificationInfoRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "通知id")
    @NotNull(message = "通知id不能为空")
    private Integer id;

    @ApiModelProperty(value = "wechat-公众号模板消息，routine-小程序订阅消息，sms-短信")
    @NotEmpty(message = "详情类型不能为空")
    @StringContains(limitValues = {"wechat","routine","sms"}, message = "未知的详情类型")
    private String detailType;

}
