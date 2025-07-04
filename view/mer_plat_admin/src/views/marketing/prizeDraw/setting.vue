<template>
  <div class="divBox">
    <el-card class="box-card mt14" :body-style="{ padding: '20px' }" :bordered="false" shadow="never">
      <el-button size="mini" type="primary" @click="add(0,{})" v-hasPermi="['platform:seckill:time:interval:add']"
      >添加商品设置</el-button
      >
      <el-table v-loading="listLoading" :data="tableData.data" size="small" ref="multipleTable" class="mt20">
        <el-table-column prop="id" label="ID" min-width="50" />
        <el-table-column prop="type" label="类型" min-width="150" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type == 1">商品</el-tag>
            <el-tag v-if="scope.row.type == 2">优惠券</el-tag>
            <el-tag v-if="scope.row.type == 3">积分</el-tag>
            <el-tag v-if="scope.row.type == 4">谢谢惠顾</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="奖品" min-width="100" prop="value">
          <template slot-scope="scope">
            <span v-if="scope.row.type == 1">{{scope.row.productName}}</span>
            <span v-if="scope.row.type == 2">{{scope.row.couponName}}</span>
            <span v-if="scope.row.type == 3">{{scope.row.value}}</span>
            <span v-if="scope.row.type == 4">谢谢惠顾</span>
          </template>
        </el-table-column>
        <el-table-column label="概率" min-width="100" prop="probability">
          <template slot-scope="scope">
            {{scope.row.probability}}%
          </template>
        </el-table-column>
