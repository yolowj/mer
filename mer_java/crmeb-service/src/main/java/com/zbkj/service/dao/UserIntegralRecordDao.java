package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.user.UserIntegralRecord;
import com.zbkj.common.response.IntegralRecordPageResponse;

import java.util.List;
import java.util.Map;

/**
 * 用户积分记录表 Mapper 接口
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
public interface UserIntegralRecordDao extends BaseMapper<UserIntegralRecord> {

    List<IntegralRecordPageResponse> findRecordPageListByPlat(Map<String, Object> map);

}
