<template>
  <div class="divBox relative">
    <el-card class="box-card" shadow="never" :bordered="false">
      <el-tabs v-model="tableFrom.type" @tab-click="onChangeType" class="mb20">
        <el-tab-pane label="短信" name="sms"></el-tab-pane>
        <el-tab-pane label="商品采集" name="copy"></el-tab-pane>
        <el-tab-pane label="物流查询" name="expr_query"></el-tab-pane>
      </el-tabs>
      <router-link :to="{ path: '/operation/onePass/index' }">
        <el-button class="link_abs" size="mini" icon="el-icon-arrow-left">返回</el-button>
      </router-link>
      <el-row v-loading="fullscreenLoading" :gutter="16">
        <el-col :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">短信账户名称：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span>{{ account }}</span>
          </el-col>
        </el-col>
        <el-col :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">当前剩余条数：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span>{{ numbers }}</span>
          </el-col>
        </el-col>
        <el-col :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">选择套餐：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <el-row :gutter="20">
              <el-col v-for="(item, index) in list" :key="index" :xl="6" :lg="6" :md="12" :sm="24" :xs="24">
                <div
                  class="list-goods-list-item mb15"
                  :class="{ active: index === current }"
                  @click="check(item, index)"
                >
                  <div class="list-goods-list-item-title" :class="{ active: index === current }">
                    ¥ <i>{{ item.price }}</i>
                  </div>
                  <div class="list-goods-list-item-price" :class="{ active: index === current }">
                    <span>{{ tableFrom.type | onePassTypeFilter }}条数: {{ item.num }}</span>
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-col>
        </el-col>
        <el-col v-if="checkList" :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">充值条数：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span>{{ checkList.num }}</span>
          </el-col>
        </el-col>
        <el-col v-if="checkList" :span="24" class="ivu-text-left mb20">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">支付金额：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span class="list-goods-list-item-number">￥{{ checkList.price }}</span>
          </el-col>
        </el-col>
        <el-col :span="24" class="ivu-text-left mb20" v-if="code">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">
            <span class="ivu-text-right ivu-block">付款方式：</span>
          </el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <span class="list-goods-list-item-pay"
              >微信支付<i v-if="code.invalid">{{ '  （ 支付码过期时间：' + code.invalid + ' ）' }}</i></span
            >
          </el-col>
        </el-col>
        <el-col :span="24">
          <el-col :xs="12" :sm="6" :md="4" :lg="2" class="mr20">&nbsp;</el-col>
          <el-col :xs="11" :sm="13" :md="19" :lg="20">
            <div class="list-goods-list-item-code mr20">
              <!--              <img :src="code.code_url">-->
              <div id="payQrcode"></div>
            </div>
          </el-col>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
