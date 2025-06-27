ALTER TABLE `java_mer_trip`.`eb_system_user_level`
    ADD COLUMN `integral_ratio` decimal(10, 2) NULL DEFAULT NULL COMMENT '积分比例' AFTER `name`;


ALTER TABLE `java_mer_trip`.`eb_coupon`
    MODIFY COLUMN `coupon_type` tinyint(2) NOT NULL DEFAULT 1 COMMENT '优惠券类型 1-满减券,2-折扣券,3-包邮券' AFTER `receive_type`;


ALTER TABLE `java_mer_trip`.`eb_product`
    ADD COLUMN `gift_points` int NOT NULL DEFAULT 0 COMMENT '赠送积分' AFTER `system_form_id`;
