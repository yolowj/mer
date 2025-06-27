package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemFormTemp;
import com.zbkj.common.request.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.SystemConfigFormItemConfigRegListVo;
import com.zbkj.common.vo.SystemConfigFormItemVo;
import com.zbkj.common.vo.SystemConfigFormVo;
import com.zbkj.service.dao.SystemFormTempDao;
import com.zbkj.service.service.SystemFormTempService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * SystemFormTempServiceImpl 接口实现
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
public class SystemFormTempServiceImpl extends ServiceImpl<SystemFormTempDao, SystemFormTemp> implements SystemFormTempService {

    @Resource
    private SystemFormTempDao dao;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @return List<SystemFormTemp>
    */
    @Override
    public List<SystemFormTemp> getList(SystemFormTempSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 SystemFormTemp 类的多条件查询
        LambdaQueryWrapper<SystemFormTemp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lambdaQueryWrapper.eq(SystemFormTemp::getId, keywords).
                    or().like(SystemFormTemp::getName, keywords).
                    or().like(SystemFormTemp::getInfo, keywords);
        }
        lambdaQueryWrapper.orderByDesc(SystemFormTemp::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 验证item规则
     * @param systemFormCheckRequest SystemFormCheckRequest 表单数据提交
     */
    @Override
    public void checkForm(SystemFormCheckRequest systemFormCheckRequest) {
        //循环取出item数据， 组合成 key => val 的map格式
        HashMap<String, String> map = new HashMap<>();
        for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormCheckRequest.getFields()) {
            map.put(systemFormItemCheckRequest.getName(), systemFormItemCheckRequest.getValue());
        }

        //取出表单模型的数据
        SystemFormTemp formTemp = getById(systemFormCheckRequest.getId());

        //解析表单规则进行验证
        SystemConfigFormVo systemConfigFormVo;
        try {
            systemConfigFormVo =  JSONObject.parseObject(formTemp.getContent(), SystemConfigFormVo.class);
        } catch (Exception e) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "模板表单 【" + formTemp.getName() + "】 的内容不是正确的JSON格式！");
        }

        SystemConfigFormItemVo systemConfigFormItemVo;
        for (String item : systemConfigFormVo.getFields()) {
            systemConfigFormItemVo = JSONObject.parseObject(item, SystemConfigFormItemVo.class);
            String model = systemConfigFormItemVo.get__vModel__(); //字段 name

            if(systemConfigFormItemVo.get__config__().getRequired() && map.get(model).equals("")) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, systemConfigFormItemVo.get__config__().getLabel() + "不能为空！");
            }
            //正则验证
            checkRule(systemConfigFormItemVo.get__config__().getRegList(), map.get(model),  systemConfigFormItemVo.get__config__().getLabel());
        }
    }

    /**
     * 新增表单模板
     * @param systemFormTempRequest 新增参数
     */
    @Override
    public Boolean add(SystemFormTempRequest systemFormTempRequest) {
        try {
            JSONObject.parseObject(systemFormTempRequest.getContent(), SystemConfigFormVo.class);
        } catch (Exception e) {
            throw new CrmebException(SystemConfigResultCode.FORM_TEMP_PARAMETER_ERROR.setMsgParams(systemFormTempRequest.getName()));
        }
        // 校验表单模板名称唯一
        SystemFormTemp temp = getOneByName(systemFormTempRequest.getName());
        if (ObjectUtil.isNotNull(temp)) {
            throw new CrmebException(SystemConfigResultCode.FORM_TEMP_NAME_REPEAT);
        }
        SystemFormTemp systemFormTemp = new SystemFormTemp();
        BeanUtils.copyProperties(systemFormTempRequest, systemFormTemp);
        return save(systemFormTemp);
    }

    /**
     * 修改表单模板
     * @param id integer id
     * @param systemFormTempRequest 修改参数
     */
    @Override
    public Boolean edit(Integer id, SystemFormTempRequest systemFormTempRequest) {
        try {
            JSONObject.parseObject(systemFormTempRequest.getContent(), SystemConfigFormVo.class);
        } catch (Exception e) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "模板表单 【" + systemFormTempRequest.getName() + "】 的内容不是正确的JSON格式！");
        }
        // 校验表单模板名称唯一
        SystemFormTemp temp = getOneByName(systemFormTempRequest.getName());
        if (ObjectUtil.isNotNull(temp) && !temp.getId().equals(id)) {
            throw new CrmebException(SystemConfigResultCode.FORM_TEMP_NAME_REPEAT);
        }
        SystemFormTemp systemFormTemp = new SystemFormTemp();
        BeanUtils.copyProperties(systemFormTempRequest, systemFormTemp);
        systemFormTemp.setId(id);
        systemFormTemp.setUpdateTime(DateUtil.date());
        return updateById(systemFormTemp);
    }

    /**
     * 通过名称查询详情
     * @param name 表单名称
     * @return SystemFormTemp
     */
    @Override
    public SystemFormTemp getOneByName(String name) {
        LambdaQueryWrapper<SystemFormTemp> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemFormTemp::getName, URLUtil.decode(name));
        lqw.last(" limit 1");
        SystemFormTemp systemFormTemp = dao.selectOne(lqw);
        return systemFormTemp;
    }

    /**
     * 获取表单组件详情
     * @param id ID
     */
    @Override
    public SystemFormTemp getByIdException(Integer id) {
        SystemFormTemp formTemp = getById(id);
        if (ObjectUtil.isNull(formTemp)) {
            throw new CrmebException(SystemConfigResultCode.FORM_TEMP_NOT_EXIST);
        }
        return formTemp;
    }

    /**
     * 验证item规则
     * @param regList List<SystemConfigFormItemConfigRegListVo 正则表达式列表
     * @param value String 验证的值
     * @param name String 提示语字段名称

     * @since 2020-04-16
     */
    private void checkRule(List<SystemConfigFormItemConfigRegListVo> regList, String value, String name) {
        if(regList.size() > 0) {
            for (SystemConfigFormItemConfigRegListVo systemConfigFormItemConfigRegListVo : regList) {
                if(!ValidateFormUtil.regular(value, name, systemConfigFormItemConfigRegListVo.getPattern())) {
                    throw new CrmebException(systemConfigFormItemConfigRegListVo.getMessage());
                }
            }
        }
    }
}

