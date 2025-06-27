package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.sgin.UserSignRecord;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SignRecordSearchRequest;
import com.zbkj.common.request.UserRecordSearchRequest;
import com.zbkj.common.response.UserSignRecordResponse;

import java.util.List;

/**
*  UserSignRecordService 接口
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
public interface UserSignRecordService extends IService<UserSignRecord> {

    /**
     * 获取用户签到记录
     *
     * @param request 分页参数
     * @return PageInfo
     */
    PageInfo<UserSignRecordResponse> pageRecordList(SignRecordSearchRequest request);

    /**
     * 获取用户最后一条签到记录
     * @param uid 用户id
     * @return UserSignRecord
     */
    UserSignRecord getLastByUid(Integer uid);

    /**
     * 获取某个月的签到记录
     * @param uid 用户id
     * @param month 月份 yyyy-MM
     * @return 签到记录
     */
    List<UserSignRecord> findByMonth(Integer uid, String month);

    /**
     * 获取用户签到记录列表
     * @param uid 用户ID
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    PageInfo<UserSignRecord> findPageByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取用户签到记录(管理端)
     */
    PageInfo<UserSignRecord> getAdminUserSignRecord(UserRecordSearchRequest request);

    /**
     * 判断用户某天是否签到
     * @param userId 用户ID
     * @param date 日期
     * @return Boolean
     */
    Boolean isSigned(Integer userId, String date);

    /**
     * 获取用户最近的签到记录
     * @param userId 用户ID
     * @param limit 查询条数
     * @return 签到记录列表
     */
    List<UserSignRecord> findRecentSignRecords(Integer userId, int limit);

    /**
     * 查询补签日期附近的签到记录
     * @param userId 用户ID
     * @param retroDate 补签日期(yyyy-MM-dd)
     * @return 附近的签到记录(包含补签日期前后的记录)
     */
    List<UserSignRecord> findNearbySignRecords(Integer userId, String retroDate);

    /**
     * 统计用户某月的补签次数
     * @param userId 用户ID
     * @param month 月份(yyyy-MM)
     * @return 补签次数
     */
    int countRetroSignTimes(Integer userId, String month);
}
