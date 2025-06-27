// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import request from '@/utils/request';

/**
 * 获取版权信息
 */
export function copyrightInfoApi() {
  return request({
    url: '/admin/platform/copyright/get/info',
    method: 'get',
  });
}

/**
 * 保存版权信息
 */
export function saveCrmebCopyRight(data) {
  return request({
    url: '/admin/platform/copyright/update/company/info',
    method: 'post',
    data,
  });
}
