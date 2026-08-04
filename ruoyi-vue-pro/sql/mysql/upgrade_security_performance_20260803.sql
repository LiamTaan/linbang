SET NAMES utf8mb4;

DROP PROCEDURE IF EXISTS `upgrade_lb_security_performance_20260803`;

CREATE TABLE IF NOT EXISTS `lb_partner_coordination` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `partner_id` BIGINT NOT NULL COMMENT '区域合作商ID',
  `dispute_type` VARCHAR(32) NOT NULL COMMENT '纠纷类型：COMPLAINT 投诉、APPEAL 申诉',
  `dispute_id` BIGINT NOT NULL COMMENT '投诉或申诉主键ID',
  `order_id` BIGINT NOT NULL COMMENT '主订单ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '订单单元ID',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PROCESSING' COMMENT '协调状态：PROCESSING 协调中、ESCALATED 已升级平台终审、FINISHED 已结束',
  `coordination_remark` VARCHAR(1000) NOT NULL COMMENT '协调意见',
  `escalate_remark` VARCHAR(1000) DEFAULT NULL COMMENT '升级平台终审备注',
  `initiated_by` BIGINT NOT NULL COMMENT '发起人用户ID',
  `initiated_time` DATETIME NOT NULL COMMENT '发起时间',
  `finished_by` BIGINT DEFAULT NULL COMMENT '结束或升级操作人用户ID',
  `finished_time` DATETIME DEFAULT NULL COMMENT '结束或升级时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_partner_coord_dispute` (`tenant_id`, `dispute_type`, `dispute_id`, `id`),
  KEY `idx_lb_partner_coord_partner` (`tenant_id`, `partner_id`, `id`),
  KEY `idx_lb_partner_coord_order_unit` (`tenant_id`, `order_id`, `unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区域合作商纠纷协调记录表';

CREATE TABLE IF NOT EXISTS `lb_promoter_operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `promoter_id` BIGINT NOT NULL COMMENT '推广员ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '关联用户ID',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务对象类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务对象ID',
  `operation_type` VARCHAR(32) NOT NULL COMMENT '操作类型 BIND/CONVERT/COMMISSION_CREATE/COMMISSION_REFUND/STATUS_CHANGE',
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

INSERT INTO `system_menu`
(`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 110831, '手动发送通知', 'linbang:message:push-task:manual-send', 3, 1, 110830, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 110831);

INSERT INTO `system_menu`
(`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 110832, '重试推送任务', 'linbang:message:push-task:retry', 3, 2, 110830, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `id` = 110832);

INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT role_menu.role_id, role_menu.menu_id, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM (
  SELECT 1 AS role_id, 110831 AS menu_id
  UNION ALL SELECT 1, 110832
  UNION ALL SELECT 20001, 110831
  UNION ALL SELECT 20001, 110832
) role_menu
WHERE NOT EXISTS (
  SELECT 1 FROM `system_role_menu` existing
  WHERE existing.`role_id` = role_menu.role_id AND existing.`menu_id` = role_menu.menu_id
    AND existing.`tenant_id` = 1 AND existing.`deleted` = b'0'
);

UPDATE `infra_config`
SET `value` = '$2a$12$9E9OkzsYtWcPxi7lNYfLvuRYK4I.Wacs67Oh2fLeB4KK3IgAWylo6',
    `updater` = 'system',
    `update_time` = NOW()
WHERE `config_key` = 'linbang.admin.dynamic-key.password'
  AND `deleted` = b'0'
  AND SHA2(`value`, 256) = '35af8949dab1faa418a798c8979607877d7ca1ab35575c81fb4b812fbe0d0d69';

DELIMITER $$
CREATE PROCEDURE `upgrade_lb_security_performance_20260803`()
BEGIN
  UPDATE `lb_user`
  SET `username` = NULL
  WHERE `username` = '';

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_user'
      AND index_name = 'uk_lb_user_username'
  ) THEN
    IF EXISTS (
      SELECT 1
      FROM `lb_user`
      WHERE `username` IS NOT NULL
      GROUP BY `username`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_user contains duplicate username values';
    END IF;
    ALTER TABLE `lb_user`
      ADD UNIQUE KEY `uk_lb_user_username` (`username`);
  END IF;

  UPDATE `lb_blacklist`
  SET `status` = 'ENABLE'
  WHERE `status` = 'ACTIVE';

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_punish_log'
      AND index_name = 'uk_lb_punish_log_source_record'
  ) THEN
    IF EXISTS (
      SELECT 1
      FROM `lb_punish_log`
      WHERE `source_record_type` IS NOT NULL AND `source_record_id` IS NOT NULL
      GROUP BY `tenant_id`, `source_record_type`, `source_record_id`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_punish_log contains duplicate source records';
    END IF;
    ALTER TABLE `lb_punish_log`
      ADD UNIQUE KEY `uk_lb_punish_log_source_record`
        (`tenant_id`, `source_record_type`, `source_record_id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_promoter_relation'
      AND column_name = 'invite_code'
  ) THEN
    ALTER TABLE `lb_promoter_relation`
      ADD COLUMN `invite_code` VARCHAR(64) DEFAULT NULL COMMENT '绑定时使用的邀请码审计快照' AFTER `convert_status`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_promoter_relation'
      AND column_name = 'source_channel'
  ) THEN
    ALTER TABLE `lb_promoter_relation`
      ADD COLUMN `source_channel` VARCHAR(32) DEFAULT NULL COMMENT '邀请来源渠道 SHARE_CARD/TIMELINE/QRCODE/MANUAL/UNKNOWN' AFTER `invite_code`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_promoter_relation'
      AND column_name = 'source_page'
  ) THEN
    ALTER TABLE `lb_promoter_relation`
      ADD COLUMN `source_page` VARCHAR(255) DEFAULT NULL COMMENT '捕获邀请码的来源页面' AFTER `source_channel`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_promoter_relation'
      AND index_name = 'uk_lb_promoter_relation_user_id'
  ) THEN
    IF EXISTS (
      SELECT 1
      FROM `lb_promoter_relation`
      GROUP BY `user_id`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_promoter_relation contains duplicate user_id values';
    END IF;
    ALTER TABLE `lb_promoter_relation`
      ADD UNIQUE KEY `uk_lb_promoter_relation_user_id` (`user_id`);
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_promoter_relation'
      AND index_name = 'idx_lb_promoter_relation_user_id'
  ) THEN
    ALTER TABLE `lb_promoter_relation`
      DROP INDEX `idx_lb_promoter_relation_user_id`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_commission_order'
      AND index_name = 'uk_lb_commission_order_source_unit'
  ) THEN
    IF EXISTS (
      SELECT 1
      FROM `lb_commission_order`
      WHERE `source_order_id` IS NOT NULL AND `source_unit_id` IS NOT NULL
      GROUP BY `promoter_id`, `source_order_id`, `source_unit_id`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_commission_order contains duplicate source units';
    END IF;
    ALTER TABLE `lb_commission_order`
      ADD UNIQUE KEY `uk_lb_commission_order_source_unit` (`promoter_id`, `source_order_id`, `source_unit_id`);
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'infra_api_access_log'
      AND column_name = 'response_body' AND is_nullable = 'NO'
  ) THEN
    ALTER TABLE `infra_api_access_log`
      MODIFY COLUMN `response_body` LONGTEXT NULL COMMENT '响应结果';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_partner_coordination'
      AND index_name = 'idx_lb_partner_coord_dispute'
  ) THEN
    ALTER TABLE `lb_partner_coordination`
      ADD KEY `idx_lb_partner_coord_dispute` (`tenant_id`, `dispute_type`, `dispute_id`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_partner_coordination'
      AND index_name = 'idx_lb_partner_coord_partner'
  ) THEN
    ALTER TABLE `lb_partner_coordination`
      ADD KEY `idx_lb_partner_coord_partner` (`tenant_id`, `partner_id`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_partner_coordination'
      AND index_name = 'idx_lb_partner_coord_order_unit'
  ) THEN
    ALTER TABLE `lb_partner_coordination`
      ADD KEY `idx_lb_partner_coord_order_unit` (`tenant_id`, `order_id`, `unit_id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_user_address'
      AND index_name = 'idx_lb_user_address_scope_user'
  ) THEN
    ALTER TABLE `lb_user_address`
      ADD KEY `idx_lb_user_address_scope_user` (`tenant_id`, `user_id`, `deleted`, `is_default`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_user_address'
      AND index_name = 'idx_lb_user_address_scope_region'
  ) THEN
    ALTER TABLE `lb_user_address`
      ADD KEY `idx_lb_user_address_scope_region` (`tenant_id`, `adcode`, `deleted`, `user_id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry'
      AND index_name = 'idx_lb_merchant_entry_current'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD KEY `idx_lb_merchant_entry_current` (`tenant_id`, `merchant_id`, `status`, `deleted`, `id`, `region_code`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry'
      AND index_name = 'idx_lb_merchant_entry_region_status'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD KEY `idx_lb_merchant_entry_region_status` (`tenant_id`, `region_code`, `status`, `deleted`, `merchant_id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_order_info'
      AND index_name = 'idx_lb_order_info_merchant_scope'
  ) THEN
    ALTER TABLE `lb_order_info`
      ADD KEY `idx_lb_order_info_merchant_scope` (`tenant_id`, `merchant_id`, `deleted`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_complaint'
      AND index_name = 'idx_lb_complaint_partner_page'
  ) THEN
    ALTER TABLE `lb_complaint`
      ADD KEY `idx_lb_complaint_partner_page` (`tenant_id`, `order_id`, `status`, `deleted`, `create_time`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_appeal'
      AND index_name = 'idx_lb_appeal_partner_page'
  ) THEN
    ALTER TABLE `lb_appeal`
      ADD KEY `idx_lb_appeal_partner_page` (`tenant_id`, `order_id`, `status`, `deleted`, `create_time`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_promoter_relation'
      AND index_name = 'idx_lb_promoter_relation_partner_stat'
  ) THEN
    ALTER TABLE `lb_promoter_relation`
      ADD KEY `idx_lb_promoter_relation_partner_stat` (`tenant_id`, `user_id`, `deleted`, `promoter_id`, `convert_status`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_commission_order'
      AND index_name = 'idx_lb_commission_order_partner_stat'
  ) THEN
    ALTER TABLE `lb_commission_order`
      ADD KEY `idx_lb_commission_order_partner_stat` (`tenant_id`, `user_id`, `promoter_id`, `deleted`, `status`, `source_order_id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_partner_region_rel'
      AND index_name = 'idx_lb_partner_region_rel_scope'
  ) THEN
    ALTER TABLE `lb_partner_region_rel`
      ADD KEY `idx_lb_partner_region_rel_scope` (`tenant_id`, `partner_id`, `status`, `adcode`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_price_report'
      AND index_name = 'idx_lb_merchant_price_report_partner_stat'
  ) THEN
    ALTER TABLE `lb_merchant_price_report`
      ADD KEY `idx_lb_merchant_price_report_partner_stat` (`tenant_id`, `partner_id`, `region_code`, `status`, `deleted`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_message_record'
      AND index_name = 'idx_lb_message_record_partner_instruction'
  ) THEN
    ALTER TABLE `lb_message_record`
      ADD KEY `idx_lb_message_record_partner_instruction` (`tenant_id`, `receiver_user_id`, `send_status`, `message_category`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'infra_file'
      AND index_name = 'idx_infra_file_config_path'
  ) THEN
    ALTER TABLE `infra_file`
      ADD KEY `idx_infra_file_config_path` (`config_id`, `path`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'infra_file'
      AND index_name = 'idx_infra_file_pending_owner'
  ) THEN
    ALTER TABLE `infra_file`
      ADD KEY `idx_infra_file_pending_owner` (`updater`, `size`, `create_time`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'pay_notify_task'
      AND index_name = 'idx_pay_notify_task_status_time_id'
  ) THEN
    ALTER TABLE `pay_notify_task`
      ADD KEY `idx_pay_notify_task_status_time_id` (`status`, `next_notify_time`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_message_optimization'
      AND column_name = 'optimization_key'
  ) THEN
    ALTER TABLE `lb_message_optimization`
      ADD COLUMN `optimization_key` CHAR(64) DEFAULT NULL COMMENT '优化任务维度唯一键（SHA-256）' AFTER `ref_type`;
  END IF;

  UPDATE `lb_message_optimization`
  SET `optimization_key` = SHA2(CONCAT_WS('|',
      COALESCE(`ref_type`, ''),
      COALESCE(CAST(`template_id` AS CHAR), ''),
      COALESCE(CAST(`campaign_id` AS CHAR), ''),
      COALESCE(`scene_code`, ''),
      COALESCE(`channel_type`, ''),
      DATE_FORMAT(`stat_start_date`, '%Y-%m-%d'),
      DATE_FORMAT(`stat_end_date`, '%Y-%m-%d')), 256)
  WHERE `optimization_key` IS NULL OR `optimization_key` = '';

  DELETE older
  FROM `lb_message_optimization` older
  INNER JOIN `lb_message_optimization` newer
    ON newer.`tenant_id` = older.`tenant_id`
    AND newer.`optimization_key` = older.`optimization_key`
    AND newer.`id` > older.`id`;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_message_optimization'
      AND column_name = 'optimization_key' AND is_nullable = 'YES'
  ) THEN
    ALTER TABLE `lb_message_optimization`
      MODIFY COLUMN `optimization_key` CHAR(64) NOT NULL COMMENT '优化任务维度唯一键（SHA-256）';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_message_optimization'
      AND index_name = 'uk_lb_message_optimization_key'
  ) THEN
    ALTER TABLE `lb_message_optimization`
      ADD UNIQUE KEY `uk_lb_message_optimization_key` (`tenant_id`, `optimization_key`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_message_feedback_stat'
      AND column_name = 'stat_key'
  ) THEN
    ALTER TABLE `lb_message_feedback_stat`
      ADD COLUMN `stat_key` CHAR(64) DEFAULT NULL COMMENT '统计维度唯一键（SHA-256）' AFTER `stat_date`;
  END IF;

  UPDATE `lb_message_feedback_stat`
  SET `stat_key` = SHA2(CONCAT_WS('|',
      DATE_FORMAT(`stat_date`, '%Y-%m-%d'),
      COALESCE(`scene_code`, ''),
      COALESCE(`message_category`, ''),
      COALESCE(CAST(`template_id` AS CHAR), ''),
      COALESCE(CAST(`campaign_id` AS CHAR), ''),
      COALESCE(CAST(`push_task_id` AS CHAR), ''),
      COALESCE(`channel_type`, '')), 256)
  WHERE `stat_key` IS NULL OR `stat_key` = '';

  DELETE older
  FROM `lb_message_feedback_stat` older
  INNER JOIN `lb_message_feedback_stat` newer
    ON newer.`tenant_id` = older.`tenant_id`
    AND newer.`stat_key` = older.`stat_key`
    AND newer.`id` > older.`id`;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_message_feedback_stat'
      AND column_name = 'stat_key' AND is_nullable = 'YES'
  ) THEN
    ALTER TABLE `lb_message_feedback_stat`
      MODIFY COLUMN `stat_key` CHAR(64) NOT NULL COMMENT '统计维度唯一键（SHA-256）';
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_message_feedback_stat'
      AND index_name = 'uk_lb_message_feedback_stat_key'
  ) THEN
    ALTER TABLE `lb_message_feedback_stat`
      ADD UNIQUE KEY `uk_lb_message_feedback_stat_key` (`tenant_id`, `stat_key`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_wallet_flow'
      AND column_name = 'related_withdraw_id'
  ) THEN
    ALTER TABLE `lb_wallet_flow`
      ADD COLUMN `related_withdraw_id` BIGINT DEFAULT NULL COMMENT '关联提现申请ID' AFTER `related_refund_id`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_escrow_proof'
      AND index_name = 'idx_lb_escrow_proof_order_unit_status'
  ) THEN
    ALTER TABLE `lb_escrow_proof`
      ADD KEY `idx_lb_escrow_proof_order_unit_status` (`order_id`, `unit_id`, `proof_status`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_wallet_withdraw'
      AND index_name = 'idx_lb_wallet_withdraw_account_time'
  ) THEN
    ALTER TABLE `lb_wallet_withdraw`
      ADD KEY `idx_lb_wallet_withdraw_account_time` (`tenant_id`, `wallet_account_id`, `create_time`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_wallet_withdraw'
      AND index_name = 'idx_lb_wallet_withdraw_card_time'
  ) THEN
    ALTER TABLE `lb_wallet_withdraw`
      ADD KEY `idx_lb_wallet_withdraw_card_time` (`tenant_id`, `bank_card_id`, `create_time`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_wallet_flow'
      AND index_name = 'idx_lb_wallet_flow_related_withdraw_id'
  ) THEN
    ALTER TABLE `lb_wallet_flow`
      ADD KEY `idx_lb_wallet_flow_related_withdraw_id` (`related_withdraw_id`);
  END IF;
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_wallet_flow'
      AND index_name = 'idx_lb_wallet_flow_user_time_id'
  ) THEN
    ALTER TABLE `lb_wallet_flow`
      ADD KEY `idx_lb_wallet_flow_user_time_id` (`user_id`, `create_time`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_wallet_flow'
      AND index_name = 'idx_lb_wallet_flow_order_biz_unit'
  ) THEN
    ALTER TABLE `lb_wallet_flow`
      ADD KEY `idx_lb_wallet_flow_order_biz_unit` (`related_order_id`, `related_unit_id`, `biz_type`, `flow_type`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_wallet_flow'
      AND index_name = 'uk_lb_wallet_flow_refund_biz'
  ) THEN
    ALTER TABLE `lb_wallet_flow`
      ADD UNIQUE KEY `uk_lb_wallet_flow_refund_biz` (`related_refund_id`, `biz_type`, `flow_type`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_order_info'
      AND index_name = 'idx_lb_order_info_accept_filter'
  ) THEN
    ALTER TABLE `lb_order_info`
      ADD KEY `idx_lb_order_info_accept_filter` (`status`, `category_id`, `pricing_mode`, `order_amount`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_order_info'
      AND index_name = 'idx_lb_order_info_status_id'
  ) THEN
    ALTER TABLE `lb_order_info`
      ADD KEY `idx_lb_order_info_status_id` (`status`, `id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_order_attachment'
      AND index_name = 'idx_lb_order_attachment_file_id'
  ) THEN
    ALTER TABLE `lb_order_attachment`
      ADD KEY `idx_lb_order_attachment_file_id` (`file_id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_order_unit_proof'
      AND index_name = 'idx_lb_order_unit_proof_file_id'
  ) THEN
    ALTER TABLE `lb_order_unit_proof`
      ADD KEY `idx_lb_order_unit_proof_file_id` (`file_id`);
  END IF;

  -- Normalize legacy fraction-based divide rates to percentage points. Tax is part of merchant gross share.
  UPDATE `lb_divide_rule`
  SET `merchant_rate` = (`merchant_rate` + `tax_withhold_rate`) * 100,
      `platform_rate` = `platform_rate` * 100,
      `partner_rate` = `partner_rate` * 100,
      `promoter_rate` = `promoter_rate` * 100,
      `tax_withhold_rate` = `tax_withhold_rate` * 100
  WHERE `deleted` = b'0'
    AND (`merchant_rate` + `platform_rate` + `partner_rate` + `promoter_rate` + `tax_withhold_rate`)
        BETWEEN 0.9999 AND 1.0001;

  UPDATE `lb_divide_rule`
  SET `effective_time` = COALESCE(`create_time`, CURRENT_TIMESTAMP)
  WHERE `effective_time` IS NULL;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_divide_rule'
      AND index_name = 'idx_lb_divide_rule_match'
  ) THEN
    ALTER TABLE `lb_divide_rule`
      ADD KEY `idx_lb_divide_rule_match` (`category_id`, `status`, `effective_time`, `city_level`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_message_record'
      AND column_name = 'external_click_token'
  ) THEN
    ALTER TABLE `lb_message_record`
      ADD COLUMN `external_click_token` CHAR(32) DEFAULT NULL COMMENT '外部消息点击随机凭证' AFTER `provider_message_id`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_message_record'
      AND index_name = 'uk_lb_message_record_external_click_token'
  ) THEN
    ALTER TABLE `lb_message_record`
      ADD UNIQUE KEY `uk_lb_message_record_external_click_token` (`external_click_token`);
  END IF;

  UPDATE `lb_register_reminder_record`
  SET `reminder_key` = CASE
      WHEN `social_type` IS NOT NULL THEN CONCAT('SOCIAL_', `social_type`, '_',
          CASE WHEN `social_openid` REGEXP '^[0-9a-f]{64}$' THEN `social_openid`
            ELSE SHA2(COALESCE(NULLIF(`social_openid`, ''), `reminder_key`), 256) END)
      ELSE CONCAT('DEVICE_', CASE WHEN `device_id` REGEXP '^[0-9a-f]{64}$' THEN `device_id`
        ELSE SHA2(COALESCE(NULLIF(`device_id`, ''), `reminder_key`), 256) END)
    END
  WHERE `reminder_key` NOT REGEXP '^(SOCIAL_[0-9]+|DEVICE)_[0-9a-f]{64}$'
    AND LEFT(`reminder_key`, 7) <> 'LEGACY_';

  UPDATE `lb_register_reminder_record`
  SET `device_id` = SHA2(`device_id`, 256)
  WHERE `device_id` IS NOT NULL AND `device_id` <> ''
    AND `device_id` NOT REGEXP '^[0-9a-f]{64}$';

  UPDATE `lb_register_reminder_record`
  SET `social_openid` = SHA2(`social_openid`, 256)
  WHERE `social_openid` IS NOT NULL AND `social_openid` <> ''
    AND `social_openid` NOT REGEXP '^[0-9a-f]{64}$';

  UPDATE `lb_register_reminder_record` duplicate_row
  JOIN (
    SELECT `tenant_id`, `reminder_key`, MIN(`id`) AS keep_id
    FROM `lb_register_reminder_record`
    GROUP BY `tenant_id`, `reminder_key`
    HAVING COUNT(*) > 1
  ) duplicate_group
    ON duplicate_group.`tenant_id` = duplicate_row.`tenant_id`
   AND duplicate_group.`reminder_key` = duplicate_row.`reminder_key`
   AND duplicate_row.`id` <> duplicate_group.`keep_id`
  SET duplicate_row.`reminder_key` = CONCAT('LEGACY_', duplicate_row.`id`, '_',
      LEFT(SHA2(duplicate_row.`reminder_key`, 256), 48));

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_register_reminder_record'
      AND index_name = 'uk_lb_register_reminder_tenant_key'
  ) THEN
    ALTER TABLE `lb_register_reminder_record`
      ADD UNIQUE KEY `uk_lb_register_reminder_tenant_key` (`tenant_id`, `reminder_key`);
  END IF;

  UPDATE `lb_priority_pool_record` duplicate_record
  JOIN `lb_priority_pool_record` canonical_record
    ON canonical_record.`tenant_id` = duplicate_record.`tenant_id`
   AND canonical_record.`merchant_id` = duplicate_record.`merchant_id`
   AND canonical_record.`current_flag` = b'1'
   AND canonical_record.`deleted` = b'0'
   AND canonical_record.`id` > duplicate_record.`id`
  SET duplicate_record.`current_flag` = b'0',
      duplicate_record.`expire_time` = COALESCE(duplicate_record.`expire_time`, CURRENT_TIMESTAMP),
      duplicate_record.`reason_code` = 'MIGRATION_DUPLICATE_CURRENT'
  WHERE duplicate_record.`current_flag` = b'1'
    AND duplicate_record.`deleted` = b'0';

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_priority_pool_record'
      AND column_name = 'active_current_key'
  ) THEN
    ALTER TABLE `lb_priority_pool_record`
      ADD COLUMN `active_current_key` VARCHAR(64) GENERATED ALWAYS AS (
        CASE WHEN `current_flag` = b'1' AND `deleted` = b'0'
          THEN CONCAT(`tenant_id`, ':', `merchant_id`) ELSE NULL END
      ) STORED COMMENT '当前生效记录并发幂等键' AFTER `tenant_id`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_priority_pool_record'
      AND index_name = 'uk_lb_priority_pool_active_current'
  ) THEN
    ALTER TABLE `lb_priority_pool_record`
      ADD UNIQUE KEY `uk_lb_priority_pool_active_current` (`active_current_key`);
  END IF;

  UPDATE `lb_review` duplicate_review
  JOIN `lb_review` canonical_review
    ON canonical_review.`tenant_id` = duplicate_review.`tenant_id`
   AND canonical_review.`unit_id` = duplicate_review.`unit_id`
   AND canonical_review.`from_user_id` = duplicate_review.`from_user_id`
   AND canonical_review.`status` = 'ENABLE'
   AND canonical_review.`deleted` = b'0'
   AND canonical_review.`id` < duplicate_review.`id`
  SET duplicate_review.`status` = 'DISABLED'
  WHERE duplicate_review.`status` = 'ENABLE'
    AND duplicate_review.`deleted` = b'0';

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_review'
      AND column_name = 'active_review_key'
  ) THEN
    ALTER TABLE `lb_review`
      ADD COLUMN `active_review_key` VARCHAR(160) GENERATED ALWAYS AS (
        CASE WHEN `status` = 'ENABLE' AND `deleted` = b'0'
          THEN CONCAT(`tenant_id`, ':', `unit_id`, ':', `from_user_id`) ELSE NULL END
      ) STORED COMMENT '生效评价并发幂等键' AFTER `deleted`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_review'
      AND index_name = 'uk_lb_review_active_key'
  ) THEN
    ALTER TABLE `lb_review`
      ADD UNIQUE KEY `uk_lb_review_active_key` (`active_review_key`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_order_abnormal'
      AND column_name = 'final_audit_status'
  ) THEN
    ALTER TABLE `lb_order_abnormal`
      ADD COLUMN `final_audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING'
        COMMENT '终审状态：PENDING 待终审、APPROVED 通过、REJECTED 驳回' AFTER `handle_time`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_order_abnormal'
      AND column_name = 'final_audit_by'
  ) THEN
    ALTER TABLE `lb_order_abnormal`
      ADD COLUMN `final_audit_by` BIGINT DEFAULT NULL COMMENT '终审人' AFTER `final_audit_status`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_order_abnormal'
      AND column_name = 'final_audit_time'
  ) THEN
    ALTER TABLE `lb_order_abnormal`
      ADD COLUMN `final_audit_time` DATETIME DEFAULT NULL COMMENT '终审时间' AFTER `final_audit_by`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_order_abnormal'
      AND column_name = 'final_audit_remark'
  ) THEN
    ALTER TABLE `lb_order_abnormal`
      ADD COLUMN `final_audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '终审意见' AFTER `final_audit_time`;
  END IF;

  UPDATE `lb_order_abnormal`
  SET `final_audit_status` = 'APPROVED',
      `final_audit_by` = COALESCE(`final_audit_by`, `handle_by`),
      `final_audit_time` = COALESCE(`final_audit_time`, `handle_time`),
      `final_audit_remark` = COALESCE(`final_audit_remark`, `remark`)
  WHERE `handle_status` = 'FINISHED' AND `final_audit_status` = 'PENDING';

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_user_role_apply'
      AND column_name = 'active_apply_key'
  ) THEN
    ALTER TABLE `lb_user_role_apply`
      ADD COLUMN `active_apply_key` VARCHAR(160) GENERATED ALWAYS AS (
        CASE WHEN `audit_status` IN ('PENDING', 'APPROVED') AND `deleted` = b'0'
          THEN CONCAT(`tenant_id`, ':', `user_id`, ':', `apply_role_code`) ELSE NULL END
      ) STORED COMMENT '生效身份申请并发幂等键' AFTER `deleted`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_user_role_apply'
      AND index_name = 'uk_lb_user_role_apply_active'
  ) THEN
    IF EXISTS (
      SELECT 1 FROM `lb_user_role_apply`
      WHERE `audit_status` IN ('PENDING', 'APPROVED') AND `deleted` = b'0'
      GROUP BY `tenant_id`, `user_id`, `apply_role_code`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_user_role_apply contains duplicate active applications';
    END IF;
    ALTER TABLE `lb_user_role_apply`
      ADD UNIQUE KEY `uk_lb_user_role_apply_active` (`active_apply_key`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry'
      AND column_name = 'active_entry_key'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD COLUMN `active_entry_key` VARCHAR(64) GENERATED ALWAYS AS (
        CASE WHEN `status` <> 'REJECTED' AND `deleted` = b'0'
          THEN CONCAT(`tenant_id`, ':', `user_id`) ELSE NULL END
      ) STORED COMMENT '生效入驻申请并发幂等键' AFTER `deleted`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry'
      AND index_name = 'uk_lb_merchant_entry_active'
  ) THEN
    IF EXISTS (
      SELECT 1 FROM `lb_merchant_entry`
      WHERE `status` <> 'REJECTED' AND `deleted` = b'0'
      GROUP BY `tenant_id`, `user_id`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_merchant_entry contains duplicate active applications';
    END IF;
    ALTER TABLE `lb_merchant_entry`
      ADD UNIQUE KEY `uk_lb_merchant_entry_active` (`active_entry_key`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_credit_record'
      AND column_name = 'active_biz_key'
  ) THEN
    ALTER TABLE `lb_credit_record`
      ADD COLUMN `active_biz_key` VARCHAR(255) GENERATED ALWAYS AS (
        CASE WHEN `biz_type` IS NOT NULL AND `biz_id` IS NOT NULL AND `rule_code` IS NOT NULL
            AND `deleted` = b'0'
          THEN CONCAT(`tenant_id`, ':', `user_id`, ':', `rule_code`, ':', `biz_type`, ':', `biz_id`)
          ELSE NULL END
      ) STORED COMMENT '信用业务记录并发幂等键' AFTER `deleted`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_credit_record'
      AND index_name = 'uk_lb_credit_record_active_biz'
  ) THEN
    IF EXISTS (
      SELECT 1 FROM `lb_credit_record`
      WHERE `biz_type` IS NOT NULL AND `biz_id` IS NOT NULL AND `rule_code` IS NOT NULL
        AND `deleted` = b'0'
      GROUP BY `tenant_id`, `user_id`, `rule_code`, `biz_type`, `biz_id`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_credit_record contains duplicate active business records';
    END IF;
    ALTER TABLE `lb_credit_record`
      ADD UNIQUE KEY `uk_lb_credit_record_active_biz` (`active_biz_key`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_reward_order_participation'
      AND index_name = 'uk_lb_reward_participation_user'
  ) THEN
    IF EXISTS (
      SELECT 1 FROM `lb_reward_order_participation`
      GROUP BY `tenant_id`, `reward_order_id`, `participant_user_id`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_reward_order_participation contains duplicate participants';
    END IF;
    ALTER TABLE `lb_reward_order_participation`
      ADD UNIQUE KEY `uk_lb_reward_participation_user`
        (`tenant_id`, `reward_order_id`, `participant_user_id`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_promote_appeal'
      AND column_name = 'active_appeal_key'
  ) THEN
    ALTER TABLE `lb_promote_appeal`
      ADD COLUMN `active_appeal_key` VARCHAR(128) GENERATED ALWAYS AS (
        CASE WHEN `status` = 'PENDING' AND `deleted` = b'0'
          THEN CONCAT(`tenant_id`, ':', `content_id`, ':', `promoter_id`) ELSE NULL END
      ) STORED COMMENT '待审核推广申诉并发幂等键' AFTER `deleted`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_promote_appeal'
      AND index_name = 'uk_lb_promote_appeal_active'
  ) THEN
    IF EXISTS (
      SELECT 1 FROM `lb_promote_appeal`
      WHERE `status` = 'PENDING' AND `deleted` = b'0'
      GROUP BY `tenant_id`, `content_id`, `promoter_id`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_promote_appeal contains duplicate pending appeals';
    END IF;
    ALTER TABLE `lb_promote_appeal`
      ADD UNIQUE KEY `uk_lb_promote_appeal_active` (`active_appeal_key`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_complaint'
      AND column_name = 'active_complaint_key'
  ) THEN
    ALTER TABLE `lb_complaint`
      ADD COLUMN `active_complaint_key` VARCHAR(255) GENERATED ALWAYS AS (
        CASE WHEN `status` IN ('PENDING', 'PROCESSING') AND `deleted` = b'0'
          THEN CONCAT(`tenant_id`, ':', `order_id`, ':', COALESCE(`unit_id`, 0), ':',
            `complainant_user_id`, ':', `respondent_user_id`, ':', `complaint_type`) ELSE NULL END
      ) STORED COMMENT '生效投诉并发幂等键' AFTER `deleted`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_complaint'
      AND index_name = 'uk_lb_complaint_active'
  ) THEN
    IF EXISTS (
      SELECT 1 FROM `lb_complaint`
      WHERE `status` IN ('PENDING', 'PROCESSING') AND `deleted` = b'0'
      GROUP BY `tenant_id`, `order_id`, COALESCE(`unit_id`, 0),
        `complainant_user_id`, `respondent_user_id`, `complaint_type`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_complaint contains duplicate active complaints';
    END IF;
    ALTER TABLE `lb_complaint`
      ADD UNIQUE KEY `uk_lb_complaint_active` (`active_complaint_key`);
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_appeal'
      AND column_name = 'active_appeal_key'
  ) THEN
    ALTER TABLE `lb_appeal`
      ADD COLUMN `active_appeal_key` VARCHAR(192) GENERATED ALWAYS AS (
        CASE WHEN `status` IN ('PENDING', 'PROCESSING') AND `deleted` = b'0'
          THEN CONCAT(`tenant_id`, ':', `order_id`, ':', COALESCE(`unit_id`, 0), ':',
            `user_id`, ':', `appeal_type`) ELSE NULL END
      ) STORED COMMENT '生效申诉并发幂等键' AFTER `deleted`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'lb_appeal'
      AND index_name = 'uk_lb_appeal_active'
  ) THEN
    IF EXISTS (
      SELECT 1 FROM `lb_appeal`
      WHERE `status` IN ('PENDING', 'PROCESSING') AND `deleted` = b'0'
      GROUP BY `tenant_id`, `order_id`, COALESCE(`unit_id`, 0), `user_id`, `appeal_type`
      HAVING COUNT(*) > 1
    ) THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'lb_appeal contains duplicate active appeals';
    END IF;
    ALTER TABLE `lb_appeal`
      ADD UNIQUE KEY `uk_lb_appeal_active` (`active_appeal_key`);
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'infra_api_access_log'
      AND column_name = 'request_params'
  ) THEN
    UPDATE `infra_api_access_log`
    SET `request_params` = '{"redacted":true}'
    WHERE LOWER(`request_params`) REGEXP
      '(password|passwd|pwd|token|authorization|cookie|session_?id|secret|credential|code|captcha|state|sign|bank_?card|card_?no|account_?no|user_?account|cvv|id_?card|id_?no|mobile|phone|email|openid)';
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'infra_api_access_log'
      AND column_name = 'response_body'
  ) THEN
    UPDATE `infra_api_access_log`
    SET `response_body` = NULL
    WHERE LOWER(COALESCE(`response_body`, '')) REGEXP
      '(token|authorization|cookie|session_?id|secret|credential|code|captcha|state|sign|bank_?card|card_?no|account_?no|user_?account|cvv|id_?card|id_?no|mobile|phone|email|openid)';
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'infra_api_error_log'
      AND column_name = 'request_params'
  ) THEN
    UPDATE `infra_api_error_log`
    SET `request_params` = '{"redacted":true}'
    WHERE LOWER(`request_params`) REGEXP
      '(password|passwd|pwd|token|authorization|cookie|session_?id|secret|credential|code|captcha|state|sign|bank_?card|card_?no|account_?no|user_?account|cvv|id_?card|id_?no|mobile|phone|email|openid)';
  END IF;
END$$
DELIMITER ;

CALL `upgrade_lb_security_performance_20260803`();
DROP PROCEDURE IF EXISTS `upgrade_lb_security_performance_20260803`;
