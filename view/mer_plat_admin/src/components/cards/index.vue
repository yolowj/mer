<template>
  <el-row align="middle" :gutter="20" class="ivu-mt">
    <el-col
      :xl="6"
      :lg="6"
      :md="12"
      :sm="12"
      :xs="24"
      class="ivu-mb mb20"
      v-for="(item, index) in cardLists"
      :key="index"
    >
      <div class="card_box">
        <div class="card_box_cir" :class="item.class">
          <span class="iconfont" :class="item.icon" :style="{ color: item.color }" v-if="item.icon"></span>
          <i class="el-icon-edit" style="color: #fff" v-else></i>
        </div>
        <div class="card_box_txt">
          <span class="sp2" v-text="item.name"></span>
          <span class="sp1" v-text="item.count || 0"></span>
        </div>
      </div>
    </el-col>
  </el-row>
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
export default {
  name: 'index',
  props: {
    cardLists: Array,
  },
};
</script>

<style scoped lang="scss">
.one {
  background: rgba(24, 144, 255, 0.1);
}
.two {
  background: rgba(162, 119, 255, 0.1);
}
.three {
  background: rgba(232, 182, 0, 0.1);
}
.four {
  background: rgba(27, 190, 107, 0.1);
}
.five {
  background: rgba(75, 202, 213, 0.1);
}
.six {
  background: rgba(239, 156, 32, 0.1);
}
.one1 {
  background: #1890ff;
}
.two1 {
  background: #a277ff;
}
.three1 {
  background: #ef9c20;
}
.four1 {
  background: #1bbe6b;
}
.five1 {
  background: #4bcad5;
}
.six1 {
  background: #ef9c20;
}
.card_box {
  width: 100%;
  height: 110px;
  display: flex;
  align-items: center;
  /*justify-content: center*/
  padding: 25px;
  box-sizing: border-box;
  border-radius: 4px;
  background: #fff;
  box-sizing: border-box;
  .card_box_cir {
    width: 50px;
    height: 50px;
    border-radius: 4px;
    overflow: hidden;
    margin-right: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .card_box_txt {
    .sp1 {
      display: block;
      color: #333333;
      font-size: 28px;
      padding-top: 3px;
      font-family: PingFangSC-Semibold, PingFang SC;
      font-weight: 600;
    }
    .sp2 {
      display: block;
      color: #606266;
      font-size: 14px;
      font-weight: 400;
    }
  }
}
.iconfont {
  font-size: 23px;
}
</style>
