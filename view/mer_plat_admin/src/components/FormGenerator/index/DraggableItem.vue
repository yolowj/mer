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
import draggable from 'vuedraggable';
import render from '@/components/FormGenerator/components/render/render';

const components = {
  itemBtns(h, element, index, parent) {
    const { copyItem, deleteItem } = this.$listeners;
    return [
      <span
        class="drawing-item-copy"
        title="复制"
        onClick={(event) => {
          copyItem(element, parent);
          event.stopPropagation();
        }}
      >
        <i class="el-icon-copy-document" />
      </span>,
      <span
        class="drawing-item-delete"
        title="删除"
        onClick={(event) => {
          deleteItem(index, parent);
          event.stopPropagation();
        }}
      >
        <i class="el-icon-delete" />
      </span>,
    ];
  },
};
const layouts = {
  colFormItem(h, element, index, parent) {
    const { activeItem } = this.$listeners;
    const config = element.__config__;
    let className = this.activeId === config.formId ? 'drawing-item active-from-item' : 'drawing-item';
    if (this.formConf.unFocusedComponentBorder) className += ' unfocus-bordered';
    let labelWidth = config.labelWidth ? `${config.labelWidth}px` : null;
    if (config.showLabel === false) labelWidth = '0';
    if (config.tips == undefined) {
      this.$set(config, 'tips', false); //如果以前的表单没有tooltip配置，就赋值一个默认值用来读取
    }
    if (config.tips) {
      return (
        <el-col
          span={config.span}
          class={className}
          nativeOnClick={(event) => {
            activeItem(element);
            event.stopPropagation();
          }}
        >
          <el-form-item
            label-width={labelWidth}
            label={config.showLabel ? config.label : ''}
            required={config.required}
          >
            <render
              key={config.renderKey}
              conf={element}
              onInput={(event) => {
                this.$set(config, 'defaultValue', event);
              }}
            />
            <div>
              <span className="tips-info">{config.tipsDesc}</span>
            </div>
          </el-form-item>
          {components.itemBtns.apply(this, arguments)}
        </el-col>
      );
    } else {
      return (
        <el-col
          span={config.span}
          class={className}
          nativeOnClick={(event) => {
            activeItem(element);
            event.stopPropagation();
          }}
        >
          <el-form-item
            label-width={labelWidth}
            label={config.showLabel ? config.label : ''}
            required={config.required}
          >
            <render
              key={config.renderKey}
              conf={element}
              onInput={(event) => {
                this.$set(config, 'defaultValue', event);
              }}
            />
          </el-form-item>
          {components.itemBtns.apply(this, arguments)}
        </el-col>
      );
    }
  },
  rowFormItem(h, element, index, parent) {
    const { activeItem } = this.$listeners;
    const className =
      this.activeId === element.__config__.formId ? 'drawing-row-item active-from-item' : 'drawing-row-item';
    let child = renderChildren.apply(this, arguments);
    if (element.type === 'flex') {
      child = (
        <el-row type={element.type} justify={element.justify} align={element.align}>
          {child}
        </el-row>
      );
    }
    return (
      <el-col span={element.__config__.span}>
        <el-row
          gutter={element.__config__.gutter}
          class={className}
          nativeOnClick={(event) => {
            activeItem(element);
            event.stopPropagation();
          }}
        >
          <span class="component-name">{element.__config__.componentName}</span>
          <draggable list={element.__config__.children} animation={340} group="componentsGroup" class="drag-wrapper">
            {child}
          </draggable>
          {components.itemBtns.apply(this, arguments)}
        </el-row>
      </el-col>
    );
  },
};

function renderChildren(h, element, index, parent) {
  const config = element.__config__;
  if (!Array.isArray(config.children)) return null;
  return config.children.map((el, i) => {
    const layout = layouts[el.__config__.layout];
    if (layout) {
      return layout.call(this, h, el, i, config.children);
    }
    return layoutIsNotFound.call(this);
  });
}

function layoutIsNotFound() {
  throw new Error(`没有与${this.element.__config__.layout}匹配的layout`);
}

export default {
  components: {
    render,
    draggable,
  },
  props: ['element', 'index', 'drawingList', 'activeId', 'formConf'],
  render(h) {
    const layout = layouts[this.element.__config__.layout];

    if (layout) {
      return layout.call(this, h, this.element, this.index, this.drawingList);
    }
    return layoutIsNotFound.call(this);
  },
};
</script>
