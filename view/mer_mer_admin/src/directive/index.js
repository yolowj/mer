// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import hasRole from './permission/hasRole';
import hasPermi from './permission/hasPermi';
import dialogDrag from './dialog/drag';
import dialogDragWidth from './dialog/dragWidth';
import dialogDragHeight from './dialog/dragHeight';
import copy from './copy/copy';
import longpress from './longpress/longpress';

const install = function (Vue) {
  Vue.directive('hasRole', hasRole);
  Vue.directive('hasPermi', hasPermi);
  Vue.directive('dialogDrag', dialogDrag);
  Vue.directive('dialogDragWidth', dialogDragWidth);
  Vue.directive('dialogDragHeight', dialogDragHeight);
  Vue.directive('copy', copy); //v-copy 复制指令
  Vue.directive('longpress', longpress); //v-longpress 长按指令
};

if (window.Vue) {
  window['hasRole'] = hasRole;
  window['hasPermi'] = hasPermi;
  Vue.use(install); // eslint-disable-line
}

export default install;
