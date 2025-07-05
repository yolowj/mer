<template>
  <el-dialog
    v-if="dialogVisible"
    :title="formValidate.id ? '编辑用户等级' : '添加用户等级'"
    :visible.sync="dialogVisible"
    width="900px"
    :close-on-click-modal="false"
    :before-close="handleClose"
  >
    <el-form :model="formValidate" :rules="rules" ref="userRef" label-width="90px" class="demo-ruleForm" v-loading="loading">
      <el-form-item label="等级名称：" prop="name">
        <el-input v-model.trim="formValidate.name" maxlength="10" class="from-ipt-width" placeholder="请输入等级名称"></el-input>
      </el-form-item>
      <el-form-item label="积分倍率：" prop="integralRatio">
        <el-input-number
          v-model.number="formValidate.integralRatio"
          placeholder="请输入积分倍率"
          :min="0"
          :max="99999"
          step-strictly
        ></el-input-number>
      </el-form-item>
      <el-form-item label="首单倍率：" prop="firstOrderRatio">
        <el-input-number
          v-model.number="formValidate.firstOrderRatio"
          placeholder="请输入首单倍率"
          :min="0"
          :max="99999"
          step-strictly
        ></el-input-number>
      </el-form-item>
      <el-form-item label="等级级别：" prop="grade">
        <el-select v-model="formValidate.grade" placeholder="请选择" :disabled="formValidate.grade === 0">
          <el-option
            v-for="item in levelData"
            :key="item.val"
            :label="item.name"
            :value="item.val"
            :disabled="item.disabled"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="所需成长值：" prop="experience">
        <el-input-number
          :disabled="formValidate.grade === 0"
          v-model.number="formValidate.experience"
          placeholder="请输入成长值"
          :min="0"
          :max="99999"
          step-strictly
        ></el-input-number>
      </el-form-item>
      <el-form-item label="赠送优惠券：" prop="proCoupon">
        <div class="acea-row">
          <el-tag
            v-for="(tag, index) in formValidate.coupons"
            :key="index"
            class="mr10 mb10"
            :closable="!isDisabled"
            :disable-transitions="false"
            @close="handleCloseCoupon(tag)"
          >
            {{ tag.name }}
          </el-tag>
          <span class="mr15" v-if="formValidate.couponIds == null">无</span>
          <el-button v-if="!isDisabled" size="small" class="mr15" @click="addCoupon">选择优惠券</el-button>
        </div>
      </el-form-item>
      <el-form-item label="文字颜色：" prop="backColor">
        <div class="acea-row">
          <el-color-picker
              v-model="formValidate.backColor"
              @change="changeColor($event)"
          ></el-color-picker>
          <el-input size="small" v-model.trim="formValidate.backColor" class="ml10 from-ipt-width"></el-input>
        </div>
        <div class="from-tips">请输入文字颜色(例：#455A93)</div>
      </el-form-item>
      <el-form-item label="图标：" prop="icon">
        <div class="upLoadPicBox" @click="modalPicTap(false, 'icon')">
          <div v-if="formValidate.icon" class="pictrue"><img :src="formValidate.icon" /></div>
          <div v-else class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
          <p class="desc mt10">尺寸：180*180px</p>
        </div>
      </el-form-item>
      <el-form-item label="背景图：" prop="backImage">
        <div class="upLoadPicBox" @click="modalPicTap(false, 'backImage')">
          <div v-if="formValidate.backImage" class="pictrue"><img :src="formValidate.backImage" /></div>
          <div v-else class="upLoad">
            <i class="el-icon-camera cameraIconfont" />
          </div>
        </div>
        <p class="desc mt10">尺寸：660*300px</p>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="resetForm('formValidate')">取消</el-button>
      <el-button
        type="primary"
        @click="submitForm('formValidate')"
        v-hasPermi="['platform:system:user:level:save', 'platform:system:user:level:update']"
        >确 定</el-button
      >
    </span>
  </el-dialog>
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
import { levelSaveApi, levelInfoApi, levelUpdateApi } from '@/api/user';
import { Debounce } from '@/utils/validate';

export default {
  name: 'CreatGrade',
  props: {
    userInfo: {
      type: Object,
      default: ()=>{
        return {}
      },
    },
    levelNumData: {
      type: Array,
      default: [],
    },
  },
  watch: {
    userInfo: {
      handler: function (val) {
        debugger
        this.formValidate = val;
      },
      immediate: false,
      deep: true,
    },
    levelNumData: {
      handler: function (val) {
        this.levelNumDataNew = val;
      },
      immediate: false,
      deep: true,
    },
  },
  data() {
    return {
      isDisabled: this.$route.params.isDisabled === '1' ? true : false,
      dialogVisible: false,
      formValidate: this.userInfo,
      loading: false,
      rules: {
        name: [{ required: true, message: '请输入等级名称', trigger: 'blur' }],
        grade: [
          { required: true, message: '请输入等级', trigger: 'blur' },
          { type: 'number', message: '等级必须为数字值' },
        ],
        backColor: [{ required: true, message: '请输入文字颜色', trigger: 'blur' }],
        discount: [{ message: '请输入折扣', trigger: 'blur' }],
        experience: [
          { required: true, message: '请输入经验', trigger: 'blur' },
          { type: 'number', message: '经验必须为数字值' },
        ],
        icon: [{ required: true, message: '请上传图标', trigger: 'change' }],
        backImage: [{ required: true, message: '请上传背景图', trigger: 'change' }],
      },
      keyNum: 0,
      levelNum: [],
      levelNumDataNew: this.levelNumData,
      //levelData: []
    };
  },
  mounted() {
    for (let i = 0; i < 100; i++) {
      this.levelNum.push({
        val: i,
        name: i,
      });
    }
  },
  computed: {
    levelData() {
      this.levelNum.map((i) => {
        if (this.levelNumDataNew.some((item) => item === i.val)) {
          i.disabled = true;
        } else {
          i.disabled = false;
        }
      });
      return this.levelNum;
    },
  },
  methods: {
    //点击颜色
    changeColor(e, color) {
      if (e) {
        this.$refs.userRef.clearValidate('backColor')
      }
    },
    // 点击商品图
    modalPicTap(multiple, num) {
      const _this = this;
      this.$modalUpload(
        function (img) {
          if (!img) return;
          if (num === 'icon') {
            _this.formValidate.icon = img[0].sattDir;
            _this.$refs.userRef.clearValidate('icon');
          } else {
            _this.formValidate.backImage = img[0].sattDir;
            _this.$refs.userRef.clearValidate('backImage');
          }
        },
        multiple,
        'user',
      );
    },
    handleCloseCoupon(tag) {
      this.formValidate.coupons.splice(this.formValidate.coupons.indexOf(tag), 1);
      this.formValidate.couponIds.splice(this.formValidate.couponIds.indexOf(tag.id), 1);
    },
    handleClose() {
      this.dialogVisible = false;
    },
    addCoupon() {
      const _this = this;
      this.$modalCoupon(
        'wu',
        (this.keyNum += 1),
        this.formValidate.coupons,
        function (row) {
          _this.$set(_this.formValidate, 'couponIds', []); // 确保响应式
          _this.$set(_this.formValidate, 'coupons', row); // 确保响应式
          row.map((item) => {
            _this.formValidate.couponIds.push(item.id);
          });
        },
        '',
      );
    },

    submitForm: Debounce(function (formName) {
      this.$refs.userRef.validate((valid) => {
        if (valid) {
          this.loading = true;
          this.formValidate.id
            ? levelUpdateApi(this.formValidate)
                .then((res) => {
                  this.$message.success('编辑成功');
                  this.loading = false;
                  this.handleClose();
                  this.$parent.getList();
                })
                .catch(() => {
                  this.loading = false;
                })
            : levelSaveApi(this.formValidate)
                .then((res) => {
                  this.$message.success('添加成功');
                  this.loading = false;
                  this.handleClose();
                  this.$parent.getList();
                })
                .catch(() => {
                  this.loading = false;
                });
        } else {
          return false;
        }
      });
    }),
    resetForm() {
       this.$nextTick(() => {
        this.$refs.userRef.clearValidate();
      })
      this.dialogVisible = false;
    },
  },
};
</script>

<style scoped>
.desc {
  color: #999;
  font-size: 12px;
  line-height: 16px;
}
.el-input-number.el-input-number--small {
  width: 200px;
}
</style>
