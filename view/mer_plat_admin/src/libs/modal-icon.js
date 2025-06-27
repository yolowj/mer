// +----------------------------------------------------------------------
// | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
// +----------------------------------------------------------------------
// | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
// +----------------------------------------------------------------------
// | Author: CRMEB Team <admin@crmeb.com>
// +----------------------------------------------------------------------

/**
 * 菜单图标弹窗形式展示组件
 * @param callback 回调函数
 * @returns {Promise<any>}
 */
export default function modalIcon(callback) {
  const h = this.$createElement;
  return new Promise((resolve, reject) => {
    this.$msgbox({
      title: '菜单图标',
      customClass: 'upload-form',
      closeOnClickModal: false,
      showClose: true,
      message: h('div', { class: 'common-form-upload' }, [
        h('iconFrom', {
          on: {
            getIcon(n) {
              callback(n);
            },
          },
        }),
      ]),
      showCancelButton: false,
      showConfirmButton: false,
    })
      .then(() => {
        resolve();
      })
      .catch(() => {
        reject();
        this.$message({
          type: 'info',
          message: '已取消',
        });
      });
  });
}
