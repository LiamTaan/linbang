ALTER TABLE `lb_order_unit`
    ADD COLUMN `verify_code` varchar(32) NULL DEFAULT NULL COMMENT '核销码' AFTER `appeal_expire_time`,
    ADD COLUMN `verify_status` varchar(32) NULL DEFAULT NULL COMMENT '核销状态' AFTER `verify_code`,
    ADD COLUMN `verify_time` datetime NULL DEFAULT NULL COMMENT '核销时间' AFTER `verify_status`,
    ADD COLUMN `verify_by` bigint NULL DEFAULT NULL COMMENT '核销人用户 ID' AFTER `verify_time`,
    ADD COLUMN `verify_remark` varchar(255) NULL DEFAULT NULL COMMENT '核销备注' AFTER `verify_by`;

SELECT COLUMN_NAME
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'linbang'
  AND TABLE_NAME = 'lb_order_unit'
  AND COLUMN_NAME IN ('verify_code', 'verify_status', 'verify_time', 'verify_by', 'verify_remark')
ORDER BY FIELD(COLUMN_NAME, 'verify_code', 'verify_status', 'verify_time', 'verify_by', 'verify_remark');
