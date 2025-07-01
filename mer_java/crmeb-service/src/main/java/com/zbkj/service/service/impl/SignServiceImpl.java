package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.sgin.SignConfig;
import com.zbkj.common.model.sgin.UserSignRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserExperienceRecord;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SignConfigRequest;
import com.zbkj.common.request.SignRecordSearchRequest;
import com.zbkj.common.request.SupSignRequest;
import com.zbkj.common.response.SignConfigResponse;
import com.zbkj.common.response.SignPageInfoResponse;
import com.zbkj.common.response.SupSignResponse;
import com.zbkj.common.response.UserSignRecordResponse;
import com.zbkj.common.vo.PaidMemberBenefitsVo;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 签到服务实现类
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
@Service
public class SignServiceImpl implements SignService {

    @Autowired
    private SignConfigService signConfigService;
    @Autowired
    private UserSignRecordService userSignRecordService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private UserExperienceRecordService userExperienceRecordService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private PaidMemberService paidMemberService;

    /**
     * 获取签到配置
     */
    @Override
    public SignConfigResponse getConfig() {
        String signRule = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_SIGN_RULE_DESCRIPTION);
        SignConfigResponse response = new SignConfigResponse();
        response.setSignRuleDescription(signRule);
        List<SignConfig> configList = signConfigService.findList();
        if (CollUtil.isEmpty(configList)) {
            response.setBaseSignConfig(getInitBaseConfig());
            return response;
        }
        for (int i = 0; i < configList.size(); i++) {
            SignConfig signConfig = configList.get(i);
            if (signConfig.getDay().equals(0)) {
                response.setBaseSignConfig(signConfig);
                configList.remove(i);
                break;
            }
        }
        if (ObjectUtil.isNull(response.getBaseSignConfig())) {
            response.setBaseSignConfig(getInitBaseConfig());
        }
        response.setSignConfigList(configList);
        return response;
    }

    /**
     * 新增连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    @Override
    public Boolean addConfig(SignConfigRequest request) {
        request.setId(null);
        return signConfigService.add(request);
    }

    /**
     * 删除连续签到配置
     * @param id 签到配置id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        return signConfigService.delete(id);
    }

    /**
     * 编辑基础签到配置
     * @param request 配置参数
     * @return Boolean
     */
    @Override
    public Boolean editBaseConfig(SignConfigRequest request) {
        if (StrUtil.isBlank(request.getSignRuleDescription())) {
            throw new CrmebException("请填写签到规则说明");
        }
        return transactionTemplate.execute(e -> {
            signConfigService.editBaseConfig(request);
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_SIGN_RULE_DESCRIPTION, request.getSignRuleDescription());
            return Boolean.TRUE;
        });
    }

    /**
     * 编辑连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    @Override
    public Boolean editAwardConfig(SignConfigRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("签到配置id不能为空");
        }
        return signConfigService.editAwardConfig(request);
    }

    /**
     * 获取用户签到记录
     * @param request 分页参数
     */
    @Override
    public PageInfo<UserSignRecordResponse> getSignRecordList(SignRecordSearchRequest request) {
        return userSignRecordService.pageRecordList(request);
    }

    /**
     * 获取签到页信息
     * @param month 月份 yyyy-MM
     * @return 签到页信息
     */
    @Override
    public SignPageInfoResponse getPageInfo(String month) {
        User user = userService.getInfo();
        List<UserSignRecord> signRecordList = userSignRecordService.findByMonth(user.getId(), month);
        List<String> signDateStrList = signRecordList.stream().map(UserSignRecord::getDate).collect(Collectors.toList());
        SignPageInfoResponse response = new SignPageInfoResponse();
        response.setSignDateList(signDateStrList);
        return response;
    }


    /**
     * 获取签到页信息
     * @return 签到页信息
     */
    @Override
    public SignPageInfoResponse execute() {
        User user = userService.getInfo();
        DateTime date = DateUtil.date();
        String todayStr = date.toString(DateConstants.DATE_FORMAT_DATE);
        // 是否签到提示
        boolean isTip = false;
        // 判断今天是否签到
        UserSignRecord lastSignRecord = userSignRecordService.getLastByUid(user.getId());
        if (ObjectUtil.isNull(lastSignRecord) || !lastSignRecord.getDate().equals(todayStr)) {
            lastSignRecord = sign(todayStr, user, lastSignRecord);
            isTip = true;
        }else {
            throw new CrmebException("今天已经签到过啦~！");
        }

        SignPageInfoResponse response = new SignPageInfoResponse();
        response.setSignDayNum(lastSignRecord.getDay());
        response.setIntegral(lastSignRecord.getIntegral() + lastSignRecord.getAwardIntegral());
        response.setExperience(lastSignRecord.getExperience() + lastSignRecord.getAwardExperience());
        String signRule = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_SIGN_RULE_DESCRIPTION);
        response.setSignRule(signRule);
        response.setIsTip(isTip);
        return response;
    }


    /**
     * 获取移动端签到记录列表
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    @Override
    public PageInfo<UserSignRecord> findFrontSignRecordList(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        return userSignRecordService.findPageByUid(uid, pageParamRequest);
    }

    /**
     * 签到
     * @param todayStr 今天日期 yyyy-MM-dd
     * @param user 用户
     * @param lastSignRecord 最后签到记录
     */
    private UserSignRecord sign(String todayStr, User user, UserSignRecord lastSignRecord) {
        // 今日,未签到,进行签到
        SignConfigResponse config = getConfig();
        SignConfig baseSignConfig = config.getBaseSignConfig();
        // 连续签到天数
        if (user.getSignNum() > 0) {
            String yesterdayStr = DateUtil.yesterday().toString(DateConstants.DATE_FORMAT_DATE);
            if (ObjectUtil.isNull(lastSignRecord) || !lastSignRecord.getDate().equals(yesterdayStr)) {
                user.setSignNum(0);
            }
        }
        int integral = 0;
        int experience = 0;
        UserSignRecord userSignRecord = new UserSignRecord();
        userSignRecord.setUid(user.getId());
        userSignRecord.setDate(todayStr);
        userSignRecord.setDay(user.getSignNum() + 1);
        userSignRecord.setIntegral(0);
        userSignRecord.setExperience(0);
        userSignRecord.setAwardIntegral(0);
        userSignRecord.setAwardExperience(0);

        if (baseSignConfig.getIsIntegral()) {
            userSignRecord.setIntegral(baseSignConfig.getIntegral());
            integral += baseSignConfig.getIntegral();
        }
        if (baseSignConfig.getIsExperience()) {
            userSignRecord.setExperience(baseSignConfig.getExperience());
            experience += baseSignConfig.getExperience();
        }
        List<SignConfig> signConfigList = config.getSignConfigList();
        SignConfig signConfig = signConfigList.stream().filter(e -> e.getDay().equals(user.getSignNum() + 1)).findAny().orElse(null);
        if (ObjectUtil.isNotNull(signConfig)) {
            if (signConfig.getIsIntegral()) {
                userSignRecord.setAwardIntegral(signConfig.getIntegral());
                integral += signConfig.getIntegral();
            }
            if (signConfig.getIsExperience()) {
                userSignRecord.setAwardExperience(signConfig.getExperience());
                experience += signConfig.getExperience();
            }
        } else {
            if (user.getSignNum() > 0 && CollUtil.isNotEmpty(signConfigList)) {
                SignConfig signConfigMax = signConfigList.stream().max(Comparator.comparing(SignConfig::getDay)).get();
                if (signConfigMax.getIsIntegral()) {
                    userSignRecord.setAwardIntegral(signConfigMax.getIntegral());
                    integral += signConfigMax.getIntegral();
                }
                if (signConfigMax.getIsExperience()) {
                    userSignRecord.setAwardExperience(signConfigMax.getExperience());
                    experience += signConfigMax.getExperience();
                }
            }
        }

        if (user.getIsPaidMember()) {
            List<PaidMemberBenefitsVo> benefitsList = paidMemberService.getBenefitsList();
            for (PaidMemberBenefitsVo b : benefitsList) {
                if (b.getStatus()) {
                    if (b.getName().equals("integralDoubling") && b.getMultiple() > 1 && b.getChannelStr().contains("1")) {
                        integral = integral * b.getMultiple();
                        userSignRecord.setIntegral(userSignRecord.getIntegral() * b.getMultiple());
                        userSignRecord.setAwardIntegral(userSignRecord.getAwardIntegral() * b.getMultiple());
                    }
                    if (b.getName().equals("experienceDoubling") && b.getMultiple() > 1 && b.getChannelStr().contains("1")) {
                        experience = experience * b.getMultiple();
                        userSignRecord.setExperience(userSignRecord.getExperience() * b.getMultiple());
                        userSignRecord.setAwardExperience(userSignRecord.getAwardExperience() * b.getMultiple());
                    }
                }
            }
        }

        StringBuilder mark = new StringBuilder("签到奖励");
        if (baseSignConfig.getIsIntegral() || (ObjectUtil.isNotNull(signConfig) && signConfig.getIsIntegral())) {
            mark.append(integral).append("积分");
        }
        if (baseSignConfig.getIsExperience() || (ObjectUtil.isNotNull(signConfig) && signConfig.getIsExperience())) {
            mark.append(experience).append("经验");
        }
        userSignRecord.setMark(mark.toString());
        int finalIntegral = integral;
        int finalExperience = experience;
        Boolean execute = transactionTemplate.execute(e -> {
            userService.updateSignNumByUid(userSignRecord.getDay(), user.getId());
            userSignRecordService.save(userSignRecord);
            if (finalIntegral > 0) {
                userService.updateIntegral(user.getId(), finalIntegral, Constants.OPERATION_TYPE_ADD);
                UserIntegralRecord integralRecord = new UserIntegralRecord();
                integralRecord.setUid(user.getId());
                integralRecord.setLinkId("0");
                integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
                integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                integralRecord.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_SIGN);
                integralRecord.setMark(StrUtil.format("签到奖励{}积分", finalIntegral));
                integralRecord.setIntegral(finalIntegral);
                integralRecord.setBalance(finalIntegral + user.getIntegral());
                integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
                userIntegralRecordService.save(integralRecord);
            }
            if (finalExperience > 0) {
                userService.updateExperience(user.getId(), finalExperience, Constants.OPERATION_TYPE_ADD);
                UserExperienceRecord experienceRecord = new UserExperienceRecord();
                experienceRecord.setUid(user.getId());
                experienceRecord.setLinkId("0");
                experienceRecord.setLinkType(ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_SIGN);
                experienceRecord.setType(ExperienceRecordConstants.EXPERIENCE_RECORD_TYPE_ADD);
                experienceRecord.setTitle(ExperienceRecordConstants.EXPERIENCE_RECORD_TITLE_SIGN);
                experienceRecord.setMark(StrUtil.format("签到奖励{}经验", finalExperience));
                experienceRecord.setExperience(finalExperience);
                experienceRecord.setBalance(finalExperience + user.getExperience());
                experienceRecord.setStatus(ExperienceRecordConstants.EXPERIENCE_RECORD_STATUS_CREATE);
                userExperienceRecordService.save(experienceRecord);
            }
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("签到失败");
        }
        if (finalExperience > 0) {
            asyncService.userLevelUp(user.getId(), user.getLevel(), user.getExperience() + finalExperience);
        }
        return userSignRecord;
    }

    /**
     * 获取初始化基础签到配置
     */
    private SignConfig getInitBaseConfig() {
        SignConfig signConfig = new SignConfig();
        signConfig.setDay(0);
        signConfig.setIsIntegral(false);
        signConfig.setIsExperience(false);
        signConfig.setIntegral(0);
        signConfig.setExperience(0);
        signConfigService.save(signConfig);
        return signConfig;
    }

    // 添加获取每月最大补签次数的方法
    private int getMaxRetroSignTimes() {
        String configValue = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_RETRO_SIGN_MAX_TIMES);
        return StrUtil.isNotBlank(configValue) ? Integer.parseInt(configValue) : 3; // 默认3次
    }

    // 添加获取用户当月已补签次数的方法
    private int getUserRetroSignTimesThisMonth(Integer userId) {
        DateTime now = DateUtil.date();
        String month = now.toString(DateConstants.DATE_FORMAT_MONTH);
        return userSignRecordService.countRetroSignTimes(userId, month);
    }

    @Override
    public SupSignResponse retroactiveSign(String date, String month) {
        User user = userService.getInfo();
        // 1. 参数校验
        try {
            DateUtil.parse(date, DateConstants.DATE_FORMAT_DATE);
        } catch (Exception e) {
            throw new CrmebException("日期格式不正确，应为yyyy-MM-dd");
        }

        int maxTimes = getMaxRetroSignTimes();
        int usedTimes = getUserRetroSignTimesThisMonth(user.getId());

        if (usedTimes >= maxTimes) {
            throw new CrmebException(StrUtil.format("每月最多补签{}次，您已用完补签机会", maxTimes));
        }

        // 不能补签未来日期
        if (DateUtil.parse(date, DateConstants.DATE_FORMAT_DATE).isAfter(DateUtil.date())) {
            throw new CrmebException("不能补签未来的日期");
        }

        // 2. 获取用户信息
        user = userService.getById(user.getId());
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }

        // 3. 检查是否已签到
        if (userSignRecordService.isSigned(user.getId(), date)) {
            throw new CrmebException("该日期已签到，无需补签");
        }

        // 4. 检查积分是否足够(需要10积分补签)
        int needIntegral = 10;
        if (user.getIntegral() < needIntegral) {
            throw new CrmebException("积分不足，补签需要" + needIntegral + "积分");
        }

        // 5. 获取签到配置
        SignConfigResponse config = getConfig();
        SignConfig baseSignConfig = config.getBaseSignConfig();

        // 6. 计算连续签到天数(需查询补签日期的前后签到记录)
        int continuousDays = calculateContinuousDays(user.getId(), date);

        // 7. 创建补签记录(先创建临时对象，事务中再保存)
        UserSignRecord retroRecord = new UserSignRecord();
        retroRecord.setUid(user.getId());
        retroRecord.setDate(date);
        retroRecord.setDay(continuousDays + 1);
        retroRecord.setIsRetroactive(1); // 标记为补签
        retroRecord.setIntegral(0);
        retroRecord.setExperience(0);
        retroRecord.setAwardIntegral(0);
        retroRecord.setAwardExperience(0);

        // 8. 计算奖励(同正常签到逻辑)
        int integral = 0;
        int experience = 0;

        if (baseSignConfig.getIsIntegral()) {
            retroRecord.setIntegral(baseSignConfig.getIntegral());
            integral += baseSignConfig.getIntegral();
        }
        if (baseSignConfig.getIsExperience()) {
            retroRecord.setExperience(baseSignConfig.getExperience());
            experience += baseSignConfig.getExperience();
        }

        // 连续签到奖励
        List<SignConfig> signConfigList = config.getSignConfigList();
        SignConfig signConfig = signConfigList.stream()
                .filter(e -> e.getDay().equals(retroRecord.getDay()))
                .findAny().orElse(null);

        if (ObjectUtil.isNotNull(signConfig)) {
            if (signConfig.getIsIntegral()) {
                retroRecord.setAwardIntegral(signConfig.getIntegral());
                integral += signConfig.getIntegral();
            }
            if (signConfig.getIsExperience()) {
                retroRecord.setAwardExperience(signConfig.getExperience());
                experience += signConfig.getExperience();
            }
        }

        // 会员加成
        if (user.getIsPaidMember()) {
            List<PaidMemberBenefitsVo> benefitsList = paidMemberService.getBenefitsList();
            for (PaidMemberBenefitsVo b : benefitsList) {
                if (b.getStatus()) {
                    if (b.getName().equals("integralDoubling") && b.getMultiple() > 1 && b.getChannelStr().contains("1")) {
                        integral = integral * b.getMultiple();
                        retroRecord.setIntegral(retroRecord.getIntegral() * b.getMultiple());
                        retroRecord.setAwardIntegral(retroRecord.getAwardIntegral() * b.getMultiple());
                    }
                    if (b.getName().equals("experienceDoubling") && b.getMultiple() > 1 && b.getChannelStr().contains("1")) {
                        experience = experience * b.getMultiple();
                        retroRecord.setExperience(retroRecord.getExperience() * b.getMultiple());
                        retroRecord.setAwardExperience(retroRecord.getAwardExperience() * b.getMultiple());
                    }
                }
            }
        }

        // 9. 设置备注
        StringBuilder mark = new StringBuilder("补签奖励");
        if (integral > 0) mark.append(integral).append("积分");
        if (experience > 0) mark.append(experience).append("经验");
        retroRecord.setMark(mark.toString());

        // 10. 事务处理
        int finalIntegral = integral;
        int finalExperience = experience;
        User finalUser = user;
        Boolean result = transactionTemplate.execute(e -> {
            // 扣除补签所需积分
            userService.updateIntegral(finalUser.getId(), needIntegral, Constants.OPERATION_TYPE_SUBTRACT);

            // 记录积分扣除流水
            UserIntegralRecord deductRecord = new UserIntegralRecord();
            deductRecord.setUid(finalUser.getId());
            deductRecord.setLinkId("0");
            deductRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
            deductRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_SUB);
            deductRecord.setTitle("补签扣除");
            deductRecord.setMark(StrUtil.format("补签{}扣除{}积分", date, needIntegral));
            deductRecord.setIntegral(needIntegral);
            deductRecord.setBalance(finalUser.getIntegral() - needIntegral);
            deductRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
            userIntegralRecordService.save(deductRecord);

            // 保存补签记录
            userSignRecordService.save(retroRecord);

            // 发放奖励积分
            if (finalIntegral > 0) {
                userService.updateIntegral(finalUser.getId(), finalIntegral, Constants.OPERATION_TYPE_ADD);
                UserIntegralRecord integralRecord = new UserIntegralRecord();
                integralRecord.setUid(finalUser.getId());
                integralRecord.setLinkId("0");
                integralRecord.setLinkType(IntegralRecordConstants.INTEGRAL_RECORD_LINK_TYPE_SIGN);
                integralRecord.setType(IntegralRecordConstants.INTEGRAL_RECORD_TYPE_ADD);
                integralRecord.setTitle(IntegralRecordConstants.INTEGRAL_RECORD_TITLE_SIGN);
                integralRecord.setMark(StrUtil.format("补签奖励{}积分", finalIntegral));
                integralRecord.setIntegral(finalIntegral);
                integralRecord.setBalance(finalUser.getIntegral() - needIntegral + finalIntegral);
                integralRecord.setStatus(IntegralRecordConstants.INTEGRAL_RECORD_STATUS_COMPLETE);
                userIntegralRecordService.save(integralRecord);
            }

            // 发放经验奖励
            if (finalExperience > 0) {
                userService.updateExperience(finalUser.getId(), finalExperience, Constants.OPERATION_TYPE_ADD);
                UserExperienceRecord experienceRecord = new UserExperienceRecord();
                experienceRecord.setUid(finalUser.getId());
                experienceRecord.setLinkId("0");
                experienceRecord.setLinkType(ExperienceRecordConstants.EXPERIENCE_RECORD_LINK_TYPE_SIGN);
                experienceRecord.setType(ExperienceRecordConstants.EXPERIENCE_RECORD_TYPE_ADD);
                experienceRecord.setTitle(ExperienceRecordConstants.EXPERIENCE_RECORD_TITLE_SIGN);
                experienceRecord.setMark(StrUtil.format("补签奖励{}经验", finalExperience));
                experienceRecord.setExperience(finalExperience);
                experienceRecord.setBalance(finalUser.getExperience() + finalExperience);
                experienceRecord.setStatus(ExperienceRecordConstants.EXPERIENCE_RECORD_STATUS_CREATE);
                userExperienceRecordService.save(experienceRecord);
            }

            // 更新连续签到天数(需要根据实际情况调整)
            updateContinuousSignDays(finalUser.getId());

            return Boolean.TRUE;
        });

        if (!result) {
            throw new CrmebException("补签失败");
        }

        // 异步处理等级提升
        if (finalExperience > 0) {
            asyncService.userLevelUp(user.getId(), user.getLevel(), user.getExperience() + finalExperience);
        }
        //UserSignRecord lastSignRecord = userSignRecordService.getLastByUid(user.getId());
        SupSignResponse response = new SupSignResponse();
        response.setSignDayNum(continuousDays + 1);
        response.setMakeupCards(maxTimes-usedTimes-1);

        return response;
    }

    /**
     * 计算用户在补签日期的连续签到天数
     * @param userId 用户ID
     * @param retroDate 补签日期(yyyy-MM-dd)
     * @return 补签后的连续签到天数
     */
    private int calculateContinuousDays(Integer userId, String retroDate) {
        // 1. 将补签日期转为DateTime
        DateTime retroDateTime = DateUtil.parse(retroDate, DateConstants.DATE_FORMAT_DATE);

        // 2. 查询补签日期前后的签到记录
        List<UserSignRecord> nearbyRecords = userSignRecordService.findNearbySignRecords(userId, retroDate);

        // 3. 如果没有相邻签到记录，则连续天数为1
        if (CollUtil.isEmpty(nearbyRecords)) {
            return 1;
        }

        // 4. 计算补签前的连续天数(向前计算)
        int beforeDays = calculateContinuousDaysBefore(nearbyRecords, retroDateTime);

        // 5. 计算补签后的连续天数(向后计算)
        int afterDays = calculateContinuousDaysAfter(nearbyRecords, retroDateTime);

        // 6. 总连续天数为前后相加
        return beforeDays + afterDays + 1; // +1是补签当天
    }

    /**
     * 计算补签日期之前的连续天数
     */
    private int calculateContinuousDaysBefore(List<UserSignRecord> records, DateTime retroDate) {
        int days = 0;
        DateTime currentDate = retroDate;

        // 按日期倒序检查之前的连续签到
        for (UserSignRecord record : records) {
            DateTime recordDate = DateUtil.parse(record.getDate(), DateConstants.DATE_FORMAT_DATE);
            if (recordDate.isAfter(retroDate)) {
                continue; // 跳过补签日期之后的记录
            }

            long diffDays = DateUtil.between(recordDate, currentDate, DateUnit.DAY);
            if (diffDays == 1) {
                days++;
                currentDate = recordDate;
            } else if (diffDays > 1) {
                break; // 日期不连续，终止计算
            }
        }

        return days;
    }

    /**
     * 计算补签日期之后的连续天数
     */
    private int calculateContinuousDaysAfter(List<UserSignRecord> records, DateTime retroDate) {
        int days = 0;
        DateTime currentDate = retroDate;

        // 按日期顺序检查之后的连续签到
        for (UserSignRecord record : records) {
            DateTime recordDate = DateUtil.parse(record.getDate(), DateConstants.DATE_FORMAT_DATE);
            if (recordDate.isBefore(retroDate)) {
                continue; // 跳过补签日期之前的记录
            }

            long diffDays = DateUtil.between(currentDate, recordDate, DateUnit.DAY);
            if (diffDays == 1) {
                days++;
                currentDate = recordDate;
            } else if (diffDays > 1) {
                break; // 日期不连续，终止计算
            }
        }

        return days;
    }

    /**
     * 更新用户连续签到天数
     * @param userId 用户ID
     */
    private void updateContinuousSignDays(Integer userId) {
        // 1. 获取用户最近的签到记录（按日期倒序）
        List<UserSignRecord> signRecords = userSignRecordService.findRecentSignRecords(userId, 30); // 最多检查30天内的记录

        if (CollUtil.isEmpty(signRecords)) {
            // 没有签到记录，连续天数为0
            userService.updateSignNumByUid(0, userId);
            return;
        }

        // 2. 按日期排序（升序）
        signRecords.sort(Comparator.comparing(UserSignRecord::getDate));

        // 3. 计算连续签到天数
        int continuousDays = 1; // 至少1天
        DateTime previousDate = DateUtil.parse(signRecords.get(0).getDate(), DateConstants.DATE_FORMAT_DATE);

        // 从第二天开始检查
        for (int i = 1; i < signRecords.size(); i++) {
            DateTime currentDate = DateUtil.parse(signRecords.get(i).getDate(), DateConstants.DATE_FORMAT_DATE);
            long daysBetween = DateUtil.between(previousDate, currentDate, DateUnit.DAY);

            if (daysBetween == 1) {
                // 日期连续
                continuousDays++;
            } else if (daysBetween > 1) {
                // 日期不连续，中断计算
                break;
            }
            previousDate = currentDate;
        }

        // 4. 检查今天是否已签到（如果今天已签到，连续天数不需要减1）
        boolean todaySigned = false;
        String today = DateUtil.today();
        if (signRecords.get(0).getDate().equals(today)) {
            todaySigned = true;
        }

        // 5. 如果最近签到不是今天，且最后签到不是昨天，则连续天数重置
        if (!todaySigned) {
            DateTime lastSignDate = DateUtil.parse(signRecords.get(0).getDate(), DateConstants.DATE_FORMAT_DATE);
            long daysFromLastSign = DateUtil.between(lastSignDate, DateUtil.date(), DateUnit.DAY);

            if (daysFromLastSign > 1) {
                // 超过1天没签到，连续天数重置为0
                continuousDays = 0;
            }
        }

        // 6. 更新用户连续签到天数
        userService.updateSignNumByUid(continuousDays, userId);
    }

}
