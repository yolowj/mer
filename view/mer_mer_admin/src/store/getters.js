// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

const getters = {
  sidebar: (state) => state.app.sidebar,
  size: (state) => state.app.size,
  device: (state) => state.app.device,
  visitedViews: (state) => state.tagsView.visitedViews,
  cachedViews: (state) => state.tagsView.cachedViews,
  token: (state) => state.user.token,
  avatar: (state) => state.user.avatar,
  name: (state) => state.user.name,
  introduction: (state) => state.user.introduction,
  roles: (state) => state.user.roles,
  permission_routes: (state) => state.permission.routes,
  permissions: (state) => state.user.permissions,
  sidebarRouters: (state) => state.permission.sidebarRouters,
  errorLogs: (state) => state.errorLog.logs,
  isLogin: (state) => state.user.isLogin,
  merPlatProductClassify: (state) => state.product.merPlatProductClassify,
  merProductClassify: (state) => state.product.merProductClassify,
  productBrand: (state) => state.product.productBrand,
  merchantClassify: (state) => state.merchant.merchantClassify,
  merchantType: (state) => state.merchant.merchantType,
  shippingTemplates: (state) => state.product.shippingTemplates,
  mediaDomain: (state) => state.settings.mediaDomain,
  systemFormList: (state) => state.settings.systemFormList,
  frontDomain: (state) => state.settings.frontDomain,
  mobileTheme: (state) => state.settings.mobileTheme,
};
export default getters;
