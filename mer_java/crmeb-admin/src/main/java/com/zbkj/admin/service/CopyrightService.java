package com.zbkj.admin.service;

import com.zbkj.admin.copyright.CopyrightInfoResponse;
import com.zbkj.admin.copyright.CopyrightUpdateInfoRequest;
import com.zbkj.common.vo.MyRecord;

/**
 * 版权服务
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
public interface CopyrightService {

    /**
     * 获取版权信息
     */
    CopyrightInfoResponse getInfo();

    /**
     * 编辑公司版权信息
     */
    Boolean updateCompanyInfo(CopyrightUpdateInfoRequest request);

    /**
     * 获取商户版权信息
     */
    String getCompanyInfo();
}
