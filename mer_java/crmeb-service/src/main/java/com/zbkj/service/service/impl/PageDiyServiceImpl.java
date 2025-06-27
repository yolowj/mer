package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.page.PageDiy;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.page.PageDiyEditNameRequest;
import com.zbkj.common.request.page.PageDiyRequest;
import com.zbkj.common.response.page.PageDiyResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.page.PageDiyDao;
import com.zbkj.service.service.PageDiyService;
import com.zbkj.service.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dazongzi
 * @description PageDiyServiceImpl 接口实现
 * @date 2023-05-16
 */
@Service
public class PageDiyServiceImpl extends ServiceImpl<PageDiyDao, PageDiy> implements PageDiyService {

    private static final Logger logger = LoggerFactory.getLogger(PageDiyServiceImpl.class);

    @Resource
    private PageDiyDao dao;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
     * 列表
     *
     * @return List<PageDiy>
     * @author dazongzi
     * @since 2023-05-16
     */
    @Override
    public List<PageDiy> getList(String name, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PageDiy> lambdaQueryWrapper = Wrappers.lambdaQuery();
        // 列表查询时忽略掉value字段，以免影响查询速度
        lambdaQueryWrapper.select(PageDiy.class, i -> !i.getColumn().equals("value"));
        if (StrUtil.isNotBlank(name)) {
            lambdaQueryWrapper.like(PageDiy::getName, URLUtil.decode(name));
        }
        lambdaQueryWrapper.eq(PageDiy::getMerId, 0);
        lambdaQueryWrapper.eq(PageDiy::getIsDel, 0);
        lambdaQueryWrapper.orderByDesc(PageDiy::getIsDefault, PageDiy::getAddTime);
        List<PageDiy> pageDiys = dao.selectList(lambdaQueryWrapper);
        return pageDiys;
    }

