<template>
  <div class="divBox">
    <el-card class="box-card" shadow="never" :bordered="false">
      <div slot="header" class="clearfix">
        <el-form inline>
          <el-form-item>
            <el-input v-model.trim="tableFrom.keywords" placeholder="请输入用户名称" class="selWidth">
              <el-button slot="append" icon="el-icon-search" @click="search" />
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <el-table v-loading="loading" :data="tableData.data" width="800px" size="small">
        <el-table-column label="" width="40">
          <template slot-scope="scope">
            <el-radio
              v-model="templateRadio"
              :label="scope.row.uid"
              @change.native="getTemplateRow(scope.$index, scope.row)"
              >&nbsp;</el-radio
            >
          </template>
        </el-table-column>
        <el-table-column prop="uid" label="ID" min-width="60" />
        <el-table-column prop="nickname" label="微信用户名称" min-width="130" />
        <el-table-column label="用户头像" min-width="80">
          <template slot-scope="scope">
            <div class="demo-image__preview line-heightOne">
              <el-image class="tabImage" :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]"  fit="cover"/>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="性别" min-width="80">
          <template slot-scope="scope">
            <span>{{ scope.row.sex | saxFilter }}</span>
          </template>
        </el-table-column>
        <el-table-column label="地区" min-width="130">
          <template slot-scope="scope">
            <span>{{ scope.row.addres }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="block">
        <el-pagination
          :page-sizes="[10, 20, 30, 40]"
          :page-size="tableFrom.limit"
          :current-page="tableFrom.page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.total"
          @size-change="handleSizeChange"
          @current-change="pageChange"
        />
      </div>
    </el-card>
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

import { userListApi } from '@/api/user';
export default {
  name: 'UserList',
  filters: {
    saxFilter(status) {
      const statusMap = {
        0: '未知',
        1: '男',
        2: '女',
      };
      return statusMap[status];
    },
    statusFilter(status) {
      const statusMap = {
        wechat: '微信用户',
        routine: '小程序用户',
      };
      return statusMap[status];
    },
  },
  data() {
    return {
      templateRadio: 0,
      loading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 10,
        keywords: '',
      },
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getTemplateRow(idx, row) {
      this.$emit('getTemplateRow', row);
    },
    // 列表
    getList() {
      this.loading = true;
      userListApi(this.tableFrom)
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.loading = false;
        })
        .catch((res) => {
          this.$message.error(res.message);
          this.loading = false;
        });
    },
    search() {
      this.loading = true;
      userListApi({ keywords: this.tableFrom.keywords })
        .then((res) => {
          this.tableData.data = res.list;
          this.tableData.total = res.total;
          this.loading = false;
        })
        .catch((res) => {
          this.$message.error(res.message);
          this.loading = false;
        });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList();
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList();
    },
  },
};
</script>

<style scoped></style>
