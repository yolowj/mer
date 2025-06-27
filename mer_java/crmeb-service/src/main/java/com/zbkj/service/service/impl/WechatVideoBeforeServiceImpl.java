package com.zbkj.service.service.impl;

import com.zbkj.service.service.WechatVideoBeforeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
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
public class WechatVideoBeforeServiceImpl implements WechatVideoBeforeService {

    private static final Logger logger = LoggerFactory.getLogger(WechatVideoBeforeServiceImpl.class);

//    @Autowired
//    private RestTemplateUtil restTemplateUtil;
//
//    @Autowired
//    private WechatService wechatService;
//
//    @Autowired
//    private PayComponentCatService payComponentCatService;

//    /**
//     * 上传图片
//     *
//     * @return 图片上传结果
//     */
//    @Override
//    public WechatVideoUploadImageResponseVo shopImgUpload(ShopUploadImgRequest request) {
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
//        params.add("resp_type", request.getRespType());
//        params.add("upload_type", request.getUploadType());
//        params.add("img_url", request.getImgUrl());
//        String uploadResult = restTemplateUtil.postFormData(StrUtil.format(WeChatConstants.WECHAT_SHOP_IMG_UPLOAD, miniAccessToken),
//                params);
//        return JSONObject.parseObject(uploadResult,  WechatVideoUploadImageResponseVo.class);
//    }
//
//    /**
//     * 上传品牌信息
//     *
//     * @param requestVo 待上传品牌参数
//     * @return 审核单Id
//     */
//    @Override
//    public ShopAuditBrandResponseVo shopAuditBrand(ShopAuditBrandRequestVo requestVo) {
//        // 参数校验
//        Map<String, Object> brandMap = assembleAuditBrandMap(requestVo);
//        // 获取accessToken
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        // 请求微信接口
//        String url = StrUtil.format(WeChatConstants.WECHAT_SHOP_AUDIT_AUDIT_BRAND, miniAccessToken);
//        String postStringData = restTemplateUtil.postStringData(url, JSONObject.toJSONString(brandMap));
//        JSONObject jsonObject = JSONObject.parseObject(postStringData);
//        WxUtil.checkResult(jsonObject);
//        ShopAuditBrandResponseVo brandResponseVo = JSONObject.parseObject(postStringData, ShopAuditBrandResponseVo.class);
//        return brandResponseVo;
//    }
//
//    /**
//     * 组装上传品牌Map对象
//     * @param requestVo 上传品牌请求参数
//     * @return 商品品牌Map
//     */
//    private Map<String, Object> assembleAuditBrandMap(ShopAuditBrandRequestVo requestVo) {
//        Map<String, Object> map = CollUtil.newHashMap();
//        Map<String, Object> reqMap = CollUtil.newHashMap();
//        Map<String, Object> infoMap = CollUtil.newHashMap();
//        ShopAuditBrandRequestItemVo requestItemVo = requestVo.getAuditReq();
//        ShopAuditBrandRequestItemDataVo brandInfo = requestItemVo.getBrandInfo();
//        infoMap.put("brand_audit_type", brandInfo.getBrandAuditType());
//        infoMap.put("trademark_type", brandInfo.getTrademarkType());
//        infoMap.put("brand_management_type", brandInfo.getBrandManagementType());
//        infoMap.put("commodity_origin_type", brandInfo.getCommodityOriginType());
//        infoMap.put("brand_wording", brandInfo.getBrandWording());
//
//        if (CollUtil.isNotEmpty(brandInfo.getTrademarkChangeCertificate())) {
//            infoMap.put("trademark_change_certificate", brandInfo.getTrademarkChangeCertificate());
//        }
//        infoMap.put("trademark_registrant_nu", brandInfo.getTrademarkRegistrantNu());
//        if (CollUtil.isNotEmpty(brandInfo.getImportedGoodsForm())) {
//            infoMap.put("imported_goods_form", brandInfo.getImportedGoodsForm());
//        }
//        switch (brandInfo.getBrandAuditType()) {
//            case 1:
//                infoMap.put("trademark_registration_certificate", brandInfo.getTrademarkRegistrationCertificate());
//                infoMap.put("trademark_registrant", brandInfo.getTrademarkRegistrant());
//                infoMap.put("trademark_authorization_period", brandInfo.getTrademarkAuthorizationPeriod());
//                break;
//            case 2:
//                infoMap.put("trademark_registration_application", brandInfo.getTrademarkRegistrationApplication());
//                infoMap.put("trademark_applicant", brandInfo.getTrademarkApplicant());
//                infoMap.put("trademark_application_time", brandInfo.getTrademarkApplicationTime());
//                break;
//            case 3:
//                infoMap.put("sale_authorization", brandInfo.getSaleAuthorization());
//                infoMap.put("trademark_registration_certificate", brandInfo.getTrademarkRegistrationCertificate());
//                infoMap.put("trademark_registrant", brandInfo.getTrademarkRegistrant());
//                infoMap.put("trademark_authorization_period", brandInfo.getTrademarkAuthorizationPeriod());
//                break;
//            case 4:
//                infoMap.put("sale_authorization", brandInfo.getSaleAuthorization());
//                infoMap.put("trademark_registration_application", brandInfo.getTrademarkRegistrationApplication());
//                infoMap.put("trademark_applicant", brandInfo.getTrademarkApplicant());
//                infoMap.put("trademark_application_time", brandInfo.getTrademarkApplicationTime());
//                break;
//            default:
//                infoMap.put("sale_authorization", brandInfo.getSaleAuthorization());
//                infoMap.put("trademark_registration_certificate", brandInfo.getTrademarkRegistrationCertificate());
////                infoMap.put("trademark_change_certificate", brandInfo.getTrademarkChangeCertificate());
//                infoMap.put("trademark_registrant", brandInfo.getTrademarkRegistrant());
////                infoMap.put("trademark_registrant_nu", brandInfo.getTrademarkRegistrantNu());
//                infoMap.put("trademark_authorization_period", brandInfo.getTrademarkAuthorizationPeriod());
//                infoMap.put("trademark_registration_application", brandInfo.getTrademarkRegistrationApplication());
//                infoMap.put("trademark_applicant", brandInfo.getTrademarkApplicant());
//                infoMap.put("trademark_application_time", brandInfo.getTrademarkApplicationTime());
////                infoMap.put("imported_goods_form", brandInfo.getImportedGoodsForm());
//                break;
//        }
//        reqMap.put("license", requestItemVo.getLicense());
//        reqMap.put("brand_info", infoMap);
//
//        map.put("audit_req", reqMap);
//        return map;
//    }
//
//    /**
//     * 上传类目资质
//     *
//     * @param requestVo 类目资质参数
//     * @return 类目资质结果
//     */
//    @Override
//    public ShopAuditCategoryResponseVo shopAuditCategory(ShopAuditCategoryRequestVo requestVo) {
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        String postStringData = restTemplateUtil.postStringData(StrUtil.format(WeChatConstants.WECHAT_SHOP_AUDIT_AUDIT_CATEGORY, miniAccessToken),
//                JSONObject.toJSONString(requestVo));
//
//        ShopAuditCategoryResponseVo resultForAuditCat = JSONObject.parseObject(postStringData, ShopAuditCategoryResponseVo.class);
//        if(!resultForAuditCat.getErrcode().equals(0)){
//            throw new CrmebException("提交审核失败:"+resultForAuditCat.getErrmsg());
//        }
//        // 更新系统自定义申请状态
//        LambdaUpdateWrapper<PayComponentCat> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
//        lambdaUpdateWrapper.eq(PayComponentCat::getThirdCatId, requestVo.getAudit_req().getCategory_info().getLevel3());
//        lambdaUpdateWrapper.set(PayComponentCat::getStatus, PayComponentCatStatusEnum.WECHAT_REVIEW_ING.getCode());
//        lambdaUpdateWrapper.set(PayComponentCat::getAuditTime, CrmebDateUtil.nowDateTime());
//        lambdaUpdateWrapper.set(PayComponentCat::getAuditId, resultForAuditCat.getAudit_id());
//        lambdaUpdateWrapper.set(PayComponentCat::getAuditQualificationReq, JSON.toJSONString(requestVo));
//        payComponentCatService.update(lambdaUpdateWrapper);
//        return resultForAuditCat;
//    }
//
//    /**
//     * 获取类目审核结果
//     *
//     * @param request 待审核类目id
//     * @return 审核结果
//     */
//    @Override
//    public ShopAuditResultResponseVo shopAuditResult(ShopAuditResultCategoryAndBrandRequestVo request) {
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        String postStringData = restTemplateUtil.postStringData(StrUtil.format(WeChatConstants.WECHAT_SHOP_AUDIT_RESULT, miniAccessToken),
//                JSONObject.toJSONString(request));
//        JSONObject jsonObjectResult = JSON.parseObject(postStringData);
//        WxUtil.checkResult(jsonObjectResult);
//        JSONObject auditDataResult = jsonObjectResult.getJSONObject("data");
//        payComponentCatService.getAuditResultOrAuditCallBack(auditDataResult, request.getAudit_id());
//        return JSONObject.parseObject(postStringData, ShopAuditResultResponseVo.class);
//    }
//
//    /**
//     * 获取小程序资质
//     * 接口调用请求说明
//     *
//     * 获取曾经提交的小程序审核资质
//     *
//     * 请求类目会返回多次的请求记录，请求品牌只会返回最后一次的提交记录
//     *
//     * 图片经过转链，请使用高版本 chrome 浏览器打开
//     *
//     * 如果曾经没有提交，没有储存历史文件，或是获取失败，接口会返回1050006
//     *
//     * 注：该接口返回的是曾经在小程序方提交过的审核，非组件的入驻审核！
//     * @param request  请求参数 1 	类目 2 	品牌
//     * @return 资质结果
//     */
//    @Override
//    public ShopAuditGetMiniAppCertificateRequestVo shopAuditGetMinCertificate(ShopAuditGetMiniAppCertificateRequestVo request) {
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        String postStringData = restTemplateUtil.postStringData(StrUtil.format(WeChatConstants.WECHAT_SHOP_AUDIT_GET_MINIAPP_CERTIFICATE, miniAccessToken),
//                JSONObject.toJSONString(request));
//        return JSONObject.parseObject(postStringData, ShopAuditGetMiniAppCertificateRequestVo.class);
//    }
//
//    @Override
//    public WechatVideoUploadImageResponseVo shopImgUploadTest(MultipartFile file, Integer respType, Integer uploadType, String imgUrl) {
//        String miniAccessToken = wechatService.getMiniAccessToken();
//        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
////        HashMap<String, Object> params = new HashMap<>();
//        params.add("resp_type", respType);
//        params.add("upload_type", uploadType);
//        if (StrUtil.isNotBlank(imgUrl)) {
//            params.add("img_url", imgUrl);
//        }
//        params.add("file", file);
//        String uploadResult = restTemplateUtil.postFormData(StrUtil.format(WeChatConstants.WECHAT_SHOP_IMG_UPLOAD, miniAccessToken),
//                params);
//        return JSONObject.parseObject(uploadResult,  WechatVideoUploadImageResponseVo.class);
//    }
}
