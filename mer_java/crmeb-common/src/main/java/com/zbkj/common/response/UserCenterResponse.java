package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 个人中心响应对象
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
@ApiModel(value="UserCenterResponse对象", description="个人中心响应对象")
public class UserCenterResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "用户剩余积分")
    private Integer integral;

    @ApiModelProperty(value = "用户剩余经验")
    private Integer experience;

    @ApiModelProperty(value = "等级")
    private Integer level;

//    @ApiModelProperty(value = "是否为推广员")
//    private Boolean isPromoter;

    @ApiModelProperty(value = "用户优惠券数量")
    private Integer couponCount;

    @ApiModelProperty(value = "是否会员")
    private Boolean isVip;

    @ApiModelProperty(value = "会员图标")
    private String vipIcon;

    @ApiModelProperty(value = "会员名称")
    private String vipName;

    @ApiModelProperty(value = "用户收藏数量")
    private Integer collectCount;

    @ApiModelProperty(value = "用户足迹数量")
    private Integer browseNum;

    @ApiModelProperty(value = "个人中心banner")
    private List<HashMap<String, Object>> centerBanner;

//    @ApiModelProperty(value = "个人中心服务")
//    private List<HashMap<String, Object>> centerMenu;

    @ApiModelProperty(value = "是否付费会员")
    private Boolean isPaidMember;

    @ApiModelProperty(value = "付费会员入口：1-开启，0-关闭")
    private Integer paidMemberPaidEntrance = 0;

//    @ApiModelProperty(value = "移动端商家管理 - 当前最新token - 作用于移动端商家管理")
//    private LoginResponse loginResponse;

//    @ApiModelProperty(value = "是否移动端商家管理员")
//    private Boolean isEmployee = false;
}