<!--        <el-table-column label="状态" min-width="150">-->
<!--          <template slot-scope="scope">-->
<!--            <el-switch-->
<!--              v-if="checkPermi(['platform:seckill:time:interval:switch'])"-->
<!--              v-model="scope.row.status"-->
<!--              :active-value="1"-->
<!--              :inactive-value="0"-->
<!--              active-text="开启"-->
<!--              inactive-text="关闭"-->
<!--              @change="onchangeIsShow(scope.row)"-->
<!--            />-->
<!--            <div v-else>{{ scope.row.status ? '开启' : '关闭' }}</div>-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column prop="num" label="剩余数量" min-width="130" />
        <el-table-column prop="status" label="类型" min-width="150" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status == 0">已生效</el-tag>
            <el-tag v-if="scope.row.status == 1">已禁用</el-tag>
          </template>
        </el-table-column>





        <el-table-column prop="createTime" label="创建时间" min-width="130" />
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
            <a @click="add(1, scope.row)" v-hasPermi="['platform:seckill:time:interval:update']">编辑</a>
            <el-divider direction="vertical"></el-divider>
            <a @click="handleDelete(scope.row.id, scope.$index)" v-hasPermi="['platform:seckill:time:interval:delete']"
            >删除</a
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog title="奖品" :visible.sync="dialogVisible" width="40%">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="奖品类型：">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">商品</el-radio>
            <el-radio :label="2">优惠券</el-radio>
            <el-radio :label="3">积分</el-radio>
            <el-radio :label="4">谢谢惠顾</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="奖品：" v-if="form.type != 4">
          <div v-if="form.type == 2">
            <el-tag v-if="coupons.length > 0">{{ coupons[0].name }}</el-tag>
            <div>
              <el-button size="small" class="mr15" @click="addCoupon">选择优惠券</el-button>
            </div>
          </div>
          <div v-if="form.type == 1">
            <el-tag v-if="goods">{{ goods.name }}</el-tag>
            <div>
              <el-button size="small" type="primary" @click="addGoods">选择商品</el-button>
            </div>

          </div>
          <el-input v-model="form.value" v-if="form.type == 3" placeholder="请输入积分" />
        </el-form-item>
        <el-form-item label="奖品数量：">
          <el-input-number v-model="form.num" style="width: 80%" :min="0" :max="100" controls-position="right" placeholder="请输入奖品数量" />
        </el-form-item>
        <el-form-item label="中奖概率：">
          <el-input-number v-model="form.probability" style="width: 80%" :min="0" :max="100" controls-position="right" placeholder="请输入中奖概率" />
        </el-form-item>
        <el-form-item label="领取条件：">
          <el-select v-model="form.con"  placeholder="请选择领取条件" style="width: 80%" :min="0" :max="100" >
            <el-option label="直领" value="0" />
            <el-option label="积分/分" value="1" />
            <el-option label="支付/元" value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="领取所需：">
          <el-input-number v-model="form.mon" style="width: 80%" :min="0" :max="100" controls-position="right" placeholder="领取所需" />
        </el-form-item>

        <el-form-item label="状态：">
          <el-select v-model="form.status"  placeholder="状态" style="width: 80%" :min="0" :max="100" >
            <el-option label="禁用" value="0" />
            <el-option label="启用" value="1" />
          </el-select>
        </el-form-item>

      </el-form>
      <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="handlerSubmit">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getPrizeApi, savePrizeApi, delPrizeApi
} from '@/api/marketing';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
import { Debounce } from '@/utils/validate';
export default {
  name: 'SeckillConfig',
  components: { },
  data() {
    return {
      dialogVisible: false,
      isShow: true,
      isCreate: 0,
      editData: {},
      form: {
        type: 1
      },
      formId: 123,
      listLoading: false,
      tableData: {
        data: [],
        total: 0,
      },
      tableFrom: {
        status: '',
      },
      goods: null,
      seckillId: null,
      loading: false,
      keyNum: 0,
      coupons: []
    };
  },
  mounted() {
    if (checkPermi(['platform:seckill:time:interval:list'])) this.getList();
  },
  methods: {
    checkPermi,
    resetForm(formValue) {
      this.dialogVisible = false;
    },
    addCoupon() {
      const _this = this;
      this.$modalCoupon(
        'wu',
        (this.keyNum += 1),
        this.coupons,
        function (row) {
          _this.coupons = row;
        },
        '',
      );
    },
    addGoods() {
      const _this = this;
      this.$modalGoodList(
        function (row) {
          _this.goods = row
          console.log(row)
        },
        'one',
        _this.tableData.data,
      );
    },
    // 删除
    handleDelete(id, idx) {
      this.$modalSure().then(() => {
        delPrizeApi(id).then(() => {
          this.$message.success('删除成功');
          this.tableData.data.splice(idx, 1);
        });
      });
    },
    onchangeIsShow(row) {
      seckillIntervalSwitcheApi(row.id)
        .then(async () => {
          this.$message.success('修改成功');
          this.getList();
        })
        .catch(() => {
          row.status = !row.status;
        });
    },
    // 提交
    handlerSubmit: Debounce(function (formValue) {
      if(this.form.type == 1) {
        this.form.value = this.goods.id
      }
      if(this.form.type == 2) {
        this.form.value = this.coupons[0].id
      }
      savePrizeApi(this.form).then(res => {
        this.dialogVisible = false
        this.getList();
      })
    }),
    isSuccess() {
      this.$message.success('操作成功');
      this.dialogVisible = false;
      this.getList();
    },
    // 列表
    getList() {
      this.listLoading = true;
      getPrizeApi(this.tableFrom)
        .then((res) => {
          console.log(res)
          this.tableData.data = res.list;
          this.listLoading = false;
        })
        .catch((res) => {
          this.listLoading = false;
        });
    },

    add(type, row) {
      if(type == 1) {
        this.form = row
        if(row.type == 1) {
          this.goods = {
            name: row.productName,
            id: row.value
          }
        }
        if(row.type == 2) {
          this.coupons = [{
            name: row.couponName,
            id: row.value
          }]
        }
      }
      this.dialogVisible = true;
      // const _this = this;
      // this.seckillId = editDate ? editDate.id : null;
      // this.$modalParserFrom(
      //   isCreate === 0 ? '添加奖品' : '编辑奖品',
      //   '积分抽奖',
      //   isCreate,
      //   isCreate === 0
      //     ? {
      //       id: 0,
      //       name: '',
      //       timeData: [],
      //       status: 0,
      //     }
      //     : Object.assign(
      //       {},
      //       {
      //         id: editDate.id,
      //         name: editDate.name,
      //         timeData: [editDate.startTime, editDate.endTime],
      //         status: editDate.status,
      //       },
      //     ),
      //   function (formValue) {
      //     _this.submit(formValue);
      //   },
      //   (this.keyNum = Math.random()),
      // );
    },
  },
};
</script>

<style scoped>
::v-deep .el-range-separator {
  width: 12%;
}

::v-deep .el-col-15:nth-of-type(1) {
  width: 100% !important;
}
</style>
