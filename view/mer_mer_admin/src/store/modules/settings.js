// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

import defaultSettings from '@/settings';
import Cache from '@/plugins/cache';
import { frontDomainApi } from '@/api/authInformation';
const { showSettings, tagsView, fixedHeader, sidebarLogo, topNav, sideTheme, navIcon } = defaultSettings;
const storageSetting = Cache.local.has('layout-setting') ? Cache.local.getJSON('layout-setting') : '';
const state = {
  theme: storageSetting.theme || '#409EFF',
  sideTheme: storageSetting.sideTheme || sideTheme,
  topNav: storageSetting.topNav === undefined ? topNav : storageSetting.topNav,
  showSettings: showSettings,
  tagsView: storageSetting.tagsView === undefined ? tagsView : storageSetting.tagsView,
  fixedHeader: storageSetting.fixedHeader === undefined ? fixedHeader : storageSetting.fixedHeader,
  sidebarLogo: storageSetting.sidebarLogo === undefined ? sidebarLogo : storageSetting.sidebarLogo,
  navIcon: storageSetting.navIcon === undefined ? navIcon : storageSetting.navIcon,
  mediaDomain: localStorage.getItem('mediaDomain') || '', //图片域名
  frontDomain: localStorage.getItem('frontDomain') || '', //移动端域名
  mobileTheme: localStorage.getItem('mobileTheme') || '',
};
const mutations = {
  CHANGE_SETTING: (state, { key, value }) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value;
    }
  },
  // 线上文件、图片域名地址
  SET_mediaDomain(state, mediaDomain) {
    state.mediaDomain = mediaDomain;
    if (!mediaDomain) localStorage.removeItem('mediaDomain');
  },
  // 移动端域名
  SET_FrontDomain(state, frontDomain) {
    state.frontDomain = frontDomain;
    localStorage.setItem('frontDomain', frontDomain);
    if (!frontDomain) localStorage.removeItem('frontDomain');
  },
  // 移动端主题色
  SET_mobileThemeColor(state, mobileTheme) {
    state.mobileTheme = mobileTheme;
    localStorage.setItem('mobileTheme', mobileTheme);
    if (!mobileTheme) localStorage.removeItem('mobileTheme');
  },
};

const actions = {
  changeSetting({ commit }, data) {
    commit('CHANGE_SETTING', data);
  },
  // 获取移动端域名
  getFrontDomain({ commit, state }) {
    frontDomainApi().then((res) => {
      commit('SET_FrontDomain', res);
    });
  },
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};
