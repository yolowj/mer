<template>
  <div class="divBox">
    <div class="header clearfix">
      <div class="container">
        <el-form inline @submit.native.prevent>
          <el-form-item>
            <el-input
              @keyup.enter.native="handerSearch"
              v-model.trim="listPram.title"
              placeholder="请输入标题"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-cascader
              v-model="listPram.cid"
              :options="categoryTreeData"
              :props="categoryProps"
              placeholer="选择分类"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button size="mini" type="primary" @click="handerSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <el-table
      :data="listData.list"
      style="width: 100%"
      size="mini"
      max-height="400"
      tooltip-effect="dark"
      highlight-current-row
    >
      <el-table-column label="" width="55">
        <template slot-scope="{ row, index }">
          <el-radio v-model="templateRadio" :label="row.id" @change.native="getTemplateRow(row)">&nbsp;</el-radio>
        </template>
      </el-table-column>
      <el-table-column label="图片" min-width="80">
        <template slot-scope="scope">
          <div class="demo-image__preview line-heightOne">
            <el-image :src="scope.row.cover" :preview-src-list="[scope.row.cover]" />
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="150" />
      <el-table-column prop="visit" label="浏览量" min-width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.visit | filterEmpty }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="150" />
    </el-table>
    <div class="block mb20">
      <el-pagination
        :current-page="listPram.page"
        :page-sizes="constants.page.limit"
        :layout="constants.page.layout"
        :total="listData.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------
import * as articleApi from '@/api/article.js';
import * as categoryApi from '@/api/categoryApi.js';
import * as selfUtil from '@/utils/ZBKJIutil.js';
import {articleCategoryListApi} from "@/api/article.js";
export default {
  // name: "list",
  props: {
    handle: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      templateRadio: '',
      imgList: [],
      constants: this.$constants,
      listPram: {
        title: null,
        cid: null,
        page: 1,
        limit: this.$constants.page.limit[0],
      },
      listData: { list: [], total: 0 },
      editDialogConfig: {
        visible: false,
        data: {},
        isEdit: 0, // 0=add 1=edit
      },
      categoryTreeData: [],
      categoryProps: {
        value: 'id',
        label: 'name',
        children: 'child',
        expandTrigger: 'hover',
        checkStrictly: true,
        emitPath: false,
      },
    };
  },
  mounted() {
    this.handlerGetListData(this.listPram);
    this.handlerGetCategoryTreeData();
  },
  methods: {
    getTemplateRow(row) {
      this.$emit('getArticle', row);
    },
    handerSearch() {
      this.listPram.page = 1;
      this.handlerGetListData(this.listPram);
    },
    handlerGetListData(pram) {
      articleApi.ListArticle(pram).then((data) => {
        this.listData = data;
      });
    },
    handlerGetCategoryTreeData() {
      articleCategoryListApi().then((data) => {
        this.categoryTreeData = data;
      });
    },
    handlerHideDialog() {
      this.handlerGetListData(this.listPram);
      this.editDialogConfig.visible = false;
    },
    handlerDelete(rowData) {
      this.$confirm('确定删除当前数据', '提示').then((result) => {
        articleApi.DelArticle(rowData).then((data) => {
          this.$message.success('删除数据成功');
          this.handlerGetListData(this.listPram);
        });
      });
    },
    handleSizeChange(val) {
      this.listPram.limit = val;
      this.handlerGetListData(this.listPram);
    },
    handleCurrentChange(val) {
      this.listPram.page = val;
      this.handlerGetListData(this.listPram);
    },
  },
};
</script>

<style scoped></style>
