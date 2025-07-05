package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.request.PaidMemberBenefitsStatementRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.PaidMemberBenefitsVo;
import com.zbkj.common.vo.PaidMemberConfigVo;
import com.zbkj.service.service.*;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMassTagMessage;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CdkeyLibraryServiceImpl 接口实现
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
@Service
@Slf4j
public class PaidMemberServiceImpl implements PaidMemberService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private UserService userService;

    /**
     * 付费会员配置信息获取
     */
    @Override
    public PaidMemberConfigVo getBaseConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.CONFIG_PAID_MEMBER_PAID_ENTRANCE);
        keyList.add(SysConfigConstants.CONFIG_PAID_MEMBER_PRICE_DISPLAY);
        keyList.add(SysConfigConstants.CONFIG_PAID_MEMBER_PRODUCT_SWITCH);
        MyRecord myRecord = systemConfigService.getValuesByKeyList(keyList);
        PaidMemberConfigVo vo = new PaidMemberConfigVo();
        vo.setPaidMemberPaidEntrance(myRecord.getStr(SysConfigConstants.CONFIG_PAID_MEMBER_PAID_ENTRANCE));
        vo.setPaidMemberPriceDisplay(myRecord.getStr(SysConfigConstants.CONFIG_PAID_MEMBER_PRICE_DISPLAY));
        vo.setPaidMemberProductSwitch(myRecord.getStr(SysConfigConstants.CONFIG_PAID_MEMBER_PRODUCT_SWITCH));
        return vo;
    }

    /**
     * 编辑付费会员基础配置
     */
    @Override
    public Boolean editBaseConfig(PaidMemberConfigVo voRequest) {
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_PAID_MEMBER_PAID_ENTRANCE, voRequest.getPaidMemberPaidEntrance());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_PAID_MEMBER_PRICE_DISPLAY, voRequest.getPaidMemberPriceDisplay());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_PAID_MEMBER_PRODUCT_SWITCH, voRequest.getPaidMemberProductSwitch());
            return Boolean.TRUE;
        });
    }

    /**
     * 获取付费会员会员权益
     */
    @Override
    public List<PaidMemberBenefitsVo> getBenefitsList() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_PAID_MEMBER_BENEFITS, Constants.SORT_DESC, null);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        Iterator<GroupConfig> iterator = configList.iterator();
        List<PaidMemberBenefitsVo> voList = new ArrayList<>();
        while (iterator.hasNext()) {
            GroupConfig config = iterator.next();
            PaidMemberBenefitsVo vo = new PaidMemberBenefitsVo();
            BeanUtils.copyProperties(config, vo);
            int multiple = 1;
            String channelStr = "";
            if (StrUtil.isNotBlank(config.getLinkUrl())) {
                StrUtil.split(config.getLinkUrl(), ":");
                String[] split = config.getLinkUrl().split(":");
                String multipleStr = split[0];
                multiple = Integer.parseInt(multipleStr);
                channelStr = split[1];
                if (channelStr.equals("0")) {
                    channelStr = "";
                }
            }
            vo.setMultiple(multiple);
            vo.setChannelStr(channelStr);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 编辑付费会员会员权益
     */
    @Override
    public Boolean editBenefits(PaidMemberBenefitsVo voRequest) {
        GroupConfig groupConfig = getBenefitsGroupConfigById(voRequest.getId());
        if (groupConfig.getName().equals("integralDoubling") || groupConfig.getName().equals("experienceDoubling")) {
            if (ObjectUtil.isNull(voRequest.getMultiple()) || voRequest.getMultiple() < 1 || voRequest.getMultiple() > 99) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "倍数范围为：1~99");
            }
            String channelStr = StrUtil.isBlank(voRequest.getChannelStr()) ? "0" : voRequest.getChannelStr();
            String linkUrl = voRequest.getMultiple() + ":" + channelStr;
            groupConfig.setLinkUrl(linkUrl);
        }
        groupConfig.setImageUrl(systemAttachmentService.clearPrefix(voRequest.getImageUrl()));
        groupConfig.setValue(voRequest.getValue());
        groupConfig.setMessage(voRequest.getMessage());
        groupConfig.setStatus(voRequest.getStatus());
        groupConfig.setSort(voRequest.getSort());
        groupConfig.setUpdateTime(DateUtil.date());
        return groupConfigService.updateById(groupConfig);
    }

    /**
     * 付费会员会员权益开关
     */
    @Override
    public Boolean editBenefitsSwitch(Integer id) {
        GroupConfig groupConfig = getBenefitsGroupConfigById(id);
        groupConfig.setStatus(!groupConfig.getStatus());
        groupConfig.setUpdateTime(DateUtil.date());
        return groupConfigService.updateById(groupConfig);
    }

    /**
     * 编辑付费会员会员权益说明
     */
    @Override
    public Boolean editBenefitsStatement(PaidMemberBenefitsStatementRequest request) {
        GroupConfig groupConfig = getBenefitsGroupConfigById(request.getId());
        String expand = StrUtil.isNotBlank(request.getExpand()) ? request.getExpand() : "";
        groupConfig.setExpand(systemAttachmentService.clearPrefix(expand));
        groupConfig.setUpdateTime(DateUtil.date());
        return groupConfigService.updateById(groupConfig);
    }

    private GroupConfig getBenefitsGroupConfigById(Integer id) {
        GroupConfig groupConfig = groupConfigService.getByIdException(id);
        if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_PAID_MEMBER_BENEFITS)) {
            throw new CrmebException("数据不存在");
        }
        if (!groupConfig.getName().equals("memberExclusivePrice")
                && !groupConfig.getName().equals("integralDoubling")
                && !groupConfig.getName().equals("experienceDoubling")
                && !groupConfig.getName().equals("exclusiveCustomer")) {
            throw new CrmebException("数据不存在");
        }
        return groupConfig;
    }


    /**
     * 会员过期处理
     */
    @Override
    public void memberExpirationProcessing() {
        Boolean update = userService.memberExpirationProcessing();
        if (!update) {
            throw new CrmebException("会员过期处理失败");
        }
    }


    @Autowired
    private WxMpService wxMpService;


    /**
     * 会员日提醒
     */
    @Override
    public void memberHappyProcsssing() {
        boolean  isVipDay = false ;
        // 获取会员日配置
        String MEMBER_PRESENT_SWITCH = systemConfigService.getValueByKey(SysConfigConstants.MEMBER_PRESENT_SWITCH);
        String MEMBER_PRESENT_DAY = systemConfigService.getValueByKey(SysConfigConstants.MEMBER_PRESENT_DAY);
        String MEMBER_PRESENT_WEEK = systemConfigService.getValueByKey(SysConfigConstants.MEMBER_PRESENT_WEEK);
        // 检查开关和日期配置
        if (StrUtil.isNotBlank(MEMBER_PRESENT_SWITCH) && Constants.COMMON_SWITCH_OPEN.equals(MEMBER_PRESENT_SWITCH)) {
            if (ObjectUtil.isNotNull(MEMBER_PRESENT_DAY) && Integer.parseInt(MEMBER_PRESENT_DAY) !=0 ) {
                Integer vipMonth = LocalDate.now().getDayOfMonth();
                if (vipMonth.compareTo(Integer.parseInt(MEMBER_PRESENT_DAY)) == 0) {
                    isVipDay = true;
                }
            }
            if (ObjectUtil.isNotNull(MEMBER_PRESENT_WEEK) && Integer.parseInt(MEMBER_PRESENT_WEEK) !=0) {
                Integer vipWeek = LocalDate.now().getDayOfWeek().getValue();
                if (vipWeek.compareTo(Integer.parseInt(MEMBER_PRESENT_DAY)) == 0) {
                    isVipDay = true;
                }
            }
        }

        if(isVipDay){
            // 构建图文消息(需先上传素材)
            WxMpMassTagMessage massMessage = new WxMpMassTagMessage();
            massMessage.setTagId(Long.parseLong("0")); // 0表示所有用户
            massMessage.setMsgType(WxConsts.MassMsgType.MPNEWS);
            massMessage.setContent("会员日，折扣活动提醒");
            try {
                // 执行群发
                WxMpMassSendResult result = wxMpService.getMassMessageService().massGroupMessageSend(massMessage);
                System.out.println("广播消息ID: " + result.getMsgId());
            } catch (WxErrorException e) {
                e.printStackTrace();
            }
        }


    }



}


