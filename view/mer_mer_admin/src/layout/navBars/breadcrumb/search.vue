<template>
  <div class="layout-search-dialog">
    <el-dialog :visible.sync="isShowSearch" width="540px" destroy-on-close :modal="false" fullscreen :show-close="true">
      <el-autocomplete
        v-model="menuQuery"
        :fetch-suggestions="menuSearch"
        placeholder="菜单搜索：支持中文、路由路径"
        prefix-icon="el-icon-search"
        ref="layoutMenuAutocompleteRef"
        @select="onHandleSelect"
        @blur="onSearchBlur"
      >
        <template slot-scope="{ item }">
          <div><i :class="item.icon" class="mr10"></i>{{ item.title }}</div>
        </template>
      </el-autocomplete>
    </el-dialog>
  </div>
</template>

<script>
import { getAllSiderMenu } from '@/utils/system.js';
export default {
  name: 'layoutBreadcrumbSearch',
  data() {
    return {
      isShowSearch: false,
      menuQuery: '',
      tagsViewList: [],
    };
  },
  methods: {
    // 搜索弹窗打开
    openSearch() {
      this.menuQuery = '';
      this.isShowSearch = true;
      this.initTageView();
      this.$nextTick(() => {
        this.$refs.layoutMenuAutocompleteRef.focus();
      });
    },
    // 搜索弹窗关闭
    closeSearch() {
      setTimeout(() => {
        this.isShowSearch = false;
      }, 150);
    },
    // 菜单搜索数据过滤
    menuSearch(queryString, cb) {
      let results = queryString ? this.tagsViewList.filter(this.createFilter(queryString)) : this.tagsViewList;
      cb(results);
    },
    // 菜单搜索过滤
    createFilter(queryString) {
      return (restaurant) => {
        return (
          restaurant.path.toLowerCase().indexOf(queryString.toLowerCase()) > -1 ||
          restaurant.title.toLowerCase().indexOf(queryString.toLowerCase()) > -1
        );
      };
    },
    // 初始化菜单数据
    initTageView() {
      if (this.tagsViewList.length > 0) return false;
      this.tagsViewList = getAllSiderMenu(this.$store.state.user.menuList);
      // this.$store.state.tagsViewRoutes.tagsViewRoutes.map((v) => {
      // 	if (!v.isHide) this.tagsViewList.push({ ...v });
      // });
    },
    // 当前菜单选中时
    onHandleSelect(item) {
      let { path, redirect } = item;
      if (redirect) this.$router.push(redirect);
      else this.$router.push(path);
      this.closeSearch();
    },
    // input 失去焦点时
    onSearchBlur() {
      this.closeSearch();
    },
  },
};
</script>

<style scoped lang="scss">
.layout-search-dialog {
  ::v-deep .el-dialog {
    box-shadow: unset !important;
    border-radius: 0 !important;
    background: rgba(0, 0, 0, 0.5);
  }
  ::v-deep .el-autocomplete {
    width: 560px;
    position: absolute;
    top: 100px;
    left: 50%;
    transform: translateX(-50%);
  }
}
::v-deep .el-dialog__header {
  border: none !important;
}
::v-deep .el-input--small .el-input__inner {
  height: 36px;
  line-height: 36px;
}
</style>
