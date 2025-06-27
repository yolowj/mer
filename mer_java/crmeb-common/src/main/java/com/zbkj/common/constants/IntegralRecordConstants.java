package com.zbkj.common.constants;

/**
 * 积分记录常量类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public class IntegralRecordConstants {

    /** 积分记录类型—增加 */
    public static final Integer INTEGRAL_RECORD_TYPE_ADD = 1;

    /** 积分记录类型—扣减 */
    public static final Integer INTEGRAL_RECORD_TYPE_SUB = 2;

    /** 积分记录状态—创建 */
    public static final Integer INTEGRAL_RECORD_STATUS_CREATE = 1;

    /** 积分记录状态—冻结期 */
    public static final Integer INTEGRAL_RECORD_STATUS_FROZEN = 2;

    /** 积分记录状态—完成 */
    public static final Integer INTEGRAL_RECORD_STATUS_COMPLETE = 3;

    /** 积分记录状态—失效（订单退款） */
    public static final Integer INTEGRAL_RECORD_STATUS_INVALIDATION = 4;

    /** 积分记录关联类型—订单 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_ORDER = "order";

    /** 积分记录关联类型—订单退款 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_ORDER_REFUND = "refund";

    /** 积分记录关联类型—签到 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_SIGN = "sign";

    /** 积分记录关联类型—系统后台 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_SYSTEM = "system";

    /** 积分记录标题—用户订单付款成功 */
    public static final String INTEGRAL_RECORD_TITLE_ORDER = "用户订单付款成功";

    /** 积分记录标题—签到积分奖励 */
    public static final String INTEGRAL_RECORD_TITLE_SIGN = "签到积分奖励";

    /** 积分记录标题—后台积分操作 */
    public static final String INTEGRAL_RECORD_TITLE_SYSTEM = "后台积分操作";

    /** 积分记录标题—订单退款 */
    public static final String INTEGRAL_RECORD_TITLE_REFUND = "订单退款";
}