    /**
     * 商户DIY列表
     */
    @Override
    public PageInfo<PageDiy> findMerchantPage(CommonSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        // 列表查询时忽略掉value字段，以免影响查询速度
        lqw.select(PageDiy.class, i -> !i.getColumn().equals("value"));
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lqw.like(PageDiy::getName, URLUtil.decode(request.getKeywords()));
        }
        lqw.eq(PageDiy::getMerId, systemAdmin.getMerId());
        lqw.eq(PageDiy::getIsDel, 0);
        lqw.orderByDesc(PageDiy::getIsDefault, PageDiy::getId);
        Page<PageDiy> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<PageDiy> pageDiyList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, pageDiyList);
    }

    /**
     * 保存diy
     *
     * @param pageDiy diy数据
     */
    @Override
    public PageDiy savePageDiy(PageDiy pageDiy) {
        String adminApiPath = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(adminApiPath)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        // 检查diy模版名称唯一
        checkPageDiyNameUnique(pageDiy.getName(), 0, 0);
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        // 通配符去掉关键子 再存储
        jsonContext.delete("$..*[?(@ == '" + adminApiPath + "')]");
        pageDiy.setValue(jsonContext.jsonString());
        save(pageDiy);
        return pageDiy;
    }

    /**
     * 更新diy
     *
     * @param pageDiy
     */
    @Override
    public Boolean editPageDiy(PageDiy pageDiy) {
        String adminApiPath = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(adminApiPath)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        // 检查diy模版名称唯一
        checkPageDiyNameUnique(pageDiy.getName(), pageDiy.getId(), 0);
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        // 通配符去掉关键子 再存储
        jsonContext.delete("$..*[?(@ == '" + adminApiPath + "')]");
        pageDiy.setValue(jsonContext.jsonString());
        pageDiy.setUpdateTime(DateUtil.date());
        return dao.updateById(pageDiy) > 0;
    }

    /**
     * 编辑diy名称
     *
     * @param pageDiyEditNameRequest 待编辑名称对象
     */
    @Override
    public Boolean editPageDiyName(PageDiyEditNameRequest pageDiyEditNameRequest) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy pageDiy = getByIdException(pageDiyEditNameRequest.getId());
        if (!pageDiy.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        String name = URLUtil.decode(pageDiyEditNameRequest.getName());
        // 检查diy模版名称唯一
        checkPageDiyNameUnique(name, pageDiyEditNameRequest.getId(), admin.getMerId());
        LambdaUpdateWrapper<PageDiy> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(PageDiy::getName, name);
        wrapper.eq(PageDiy::getId, pageDiyEditNameRequest.getId());
        return update(wrapper);
    }

    /**
     * 设置DIY首页模版
     * isDefault =1 是平台首页 2=商户 diy后期商户再处理
     *
     * @param diyId 被设置为首页的diy模版id
     * @return 设置结果
     */
    @Override
    public Boolean setDiyDefault(Integer diyId) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy pageDiy = getByIdException(diyId);
        if (!admin.getMerId().equals(pageDiy.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        return transactionTemplate.execute(e -> {
            clearDiyDefault(admin.getMerId());
            LambdaUpdateWrapper<PageDiy> wrapper = Wrappers.lambdaUpdate();
            wrapper.set(PageDiy::getIsDefault, 1);
            wrapper.eq(PageDiy::getMerId, admin.getMerId());
            wrapper.eq(PageDiy::getId, diyId);
            update(wrapper);
            return Boolean.TRUE;
        });
    }

    /**
     * 清除DIY默认模板
     */
    private void clearDiyDefault(Integer merId) {
        LambdaUpdateWrapper<PageDiy> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(PageDiy::getIsDefault, 0);
        wrapper.eq(PageDiy::getMerId, merId);
        update(wrapper);
    }

    private PageDiy getByIdException(Integer id) {
        PageDiy pageDiy = getById(id);
        if (ObjectUtil.isNull(pageDiy) || pageDiy.getIsDel() > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        return pageDiy;
    }

    /**
     * 针对Admin
     * 根据id加载diy模版配置
     *
     * @param id 对应的模版id
     *           描述：
     *           id=0加载商城首页的，也就是setDiyPageHome后的数据，平台端会默认设置已经有首页的数据，也会预制
     *           id>1时加载对应的模版即可
     */
    @Override
    public PageDiy getDiyPageByPageIdForAdmin(Integer id) {
        PageDiy pageDiy = null;
        if (0 == id) {
            LambdaQueryWrapper<PageDiy> getHomeDefault = Wrappers.lambdaQuery();
            getHomeDefault.eq(PageDiy::getIsDefault, 1);
            pageDiy = getOne(getHomeDefault);
        } else {
            pageDiy = getById(id);
        }

        if (ObjectUtil.isNull(pageDiy)) throw new CrmebException(SystemConfigResultCode.PAGE_DIY_NOT_EXIST);

        String modifiedJsonString = getModifiedJsonString(pageDiy.getValue());
        pageDiy.setValue(modifiedJsonString);
        return pageDiy;
    }

    /**
     * 获取DIY默认模板ID
     */
    @Override
    public Integer getDefaultId() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.select(PageDiy::getId);
        lqw.eq(PageDiy::getMerId, admin.getMerId());
        lqw.eq(PageDiy::getIsDefault, 1);
        lqw.eq(PageDiy::getIsDel, 0);
        lqw.last(" limit 1");
        PageDiy pageDiy = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(pageDiy) ? pageDiy.getId() : 0;
    }

    /**
     * 保存商户diy模板
     */
    @Override
    public PageDiy saveMerchantPageDiy(PageDiyRequest request) {
        String adminApiPath = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(adminApiPath)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        // 检查diy模版名称唯一
        checkPageDiyNameUnique(request.getName(), 0, admin.getMerId());
        PageDiy pageDiy = new PageDiy();
        BeanUtils.copyProperties(request, pageDiy);
        pageDiy.setId(null);
        pageDiy.setValue(JSON.toJSONString(request.getValue()));
        pageDiy.setMerId(admin.getMerId());
        pageDiy.setIsDefault((ObjectUtil.isNotNull(request.getIsDefault()) && request.getIsDefault() > 0) ? 1 : 0);
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        // 通配符去掉关键子 再存储
        jsonContext.delete("$..*[?(@ == '" + adminApiPath + "')]");
        pageDiy.setValue(jsonContext.jsonString());
        boolean save = save(pageDiy);
        if (!save) {
            throw new CrmebException(CommonResultCode.ERROR, "保存diy模板失败");
        }
        return pageDiy;
    }

    /**
     * 编辑商户diy模板
     */
    @Override
    public Boolean editMerchantPageDiy(PageDiyRequest request) {
        String adminApiPath = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_API_URL);
        if (StrUtil.isBlank(adminApiPath)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "应用设置中 微信小程序数据配置 或者 支付回调地址以及网站地址 配置不全");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy tempPageDiy = getByIdException(request.getId());
        if (!tempPageDiy.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        // 检查diy模版名称唯一
        checkPageDiyNameUnique(request.getName(), tempPageDiy.getId(), admin.getMerId());
        PageDiy pageDiy = new PageDiy();
        BeanUtils.copyProperties(request, pageDiy);
        pageDiy.setValue(JSON.toJSONString(request.getValue()));
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        // 通配符去掉关键子 再存储
        jsonContext.delete("$..*[?(@ == '" + adminApiPath + "')]");
        pageDiy.setValue(jsonContext.jsonString());
        pageDiy.setUpdateTime(DateUtil.date());
        return dao.updateById(pageDiy) > 0;
    }

    /**
     * 删除DIY模板
     */
    @Override
    public Boolean deletePageDiy(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy pageDiy = getByIdException(id);
        if (!pageDiy.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        return removeById(id);
    }

    /**
     * 获取商户端装修模板详情
     */
    @Override
    public PageDiyResponse getMerchantPageDiyInfo(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        PageDiy pageDiy;
        if (0 == id) {
            LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
            lqw.eq(PageDiy::getIsDefault, 1);
            lqw.eq(PageDiy::getMerId, admin.getMerId());
            pageDiy = getOne(lqw);
        } else {
            pageDiy = getByIdException(id);
        }
        if (ObjectUtil.isNull(pageDiy) || !pageDiy.getMerId().equals(admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前DIY模版不存在");
        }
        String modifiedJsonString = getModifiedJsonString(pageDiy.getValue());
        pageDiy.setValue(modifiedJsonString);
        PageDiyResponse response = new PageDiyResponse();
        BeanUtils.copyProperties(pageDiy, response);
        response.setValue(JSON.parseObject(pageDiy.getValue()));
        return response;

    }

    /**
     * 获取移动端商户装修默认模板
     */
    @Override
    public PageDiyResponse getMerchantPageDiyInfoByFront(Integer merId, Integer id) {
        PageDiy pageDiy;
        if (ObjectUtil.isNotNull(id) && id > 0) {
            pageDiy = getDetailByMerId(id, merId);
        } else {
            pageDiy = getDefaultByMerId(merId);
        }
        if (ObjectUtil.isNull(pageDiy)) {
            throw new CrmebException(SystemConfigResultCode.PAGE_DIY_NOT_EXIST);
        }

        // 优化front比必要的字段 提高diy组件渲染效率
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        jsonContext.delete("$..*[?(@.name == 'goodList')].itemStyle.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].tabConfig.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].typeConfig.list");
//        jsonContext.delete("$..*[?(@.name == 'goodList')].selectConfig");
        jsonContext.delete("$..*[?(@.name == 'goodList')].goodsSort.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].goodsList.list[*].attrValue");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].activeValueBrand");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].goodsList");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].selectConfig");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].productList..*.goods[*].attrValue");

        PageDiyResponse response = new PageDiyResponse();
        BeanUtils.copyProperties(pageDiy, response);
        String modifiedJsonString = getModifiedJsonString(jsonContext.jsonString());
        response.setValue(JSON.parseObject(modifiedJsonString));
        return response;
    }

    /**
     * 获取商户创建默认模板
     */
    @Override
    public PageDiy getMerchantDefDiy() {
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.eq(PageDiy::getMerId, -9);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    private PageDiy getDetailByMerId(Integer id, Integer merId) {
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.eq(PageDiy::getId, id);
        lqw.eq(PageDiy::getMerId, merId);
        lqw.eq(PageDiy::getIsDel, 0);
        return dao.selectOne(lqw);
    }

    private PageDiy getDefaultByMerId(Integer merId) {
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.eq(PageDiy::getMerId, merId);
        lqw.eq(PageDiy::getIsDefault, 1);
        lqw.eq(PageDiy::getIsDel, 0);
        return dao.selectOne(lqw);
    }

    /**
     * 根据id加载diy模版配置
     *
     * @param id 对应的模版id
     *           描述：
     *           id=0加载商城首页的，也就是setDiyPageHome后的数据，平台端会默认设置已经有首页的数据，也会预制
     *           id>1时加载对应的模版即可
     */
    @Override
    public PageDiyResponse getDiyPageByPageIdForFront(Integer id) {
        PageDiy pageDiy;
        if (0 == id) {
            LambdaQueryWrapper<PageDiy> getHomeDefault = Wrappers.lambdaQuery();
            getHomeDefault.eq(PageDiy::getIsDefault, 1);
            getHomeDefault.eq(PageDiy::getMerId, 0);
            pageDiy = getOne(getHomeDefault);
        } else {
            pageDiy = getById(id);
        }

        if (ObjectUtil.isNull(pageDiy)) throw new CrmebException(SystemConfigResultCode.PAGE_DIY_NOT_EXIST);


        // 优化front比必要的字段 提高diy组件渲染效率
        DocumentContext jsonContext = JsonPath.parse(pageDiy.getValue());
        jsonContext.delete("$..*[?(@.name == 'goodList')].itemStyle.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].tabConfig.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].typeConfig.list");
//        jsonContext.delete("$..*[?(@.name == 'goodList')].selectConfig");
        jsonContext.delete("$..*[?(@.name == 'goodList')].goodsSort.list");
        jsonContext.delete("$..*[?(@.name == 'goodList')].goodsList.list[*].attrValue");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].activeValueBrand");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].goodsList");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].selectConfig");
        jsonContext.delete("$..*[?(@.name == 'homeTab')].productList..*.goods[*].attrValue");

        PageDiyResponse response = new PageDiyResponse();
        BeanUtils.copyProperties(pageDiy, response);
        String modifiedJsonString = getModifiedJsonString(jsonContext.jsonString());
        response.setValue(JSON.parseObject(modifiedJsonString));
        return response;
    }

    /**
     * 检查diy模版名称唯一
     *
     * @param pageDiyName 当前要检查的模版名称
     * @param id          更新时的 diy id
     */
    private void checkPageDiyNameUnique(String pageDiyName, Integer id, Integer merId) {
        LambdaQueryWrapper<PageDiy> lqw = Wrappers.lambdaQuery();
        lqw.eq(PageDiy::getName, pageDiyName);
        if (id > 0) {
            lqw.ne(PageDiy::getId, id);
        }
        lqw.eq(PageDiy::getMerId, merId);
        Integer count = dao.selectCount(lqw);
        if (count > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前模版名称已经存在，请修改后再保存！");
        }
    }

