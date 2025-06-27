package com.zbkj.service.service.impl;

import com.zbkj.service.service.WechatVideoSpuService;
import org.springframework.stereotype.Service;

/**
 * 视频号交易组件服务实现类
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
public class WechatVideoSpuServiceImpl implements WechatVideoSpuService {
//    private final Logger logger = LoggerFactory.getLogger(WechatVideoSpuServiceImpl.class);
//
//    @Autowired
//    private RestTemplateUtil restTemplateUtil;
//
//    @Autowired
//    private WechatService wechatService;
//
//
//    /**
//     * 获取品牌列表
//     * @return List<ShopBrandVo>
//     */
//    @Override
//    public List<ShopBrandVo> getShopBrandList() {
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_ACCOUNT_GET_BRAND_LIST, miniAccessToken);
//        String s = restTemplateUtil.postStringData(url, "{}");
//        JSONObject jsonObject = JSONObject.parseObject(s);
//        WxUtil.checkResult(jsonObject);
//        JSONArray dataArr = jsonObject.getJSONArray("data");
//        if(ObjectUtil.isNull(dataArr) || dataArr.size() == 0){
//            throw new CrmebException("暂无品牌信息");
//        }
//        List<ShopBrandVo> brandVoList = JSONArray.parseArray(dataArr.toJSONString(), ShopBrandVo.class);
//        return brandVoList;
//    }
//
//    /**
//     * 获取类目列表  获取已申请成功的类类目列表 全部的分类在接入前类目详情
//     *
//     * @return
//     */
//    @Override
//    public List<ShopCatListResponse> getShopCategoryList() {
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_ACCOUNT_GET_CATEGORY_LIST, miniAccessToken);
//        String s = restTemplateUtil.postStringData(url, "{}");
//        JSONObject jsonObject = JSONObject.parseObject(s);
//        WxUtil.checkResult(jsonObject);
//        List<ShopCatListResponse> catListResponses = JSONArray.parseArray(jsonObject.getJSONObject("data").toJSONString(), ShopCatListResponse.class);
//        return catListResponses;
//    }
//
//    /**
//     * 更新商家信息
//     *
//     * @param request
//     * @return 更新结果
//     */
//    @Override
//    public Boolean shopUpdateInfo(ShopAccountUpdateVo request) {
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_ACCOUNT_UPDATE_INFO, miniAccessToken);
//        String s = restTemplateUtil.postStringData(url, JSONObject.toJSONString(request));
//        JSONObject jsonObject = JSONObject.parseObject(s);
//        WxUtil.checkResult(jsonObject);
//        return "0".equals(jsonObject.getString("errcode"));
//    }
//
//    /**
//     * 获取商家信息
//     *
//     * @return 更新时的参数
//     */
//    @Override
//    public ShopAccountUpdateVo shopGetAccountInfo() {
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_ACCOUNT_GET_INFO, miniAccessToken);
//        String s = restTemplateUtil.postStringData(url, "{}");
//        JSONObject jsonObject = JSONObject.parseObject(s);
//        WxUtil.checkResult(jsonObject);
//        return JSONObject.parseObject(s, ShopAccountUpdateVo.class);
//    }
//
//    /**
//     * 获取商品类目
//     * 获取所有三级类目及其资质相关信息 注意：该接口拉到的是【全量】三级类目数据，数据回包大小约为2MB。 所以请商家自己做好缓存，
//     * 不要频繁调用（有严格的频率限制），该类目数据不会频率变动，推荐商家每天调用一次更新商家自身缓存
//     * 若该类目资质必填，则新增商品前，必须先通过该类目资质申请接口进行资质申请; 若该类目资质不需要，则该类目自动拥有，无需申请，
//     * 如依然调用，会报错105003； 若该商品资质必填，则新增商品时，带上商品资质字段。 接入类目审核回调，才可获取审核结果。
//     * @return ShopCatVo
//     */
//    @Override
//    public List<ShopCatDetailVo> getShopCat() {
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_CAT_GET_URL, miniAccessToken);
//        String s = restTemplateUtil.postStringData(url, "{}");
//        JSONObject jsonObject = JSONObject.parseObject(s);
//        WxUtil.checkResult(jsonObject);
//        ShopCatVo shopCatVo = JSONObject.parseObject(jsonObject.toJSONString(), ShopCatVo.class);
//        return shopCatVo.getThirdCatList();
//    }
//
//    /**
//     * 添加商品
//     * @return ShopSpuAddResponseVo
//     */
//    @Override
//    public ShopSpuAddResponseVo shopSpuAdd(ShopSpuAddVo shopSpuVo) {
//        Map<String, Object> spuAddMap = assembleSpuAddMap(shopSpuVo);
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_SPU_ADD_URL, miniAccessToken);
//        String postStringData = restTemplateUtil.postStringData(url, JSONObject.toJSONString(spuAddMap));
//        JSONObject jsonObject = JSONObject.parseObject(postStringData);
//        WxUtil.checkResult(jsonObject);
//        ShopSpuAddResponseVo addResponseVo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ShopSpuAddResponseVo.class);
//        return addResponseVo;
//    }
//
//    /**
//     * 组装商品添加Map
//     * @param shopSpuVo 商品Vo
//     * @return 商品添加Map
//     */
//    private Map<String, Object> assembleSpuAddMap(ShopSpuAddVo shopSpuVo) {
//        Map<String, Object> map = CollUtil.newHashMap();
//        map.put("out_product_id", shopSpuVo.getOutProductId());
//        map.put("title", shopSpuVo.getTitle());
//        map.put("path", shopSpuVo.getPath());
//        map.put("head_img", shopSpuVo.getHeadImg());
//        if (CollUtil.isNotEmpty(shopSpuVo.getQualificationPics())) {
//            map.put("qualification_pics", shopSpuVo.getQualificationPics());
//        }
//        if (ObjectUtil.isNotNull(shopSpuVo.getDescInfo())) {
//            Map<String, String> infoMap = CollUtil.newHashMap();
//            infoMap.put("desc", shopSpuVo.getDescInfo().getDesc());
//        }
//        map.put("third_cat_id", shopSpuVo.getThirdCatId());
//        map.put("brand_id", shopSpuVo.getBrandId());
//        List<Map<String, Object>> skuMapLsit = shopSpuVo.getSkus().stream().map(sku -> {
//            Map<String, Object> skuMap = CollUtil.newHashMap();
//            skuMap.put("out_product_id", sku.getOutProductId());
//            skuMap.put("out_sku_id", sku.getOutSkuId());
//            skuMap.put("thumb_img", sku.getThumbImg());
//            skuMap.put("sale_price", sku.getSalePrice());
//            skuMap.put("market_price", sku.getMarketPrice());
//            skuMap.put("stock_num", sku.getStockNum());
//            List<Map<String, String>> attrMapList = sku.getSkuAttrs().stream().map(attr -> {
//                Map<String, String> attrMap = CollUtil.newHashMap();
//                attrMap.put("attr_key", attr.getAttrKey());
//                attrMap.put("attr_value", attr.getAttrValue());
//                return attrMap;
//            }).collect(Collectors.toList());
//            skuMap.put("sku_attrs", attrMapList);
//            return skuMap;
//        }).collect(Collectors.toList());
//        map.put("skus", skuMapLsit);
//        return map;
//    }
//
//    /**
//     * 删除商品
//     * @return Boolean
//     */
//    @Override
//    public Boolean shopSpuDel(ShopSpuCommonVo shopSpuCommonVo) {
//        if (ObjectUtil.isNull(shopSpuCommonVo.getProductId()) && StrUtil.isBlank(shopSpuCommonVo.getOutProductId())) {
//            throw new CrmebException("商品ID不能为空");
//        }
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_SPU_DEL_URL, miniAccessToken);
//        Map<String, Object> map = new HashMap<>();
//        if (ObjectUtil.isNotNull(shopSpuCommonVo.getProductId())) {
//            map.put("product_id", shopSpuCommonVo.getProductId());
//        }
//        if (StrUtil.isNotBlank(shopSpuCommonVo.getOutProductId())) {
//            map.put("out_product_id", shopSpuCommonVo.getOutProductId());
//        }
//        String mapData = restTemplateUtil.postMapData(url, map);
//        JSONObject jsonObject = JSONObject.parseObject(mapData);
//        WxUtil.checkResult(jsonObject);
//        return Boolean.TRUE;
//    }
//
//    /**
//     * 撤回商品审核
//     * 对于审核中（edit_status=2）的商品无法重复提交，需要调用此接口，使商品流转进入未审核的状态（edit_status=1）,即可重新提交商品。
//     */
//    @Override
//    public Boolean shopSpuDelAudit(ShopSpuCommonVo shopSpuCommonVo) {
//        if (ObjectUtil.isNull(shopSpuCommonVo.getProductId()) && StrUtil.isBlank(shopSpuCommonVo.getOutProductId())) {
//            throw new CrmebException("商品ID不能为空");
//        }
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_SPU_DEL_AUDIT_URL, miniAccessToken);
//        Map<String, Object> map = new HashMap<>();
//        if (ObjectUtil.isNotNull(shopSpuCommonVo.getProductId())) {
//            map.put("product_id", shopSpuCommonVo.getProductId());
//        }
//        if (StrUtil.isNotBlank(shopSpuCommonVo.getOutProductId())) {
//            map.put("out_product_id", shopSpuCommonVo.getOutProductId());
//        }
//        String mapData = restTemplateUtil.postMapData(url, map);
//        JSONObject jsonObject = JSONObject.parseObject(mapData);
//        WxUtil.checkResult(jsonObject);
//        return Boolean.TRUE;
//    }
//
//    /**
//     * 获取商品
//     * @return ShopSpuVo
//     */
//    @Override
//    public ShopSpuVo shopSpuGet(ShopSpuCommonVo shopSpuCommonVo) {
//        if (ObjectUtil.isNull(shopSpuCommonVo.getProductId()) && StrUtil.isBlank(shopSpuCommonVo.getOutProductId())) {
//            throw new CrmebException("商品ID不能为空");
//        }
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_SPU_GET_URL, miniAccessToken);
//        Map<String, Object> map = new HashMap<>();
//        if (ObjectUtil.isNotNull(shopSpuCommonVo.getProductId())) {
//            map.put("product_id", shopSpuCommonVo.getProductId());
//        }
//        if (StrUtil.isNotBlank(shopSpuCommonVo.getOutProductId())) {
//            map.put("out_product_id", shopSpuCommonVo.getOutProductId());
//        }
//        map.put("need_edit_spu", shopSpuCommonVo.getNeedEditSpu());
//        String mapData = restTemplateUtil.postMapData(url, map);
//        JSONObject jsonObject = JSONObject.parseObject(mapData);
//        WxUtil.checkResult(jsonObject);
//        ShopSpuVo shopSpuVo = JSONObject.parseObject(jsonObject.getJSONObject("spu").toJSONString(), ShopSpuVo.class);
//        return shopSpuVo;
//    }
//
//    /**
//     * 获取商品列表
//     * @return ShopSpuListResponseVo
//     */
//    @Override
//    public ShopSpuListResponseVo shopSpuGetList(ShopSpuListRequestVo shopSpuListRequestVo) {
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_SPU_GET_LIST_URL, miniAccessToken);
//        Map<String, Object> map = new HashMap<>();
//        if (StrUtil.isNotBlank(shopSpuListRequestVo.getStartCreateTime())) {
//            map.put("start_create_time", shopSpuListRequestVo.getStartCreateTime());
//        }
//        if (StrUtil.isNotBlank(shopSpuListRequestVo.getEndCreateTime())) {
//            map.put("end_create_time", shopSpuListRequestVo.getEndCreateTime());
//        }
//        if (StrUtil.isNotBlank(shopSpuListRequestVo.getStartUpdateTime())) {
//            map.put("start_update_time", shopSpuListRequestVo.getStartUpdateTime());
//        }
//        if (StrUtil.isNotBlank(shopSpuListRequestVo.getEndUpdateTime())) {
//            map.put("end_update_time", shopSpuListRequestVo.getEndUpdateTime());
//        }
//        if (ObjectUtil.isNotNull(shopSpuListRequestVo.getStatus())) {
//            map.put("status", shopSpuListRequestVo.getStatus());
//        }
//        map.put("need_edit_spu", shopSpuListRequestVo.getNeedEditSpu());
//        map.put("page", shopSpuListRequestVo.getPage());
//        map.put("page_size", shopSpuListRequestVo.getPageSize());
//
//        String mapData = restTemplateUtil.postMapData(url, map);
//        JSONObject jsonObject = JSONObject.parseObject(mapData);
//        WxUtil.checkResult(jsonObject);
//        ShopSpuListResponseVo shopSpuListResponseVo = JSONObject.parseObject(jsonObject.toJSONString(), ShopSpuListResponseVo.class);
//        return shopSpuListResponseVo;
//    }
//
//    /**
//     * 更新商品
//     * 注意：更新成功后会更新到草稿数据并直接提交审核，审核完成后有回调，也可通过get接口的edit_status查看是否通过审核。
//     * @return ShopSpuAddResponseVo
//     */
//    @Override
//    public ShopSpuAddResponseVo shopSpuUpdate(ShopSpuAddVo shopSpuVo) {
//        Map<String, Object> spuUpdateMap = assembleSpuAddMap(shopSpuVo);
//        System.out.println("spuUpdateMap = " + spuUpdateMap);
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_SPU_UPDATE_URL, miniAccessToken);
//        String postStringData = restTemplateUtil.postStringData(url, JSONObject.toJSONString(spuUpdateMap));
//        JSONObject jsonObject = JSONObject.parseObject(postStringData);
//        WxUtil.checkResult(jsonObject);
//        ShopSpuAddResponseVo updateResponseVo = JSONObject.parseObject(jsonObject.getJSONObject("data").toJSONString(), ShopSpuAddResponseVo.class);
//        return updateResponseVo;
//    }
//
//    /**
//     * 上架商品
//     * @return Boolean
//     */
//    @Override
//    public Boolean shopSpuPuton(ShopSpuCommonVo shopSpuCommonVo) {
//        if (ObjectUtil.isNull(shopSpuCommonVo.getProductId()) && StrUtil.isBlank(shopSpuCommonVo.getOutProductId())) {
//            throw new CrmebException("商品ID不能为空");
//        }
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_SPU_LISTING_URL, miniAccessToken);
//        Map<String, Object> map = new HashMap<>();
//        if (ObjectUtil.isNotNull(shopSpuCommonVo.getProductId())) {
//            map.put("product_id", shopSpuCommonVo.getProductId());
//        }
//        if (StrUtil.isNotBlank(shopSpuCommonVo.getOutProductId())) {
//            map.put("out_product_id", shopSpuCommonVo.getOutProductId());
//        }
//        String mapData = restTemplateUtil.postMapData(url, map);
//        JSONObject jsonObject = JSONObject.parseObject(mapData);
//        WxUtil.checkResult(jsonObject);
//        return Boolean.TRUE;
//    }
//
//    /**
//     * 下架商品
//     * @return Boolean
//     */
//    @Override
//    public Boolean shopSpuPutdown(ShopSpuCommonVo shopSpuCommonVo) {
//        if (ObjectUtil.isNull(shopSpuCommonVo.getProductId()) && StrUtil.isBlank(shopSpuCommonVo.getOutProductId())) {
//            throw new CrmebException("商品ID不能为空");
//        }
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_SPU_DELISTING_URL, miniAccessToken);
//        Map<String, Object> map = new HashMap<>();
//        if (ObjectUtil.isNotNull(shopSpuCommonVo.getProductId())) {
//            map.put("product_id", shopSpuCommonVo.getProductId());
//        }
//        if (StrUtil.isNotBlank(shopSpuCommonVo.getOutProductId())) {
//            map.put("out_product_id", shopSpuCommonVo.getOutProductId());
//        }
//        String mapData = restTemplateUtil.postMapData(url, map);
//        JSONObject jsonObject = JSONObject.parseObject(mapData);
//        WxUtil.checkResult(jsonObject);
//        return Boolean.TRUE;
//    }


}
