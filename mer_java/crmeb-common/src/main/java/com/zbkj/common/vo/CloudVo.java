package com.zbkj.common.vo;

import lombok.Data;

/**
 * 文件信息
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
@Data
public class CloudVo {

    //域名空间
    private String domain;

    //accessKey
    private String accessKey;

    //secretKey
    private String secretKey;

    //bucketName
    private String bucketName;

    //节点
    private String region;
}