////////////////////////////////// 内部处理json配置中的素材地址方法 START

    /**
     * 调用diy配置数据后结合本地配置，传递给递归方法替换
     *
     * @param diyValue 当前待操作的diy对象，其实仅仅用到了value
     * @return 替换后的json对象
     */
    private String getModifiedJsonString(String diyValue) {
        // 使用 Gson 解析 JSON 数据
        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(diyValue, JsonElement.class);

        // 替换指定前缀的键
        String adminApiPath = UploadConstants.UPLOAD_FILE_KEYWORD;
        String newPrefix = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_LOCAL_UPLOAD_URL);
        JsonElement modifiedJsonElement = replaceJsonValue(jsonElement, adminApiPath, newPrefix);

        // 将修改后的 JSON 数据转换回字符串
        return gson.toJson(modifiedJsonElement);
    }

    /**
     * 根据系统关键字匹配素材前缀，匹配后追加系统中对应的素材前缀
     *
     * @param jsonElement 当前操作的json对象
     * @param keyword     匹配的关键字
     * @param newValue    需要追加的前缀
     * @return 操作后的正确json数据
     */
    private JsonElement replaceJsonValue(JsonElement jsonElement, String keyword, String newValue) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject newJsonObject = new JsonObject();
            for (String key : jsonObject.keySet()) {
                JsonElement value = jsonObject.get(key);
                if (value.isJsonPrimitive() && value.getAsString().startsWith(keyword)) {
                    newJsonObject.addProperty(key, newValue + "/" + value.getAsString());
                } else {
                    newJsonObject.add(key, replaceJsonValue(value, keyword, newValue)); // 递归处理子元素
                }
            }
            return newJsonObject;
        } else if (jsonElement.isJsonArray()) {
            // 如果数组元素也可能含有关键字，可以按需处理
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            JsonArray newJsonArray = new JsonArray();
            for (JsonElement element : jsonArray) {
                newJsonArray.add(replaceJsonValue(element, keyword, newValue)); // 递归处理数组元素
            }
            return newJsonArray;
        } else {
            return jsonElement;
        }
    }

    ////////////////////////////////// 内部处理json配置中的素材地址方法 END
}

