package com.zbkj.common.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user")
@ApiModel(value = "User对象", description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户账号")
    private String account;

    @ApiModelProperty(value = "用户密码")
    private String pwd;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "身份证号码")
    private String identityCardNo;

    @ApiModelProperty(value = "标签id,英文逗号分隔")
    private String tagId;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "国家，中国CN，其他OTHER")
    private String country;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private Integer sex;

    @ApiModelProperty(value = "用户积分")
    private Integer integral;

    @ApiModelProperty(value = "用户经验")
    private Integer experience;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "佣金金额")
    private BigDecimal brokeragePrice;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "连续签到天数")
    private Integer signNum;

    @ApiModelProperty(value = "是否关联公众号")
    private Boolean isWechatPublic;

    @ApiModelProperty(value = "是否关联小程序")
    private Boolean isWechatRoutine;

    @ApiModelProperty(value = "是否关联微信ios")
    private Boolean isWechatIos;

    @ApiModelProperty(value = "是否关联微信android")
    private Boolean isWechatAndroid;

    @ApiModelProperty(value = "是否关联ios")
    private Boolean isBindingIos;

    @ApiModelProperty(value = "用户购买次数")
    private Integer payCount;

    @ApiModelProperty(value = "是否为推广员")
    private Boolean isPromoter;

    @ApiModelProperty(value = "成为分销员时间")
    private Date promoterTime;

    @ApiModelProperty(value = "上级推广员id")
    private Integer spreadUid;

    @ApiModelProperty(value = "绑定上级推广员时间")
    private Date spreadTime;

    @ApiModelProperty(value = "下级人数")
    private Integer spreadCount;

    @ApiModelProperty(value = "注册类型：wechat-公众号，routine-小程序，H5-H5,iosWx-微信ios，androidWx-微信安卓，ios-ios")
    private String registerType;

    @ApiModelProperty(value = "创建ip")
    private String addIp;

    @ApiModelProperty(value = "最后一次登录ip")
    private String lastIp;

    @ApiModelProperty(value = "最后一次登录时间")
    private Date lastLoginTime;

    @ApiModelProperty(value = "1为正常，0为禁止")
    private Boolean status;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否注销")
    private Boolean isLogoff;

    @ApiModelProperty(value = "注销时间")
    private Date logoffTime;

    @ApiModelProperty(value = "用户签名")
    private String signature;

    @ApiModelProperty(value = "是否付费会员")
    private Boolean isPaidMember;

    @ApiModelProperty(value = "是否永久付费会员")
    private Boolean isPermanentPaidMember;

    @ApiModelProperty(value = "付费会员到期时间")
    private Date paidMemberExpirationTime;

    @ApiModelProperty(value = "是否关联微信网站应用PC")
    private Boolean isWechatPc;
}
