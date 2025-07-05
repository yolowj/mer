<template>
  <div class="divBox">
    <div class="container_box">
      <el-card class="box-card flex-1" :body-style="{ padding: '0 20px 20px' }" :bordered="false" shadow="never">
        <div class="acea-row" v-loading="listLoading">
          <div class="flex-1">
            <el-tabs v-if="tabList.length > 0" v-model="currentTab" @tab-click="tabChange" class="list-tabs">
              <el-tab-pane v-for="(item, index) in tabList" :key="index" :name="item.value" :label="item.title" />
            </el-tabs>
            <el-form
              ref="formValidate"
              class="formValidate"
              :model="formValidate"
              label-width="73px"
              @submit.native.prevent
            >
              <div v-if="currentTab == 1 && checkPermi(['platform:marketing:activity:new:people:present:config'])">
                <h2 class="form_label">规则设置</h2>
                <el-form-item label="活动状态:">
                  <el-switch
                    :width="56"
                    v-model="formValidate.newPeopleSwitch"
                    active-text="开启"
                    inactive-text="关闭"
                  />
                  <p class="desc mt10">
                    活动开启商城新注册用户可获得下方设置的优惠券福利，活动关闭之后新注册的用户不会赠送新人福利
                  </p>
                </el-form-item>
                <el-form-item label="活动对象:">
                  <span>开启活动期间所有注册商城的用户</span>
                </el-form-item>
                <el-form-item label="优惠券:">
                  <div class="grid_box mb10" v-if="couponList.length">
                    <el-table :data="couponList" style="width: 310px" size="small">
                      <el-table-column prop="id" label="优惠券ID" min-width="80" />
                      <el-table-column prop="name" label="优惠券名称" min-width="200" :show-overflow-tooltip="true" />
                      <el-table-column prop="money" label="优惠券面值 ( ¥ )" min-width="90" />
                      <el-table-column prop="minPrice" label="优惠券门槛" min-width="120">
                        <template slot-scope="scope">
                          <span class="_type">满{{ scope.row.minPrice }}元可用</span>
                        </template>
                      </el-table-column>
                      <el-table-column prop="startTime" label="使用期限" min-width="150">
                        <template slot-scope="scope">
                          <div v-if="scope.row.isDel">优惠券已删除</div>
                          <div v-else>
                            <div v-if="scope.row.isFixedTime">
                              {{ getTime(scope.row.useStartTime) + ' ~ ' + getTime(scope.row.useEndTime) + '可用' }}
                            </div>
                            <div v-else>{{ '领取后' + scope.row.day + '天内可用' }}</div>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column prop="stock" label="剩余张数" min-width="90">
                        <template slot-scope="scope">
                          <span class="_sales">{{
                            !scope.row.isLimited ? '不限量' : '剩余' + scope.row.lastTotal + '张'
                          }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column label="操作" width="100" fixed="right">
                        <template slot-scope="scope">
                          <a @click="delItem(scope.row.id, scope.$index)">删除</a>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                  <a @click="addCoupon">+ 添加优惠券</a>
                </el-form-item>
                <el-form-item>
                  <el-button
                    size="small"
                    type="primary"
                    class="mr10"
                    v-hasPermi="['platform:marketing:activity:new:people:present:edit']"
                    v-debounceClick="confirmEdit"
                    >保存
                  </el-button>
                </el-form-item>
              </div>
              <div v-if="currentTab == 2 && checkPermi(['platform:marketing:activity:birthday:present:config'])">
                <h2 class="form_label">规则设置</h2>
                <el-form-item label="活动状态:">
                  <el-switch
                    :width="56"
                    v-model="formValidate.birthdaySwitch"
                    active-text="开启"
                    inactive-text="关闭"
                  />
                  <p class="desc mt10">活动开启商城用户可获得下方设置的生日福利，活动关闭之后用户生日不会赠送福利。</p>
                </el-form-item>
                <el-form-item label="活动对象:">
                  <span>商城已经设置生日的用户</span>
                </el-form-item>
                <el-form-item label="优惠券:">
                  <div class="grid_box mb10" v-if="couponList.length">
                    <el-table :data="couponList" style="width: 310px" size="small">
                      <el-table-column prop="id" label="优惠券ID" min-width="80" />
                      <el-table-column prop="name" label="优惠券名称" min-width="200" :show-overflow-tooltip="true" />
                      <el-table-column prop="money" label="优惠券面值 ( ¥ )" min-width="90" />
                      <el-table-column prop="minPrice" label="优惠券门槛" min-width="120">
                        <template slot-scope="scope">
                          <span class="_type">满{{ scope.row.minPrice }}元可用</span>
                        </template>
                      </el-table-column>
                      <el-table-column prop="startTime" label="使用期限" min-width="150">
                        <template slot-scope="scope">
                          <div v-if="scope.row.isDel">优惠券已删除</div>
                          <div v-else>
                            <div v-if="scope.row.isFixedTime">
                              {{ getTime(scope.row.useStartTime) + ' ~ ' + getTime(scope.row.useEndTime) + '可用' }}
                            </div>
                            <div v-else>{{ '领取后' + scope.row.day + '天内可用' }}</div>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column prop="stock" label="剩余张数" min-width="90">
                        <template slot-scope="scope">
                          <span class="_sales">{{
                            !scope.row.isLimited ? '不限量' : '剩余' + scope.row.lastTotal + '张'
                          }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column label="操作" width="100" fixed="right">
                        <template slot-scope="scope">
                          <a @click="delItem(scope.row.id, scope.$index)">删除</a>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                  <a @click="addCoupon">+ 添加优惠券</a>
                </el-form-item>
                <el-form-item>
                  <el-button
                    size="small"
                    type="primary"
                    class="mr10"
                    v-hasPermi="['platform:marketing:activity:birthday:present:edit']"
                    v-debounceClick="confirmEdit"
                    >保存
                  </el-button>
                </el-form-item>
              </div>
              <div v-if="currentTab == 3 && checkPermi(['platform:marketing:activity:membersDay:present:config'])">
                <h2 class="form_label">规则设置</h2>
                <el-form-item label="月会员日:">
                  <el-select v-model="formValidate.selectedDay" placeholder="请选择日期（1-31）">
                    <el-option :key="0"  :label="'不设置'"   :value="0">
                    </el-option>
                    <el-option
                      v-for="day in 31"
                      :key="day"
                      :label="`${day}号`"
                      :value="day">
                    </el-option>
                  </el-select>
                </el-form-item>


                <el-form-item label="周会员日:"   >
                    <el-select v-model="formValidate.selectedWeek" placeholder="请选择星期"     @change="handleWeekChange">
                      <el-option
                        v-for="day in weekDays"
                        :key="day.value"
                        :label="day.label"

                        :value="day.value">
                      </el-option>
                    </el-select>
                </el-form-item>

                <el-form-item label="活动状态:">
                  <el-switch
                    :width="56"
                    v-model="formValidate.membersDaySwitch"
                    active-text="开启"
                    inactive-text="关闭"
                  />
                  <p class="desc mt10">活动开启商城用户可获得下方设置的会员日/会员周福利，活动关闭之后用户会员日/会员周不会赠送福利。</p>
                </el-form-item>
                <el-form-item label="活动对象:">
                  <span>商品已经设置会员日的商品</span>
                </el-form-item>
                <el-form-item label="优惠券:">
                  <div class="grid_box mb10" v-if="couponList.length">
                    <el-table :data="couponList" style="width: 310px" size="small">
                      <el-table-column prop="id" label="优惠券ID" min-width="80" />
                      <el-table-column prop="name" label="优惠券名称" min-width="200" :show-overflow-tooltip="true" />
                      <el-table-column prop="money" label="优惠券面值 ( ¥ )" min-width="90" />
                      <el-table-column prop="minPrice" label="优惠券门槛" min-width="120">
                        <template slot-scope="scope">
                          <span class="_type">满{{ scope.row.minPrice }}元可用</span>
                        </template>
                      </el-table-column>
                      <el-table-column prop="startTime" label="使用期限" min-width="150">
                        <template slot-scope="scope">
                          <div v-if="scope.row.isDel">优惠券已删除</div>
                          <div v-else>
                            <div v-if="scope.row.isFixedTime">
                              {{ getTime(scope.row.useStartTime) + ' ~ ' + getTime(scope.row.useEndTime) + '可用' }}
                            </div>
                            <div v-else>{{ '领取后' + scope.row.day + '天内可用' }}</div>
                          </div>
                        </template>
                      </el-table-column>
                      <el-table-column prop="stock" label="剩余张数" min-width="90">
                        <template slot-scope="scope">
                          <span class="_sales">{{
                              !scope.row.isLimited ? '不限量' : '剩余' + scope.row.lastTotal + '张'
                            }}</span>
                        </template>
                      </el-table-column>
                      <el-table-column label="操作" width="100" fixed="right">
                        <template slot-scope="scope">
                          <a @click="delItem(scope.row.id, scope.$index)">删除</a>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                  <a @click="addCoupon">+ 添加优惠券</a>
                </el-form-item>
                <el-form-item>
                  <el-button
                    size="small"
                    type="primary"
                    class="mr10"
                    v-hasPermi="['platform:marketing:activity:membersDay:present:edit']"
                    v-debounceClick="confirmEdit"
                  >保存
                  </el-button>
                </el-form-item>
              </div>
            </el-form>
          </div>
          <div v-if="currentTab === '1'" class="proview acea-row row-center-wrapper">
            <img :src="proviewImg" alt="" />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>
<script>
import {
  canSendListApi,
  newPeoplePresentEditApi,
  newPeoplePresentConfigApi,
  birthdayPresentConfigApi,
  membersDayConfigApi,
  birthdayPresentEditApi,
  membersDayEditApi
} from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'newGift',
  data() {
    return {
      currentTab: '1',
      selectedDay: 0,
      selectedWeek: 0, // 存储选中的星期值（如 1、2、3...）
      weekDays: [
        { value: 0, label: '不设置' },
        { value: 1, label: '星期一' },
        { value: 2, label: '星期二' },
        { value: 3, label: '星期三' },
        { value: 4, label: '星期四' },
        { value: 5, label: '星期五' },
        { value: 6, label: '星期六' },
        { value: 7, label: '星期日' }, // 注意：JS 中星期日是 0
      ],
      tabList: [
        { value: '1', title: '新人礼' },
        { value: '2', title: '生日有礼' },
        { value: '3', title: '会员日' },
      ],
      formValidate: {
        selectedWeek: 0,
        selectedDay: 0,
        newPeopleSwitch: false,
        birthdaySwitch: false,
        membersDaySwitch: false,
        couponIdList: [],
      },
      visibleCoupon: false,
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        page: 1,
        limit: 10,
        keywords: '',
      },
      multipleSelection: [],
      couponList: [],
      keyNum: 0,
      proviewImg: require('@/assets/imgs/new_gift_modal.png'),
    };
  },
  filters: {
    receiveType(val) {
      const typeObj = {
        1: '用户领取',
        2: '商品赠送券',
        3: '平台活动使用',
      };
      return typeObj[val];
    },
  },
  watch: {
    currentTab: {
      handler: function (val) {
        this.getConfig();
      },
      immediate: false,
      deep: true,
    },
  },
  mounted() {
    if (checkPermi(['platform:marketing:activity:new:people:present:config'])) this.getConfig();
  },
  methods: {
    checkPermi,
    getTime(time) {
      let reg = new RegExp('-', 'g'); //g代表全部
      return time.split(' ')[0].replace(reg, '.').substring(2, 10);
    },
    tabChange(e) {
      this.currentTab = e.name;
    },
   handleWeekChange(val) {
      console.log('选中的值:', val);
     this.formValidate.selectedWeek = val
     this.$set(this.formValidate, 'selectedWeek', val);
    },
    //添加优惠券
    addCoupon() {
      const _this = this;
      _this.formValidate.couponIdList = [];
      this.$modalCoupon('wu', (this.keyNum += 1), this.couponList, function (row) {
        _this.couponList = row;
        _this.couponList.forEach((item) => {
          _this.formValidate.couponIdList.push(item.id);
        });
      });
    },
    // 优惠券详情
    getConfig() {
      this.formValidate.couponIdList = [];
      if (this.currentTab === '1') {
        this.listLoading = true;
        newPeoplePresentConfigApi()
          .then((res) => {
            this.$set(this, 'couponList', res.couponList ? res.couponList : []);
            this.formValidate.newPeopleSwitch = res.newPeopleSwitch;
            this.couponList.forEach((item) => {
              this.formValidate.couponIdList.push(item.id);
            });
            this.listLoading = false;
          })
          .catch(() => {
            this.listLoading = false;
          });
      } else  if (this.currentTab === '2') {
        this.listLoading = true;
        birthdayPresentConfigApi()
          .then((res) => {
            this.$set(this, 'couponList', res.couponList ? res.couponList : []);
            this.formValidate.birthdaySwitch = res.birthdaySwitch;
            this.couponList.forEach((item) => {
              this.formValidate.couponIdList.push(item.id);
            });
            this.listLoading = false;
          })
          .catch(() => {
            this.listLoading = false;
          });
      }  else  if (this.currentTab === '3') {
        this.listLoading = true;
        membersDayConfigApi()
          .then((res) => {
            this.$set(this, 'couponList', res.couponList ? res.couponList : []);
            this.formValidate.membersDaySwitch = res.membersDaySwitch;
            this.formValidate.selectedWeek = res.selectedWeek;
            this.formValidate.selectedDay = res.selectedDay;
            this.couponList.forEach((item) => {
              this.formValidate.couponIdList.push(item.id);
            });
            this.listLoading = false;
          })
          .catch(() => {
            this.listLoading = false;
          });
      }
    },
    rowStyle: function ({ row, rowIndex }) {
      Object.defineProperty(row, 'rowIndex', { value: rowIndex, writable: true, enumerable: false });
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 列表
    getList(num) {
      this.tableFrom.page = num ? num : this.tableFrom.page;
      canSendListApi(this.tableFrom)
        .then((res) => {
          this.visibleCoupon = true;
          this.tableData.data = res.list;
          this.tableData.total = res.total;
        })
        .catch((res) => {
          this.$message.error(res.message);
        });
    },
    pageChange(page) {
      this.tableFrom.page = page;
      this.getList('');
    },
    handleSizeChange(val) {
      this.tableFrom.limit = val;
      this.getList('');
    },
    // 保存
    confirmEdit() {
      if (this.couponList.filter((item) => item.isDel).length > 0) {
        this.$confirm('当前活动中存在已删除优惠券，提交后会自动删除。', '提示').then((result) => {
          this.formValidate.couponIdList = [];
          this.couponList.forEach((item) => {
            if (!item.isDel) {
              this.formValidate.couponIdList.push(item.id);
            }
          });
          this.handlerEdit();
        });
      } else {
        this.handlerEdit();
      }
    },
    //保存提交数据
    handlerEdit() {
      if (this.currentTab === '1') {
        newPeoplePresentEditApi(this.formValidate).then((res) => {
          this.$message.success('保存成功');
          this.getConfig();
        });
      }  if (this.currentTab === '2') {
        birthdayPresentEditApi(this.formValidate).then((res) => {
          this.$message.success('保存成功');
          this.getConfig();
        });
      } if (this.currentTab === '3') {
        membersDayEditApi(this.formValidate).then((res) => {
          this.$message.success('保存成功');
          this.getConfig();
        });
      }
    },
    // 删除
    delItem(id, index) {
      let i = this.formValidate.couponIdList.findIndex((item) => item == id);
      this.couponList.splice(index, 1);
      this.formValidate.couponIdList.splice(i, 1);
    },
  },
};
</script>
<style lang="scss" scoped>
::v-deep .form_label {
  border: none !important;
}
.proview {
  width: 500px;
  height: 800px;
  border-left: 1px solid #f5f5f5;
  img {
    width: 375px;
  }
}

.flex-1 {
  flex: 1;
}

.form_label {
  font-size: 16px;
  padding-bottom: 16px;
  font-weight: bolder;
  margin-top: 20px;
  margin-bottom: 16px;
  color: rgb(38, 38, 38);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.suffix_text {
  color: #333;
}
.grid_box {
  display: flex;
  grid-template-rows: auto;
  grid-template-columns: repeat(4, 1fr);
  grid-gap: 20px;
  flex-wrap: wrap;
}
.show {
  background-color: #fde2db;
}
.del {
  background-color: #f8f8f8;
}
.coupon_item {
  width: 310px;
  height: 75px;
  background-image: radial-gradient(circle at 60px top, #fff, #fff 7px, transparent 7px),
    radial-gradient(circle at 60px bottom, #fff, #fff 7px, transparent 7px);
  border-radius: 4px;
  position: relative;
  .close {
    font-size: 20px;
    color: #999;
    position: absolute;
    right: -5px;
    top: -5px;
    cursor: pointer;
  }

  ._left {
    width: 116px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #e93323;
    border-right: 1px dashed #fff;

    ._symbol {
      font-size: 16px;
    }

    ._price {
      font-size: 30px;
      font-weight: bol;
    }

    ._type {
      font-size: 12px;
      line-height: 14px;
    }
  }

  ._right {
    flex: 1;
    padding: 10px 20px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    line-height: 14px;
    color: #666;

    ._sales {
      color: #999;
    }
  }
}
</style>
