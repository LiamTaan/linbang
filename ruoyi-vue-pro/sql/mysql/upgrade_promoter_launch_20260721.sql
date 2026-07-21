-- 推广员体系上线迁移。执行前必须确认当前库名，并完整备份生产数据库。
-- 保留每个被邀请用户最早创建的推广关系，冲突数据先备份再物理清理。

CREATE TABLE IF NOT EXISTS `lb_promoter_relation_backup_20260721` LIKE `lb_promoter_relation`;
INSERT IGNORE INTO `lb_promoter_relation_backup_20260721` SELECT * FROM `lb_promoter_relation`;

CREATE TABLE IF NOT EXISTS `lb_promoter_backup_20260721` LIKE `lb_promoter`;
INSERT IGNORE INTO `lb_promoter_backup_20260721` SELECT * FROM `lb_promoter`;

CREATE TABLE IF NOT EXISTS `lb_commission_order_backup_20260721` LIKE `lb_commission_order`;
INSERT IGNORE INTO `lb_commission_order_backup_20260721` SELECT * FROM `lb_commission_order`;

CREATE TABLE IF NOT EXISTS `lb_promoter_operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `promoter_id` BIGINT NOT NULL COMMENT '推广员ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '关联用户ID',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务对象类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务对象ID',
  `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型',
  `before_status` VARCHAR(32) DEFAULT NULL COMMENT '变更前状态',
  `after_status` VARCHAR(32) DEFAULT NULL COMMENT '变更后状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '操作说明',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_promoter_operation_log_promoter_time` (`promoter_id`, `create_time`),
  KEY `idx_lb_promoter_operation_log_biz` (`biz_type`, `biz_id`),
  KEY `idx_lb_promoter_operation_log_user_id` (`user_id`),
  KEY `idx_lb_promoter_operation_log_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广员业务操作日志表';

DELETE duplicate_relation
FROM `lb_promoter_relation` duplicate_relation
JOIN `lb_promoter_relation` retained_relation
  ON retained_relation.user_id = duplicate_relation.user_id
 AND retained_relation.id < duplicate_relation.id;

ALTER TABLE `lb_promoter_relation`
  ADD COLUMN `invite_code` VARCHAR(64) DEFAULT NULL COMMENT '绑定时使用的邀请码审计快照' AFTER `convert_status`,
  ADD COLUMN `source_channel` VARCHAR(32) DEFAULT NULL COMMENT '邀请来源渠道 SHARE_CARD/TIMELINE/QRCODE/MANUAL/UNKNOWN' AFTER `invite_code`,
  ADD COLUMN `source_page` VARCHAR(255) DEFAULT NULL COMMENT '捕获邀请码的来源页面' AFTER `source_channel`;

UPDATE `lb_promoter_relation` relation_row
JOIN `lb_promoter` promoter ON promoter.id = relation_row.promoter_id
SET relation_row.invite_code = promoter.invite_code,
    relation_row.source_channel = COALESCE(relation_row.source_channel, 'HISTORY')
WHERE relation_row.invite_code IS NULL;

ALTER TABLE `lb_promoter_relation`
  DROP INDEX `uk_lb_promoter_relation_promoter_user`,
  DROP INDEX `idx_lb_promoter_relation_user_id`,
  ADD UNIQUE KEY `uk_lb_promoter_relation_user_id` (`user_id`),
  ADD KEY `idx_lb_promoter_relation_promoter_bind` (`promoter_id`, `bind_time`),
  ADD KEY `idx_lb_promoter_relation_promoter_convert` (`promoter_id`, `convert_status`);

DELETE duplicate_commission
FROM `lb_commission_order` duplicate_commission
JOIN `lb_commission_order` retained_commission
  ON retained_commission.promoter_id = duplicate_commission.promoter_id
 AND retained_commission.source_order_id = duplicate_commission.source_order_id
 AND retained_commission.source_unit_id = duplicate_commission.source_unit_id
 AND retained_commission.id < duplicate_commission.id
WHERE duplicate_commission.source_order_id IS NOT NULL
  AND duplicate_commission.source_unit_id IS NOT NULL;

ALTER TABLE `lb_commission_order`
  ADD UNIQUE KEY `uk_lb_commission_order_source_unit` (`promoter_id`, `source_order_id`, `source_unit_id`);

UPDATE `lb_promoter` promoter
LEFT JOIN (
  SELECT promoter_id,
         COUNT(*) AS bind_count,
         SUM(CASE WHEN convert_status = 'CONVERTED' THEN 1 ELSE 0 END) AS convert_count
  FROM `lb_promoter_relation`
  GROUP BY promoter_id
) relation_stat ON relation_stat.promoter_id = promoter.id
SET promoter.bind_user_count = COALESCE(relation_stat.bind_count, 0),
    promoter.convert_count = COALESCE(relation_stat.convert_count, 0),
    promoter.level_code = CASE
      WHEN COALESCE(relation_stat.bind_count, 0) >= 50 THEN 'L3'
      WHEN COALESCE(relation_stat.bind_count, 0) >= 10 THEN 'L2'
      ELSE 'L1'
    END;

SELECT user_id, COUNT(*) AS relation_count
FROM `lb_promoter_relation`
GROUP BY user_id
HAVING COUNT(*) > 1;

SELECT level_code, COUNT(*) AS promoter_count
FROM `lb_promoter`
GROUP BY level_code;
