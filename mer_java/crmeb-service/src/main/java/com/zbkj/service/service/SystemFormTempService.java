package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.system.SystemFormTemp;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemFormCheckRequest;
import com.zbkj.common.request.SystemFormTempRequest;
import com.zbkj.common.request.SystemFormTempSearchRequest;

import java.util.List;

/**
 * SystemFormTempService 接口
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
public interface SystemFormTempService extends IService<SystemFormTemp> {

    /**
     * 列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @return List<SystemFormTemp>
     */
    List<SystemFormTemp> getList(SystemFormTempSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 验证item规则
     * @param systemFormCheckRequest SystemFormCheckRequest 表单数据提交
     */
    void checkForm(SystemFormCheckRequest systemFormCheckRequest);

    /**
     * 新增表单模板
     * @param systemFormTempRequest 新增参数
     */
    Boolean add(SystemFormTempRequest systemFormTempRequest);

    /**
     * 修改表单模板
     * @param id integer id
     * @param systemFormTempRequest 修改参数
     */
    Boolean edit(Integer id, SystemFormTempRequest systemFormTempRequest);

    /**
     * 通过名称查询详情
     * @param name 表单名称
     * @return SystemFormTemp
     */
    SystemFormTemp getOneByName(String name);

    /**
     * 获取表单组件详情
     * @param id ID
     */
    SystemFormTemp getByIdException(Integer id);
}