// +---------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +---------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +---------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +---------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +---------------------------------------------------------------------
import { smsNumberApi, smsPriceApi, payCodeApi, smsInfoApi } from '@/api/sms';
import { isLogin } from '@/libs/public';
import { mapGetters } from 'vuex';
import QRcode from 'qrcodejs2';
import { checkPermi } from '@/utils/permission'; // 权限判断函数
export default {
  name: 'SmsPay',
  data() {
    return {
      numbers: '',
      account: '',
      list: [],
      current: 0,
      checkList: {},
      fullscreenLoading: false,
      code: {},
      tableFrom: {
        type: 'sms',
      },
    };
  },
  computed: {
    ...mapGetters(['isLogin']),
  },
  created() {
    this.tableFrom.type = this.$route.query.type;
    if (checkPermi(['platform:one:pass:is:login'])) this.onIsLogin();
  },
  mounted() {
    if (!this.isLogin) {
      // this.$router.push('/operation/onePass?url=' + this.$route.path)
    } else {
      if (checkPermi(['platform:one:pass:info'])) this.getNumber();
      if (checkPermi(['platform:one:pass:meal:list'])) this.getPrice();
    }
  },
  methods: {
    checkPermi,
    onChangeType(val) {
      this.current = 0;
      this.getPrice();
      this.getNumber();
    },
    // 查看是否登录
    onIsLogin() {
      this.fullscreenLoading = true;
      this.$store
        .dispatch('user/isLogin')
        .then(async (res) => {
          const data = res;
          if (!data.isLogin) {
            this.$message.warning('请先登录');
            this.$router.push('/operation/onePass/index?url=' + this.$route.path);
          } else {
            this.getNumber();
            this.getPrice();
          }
          this.fullscreenLoading = false;
        })
        .catch((res) => {
          this.$router.push('/operation/onePass/index?url=' + this.$route.path);
          this.fullscreenLoading = false;
        });
    },
    // 剩余条数
    getNumber() {
      smsInfoApi().then(async (res) => {
        let data = res;
        this.account = data.account;
        switch (this.tableFrom.type) {
          case 'sms':
            this.numbers = data.sms.num;
            break;
          case 'copy':
            this.numbers = data.copy.num;
            break;
          case 'expr_dump':
            this.numbers = data.dump.num;
            break;
          default:
            this.numbers = data.query.num;
            break;
        }
      });
    },
    // 支付套餐
    getPrice() {
      this.fullscreenLoading = true;
      smsPriceApi(this.tableFrom)
        .then(async (res) => {
          setTimeout(() => {
            this.fullscreenLoading = false;
          }, 800);
          const data = res;
          this.list = data.data;
          this.checkList = this.list[0];
          this.getCode(this.checkList);
        })
        .catch(() => {
          this.fullscreenLoading = false;
        });
    },
    // 选中
    check(item, index) {
      this.fullscreenLoading = true;
      this.current = index;
      setTimeout(() => {
        this.getCode(item);
        this.checkList = item;
        this.fullscreenLoading = false;
      }, 800);
    },
    // 支付码
    getCode(item) {
      const data = {
        payType: 'weixin',
        mealId: item.id,
        price: item.price,
        num: item.num,
        type: this.tableFrom.type,
      };
      payCodeApi(data)
        .then(async (res) => {
          this.code = res;
          document.getElementById('payQrcode').innerHTML = '';
          new QRcode('payQrcode', { width: 135, height: 135, text: res.qr_code });
        })
        .catch((err) => {
          this.$router.push({ path: '/operation/onePass/index', query: { type: this.tableFrom.type } });
          this.code = {};
          document.getElementById('payQrcode').innerHTML = '';
        });
    },
  },
};
</script>

<style lang="scss" scoped>
.active {
  background: #0091ff;
  box-shadow: 0px 6px 20px 0px rgba(0, 145, 255, 0.3);
  color: #fff !important;
}
.list-goods-list-item {
  border: 1px solid #dadfe6;
  height: 118px;
  box-sizing: border-box;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}
.list-goods-list {
  &-item {
    text-align: center;
    position: relative;
    cursor: pointer;
    img {
      width: 60%;
    }
    .ivu-tag {
      position: absolute;
      top: 10px;
      right: 10px;
    }
    &-title {
      font-size: 16px;
      font-weight: bold;
      color: #0091ff;
      margin-bottom: 15px;
      i {
        font-size: 30px;
        font-style: normal;
      }
    }
    &-desc {
      font-size: 14px;
      color: #303133;
    }
    &-price {
      font-size: 14px;
      color: #000000;
      s {
        color: #c5c8ce;
      }
    }
    &-number {
      font-size: 14px;
      color: #ed4014;
    }
    &-pay {
      font-size: 14px;
      color: #00c050;
      i {
        font-size: 12px;
        font-style: normal;
        color: #6d7278;
      }
    }
    &-code {
      width: 130px;
      height: 130px;
      img {
        width: 100%;
        height: 100%;
      }
    }
  }
}
.relative {
  position: relative;
}
.link_abs {
  position: absolute;
  top: 36px;
  right: 40px;
}
</style>
