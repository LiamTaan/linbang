SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `system_social_client` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Application name',
  `social_type` int NOT NULL COMMENT 'Social platform type',
  `user_type` int NOT NULL COMMENT 'User type',
  `status` tinyint NOT NULL COMMENT 'Status: 0 enabled, 1 disabled',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Client AppID',
  `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Client secret',
  `agent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Agent ID',
  `public_key` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Public key',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Creator',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'Updater',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT 'Logical deleted flag',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT 'Tenant ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_social_user_type` (`tenant_id`, `social_type`, `user_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Social client configuration';

DROP PROCEDURE IF EXISTS `upgrade_lb_merchant_entry_snapshots`;
DELIMITER $$
CREATE PROCEDURE `upgrade_lb_merchant_entry_snapshots`()
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry' AND column_name = 'merchant_name_snapshot'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD COLUMN `merchant_name_snapshot` varchar(128) NULL DEFAULT NULL COMMENT 'Merchant name snapshot' AFTER `region_code`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry' AND column_name = 'contact_name_snapshot'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD COLUMN `contact_name_snapshot` varchar(64) NULL DEFAULT NULL COMMENT 'Contact name snapshot' AFTER `merchant_name_snapshot`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry' AND column_name = 'contact_mobile_snapshot'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD COLUMN `contact_mobile_snapshot` varchar(32) NULL DEFAULT NULL COMMENT 'Contact mobile snapshot' AFTER `contact_name_snapshot`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry' AND column_name = 'service_scope_desc_snapshot'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD COLUMN `service_scope_desc_snapshot` varchar(500) NULL DEFAULT NULL COMMENT 'Service scope snapshot' AFTER `contact_mobile_snapshot`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry' AND column_name = 'applicant_real_name_snapshot'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD COLUMN `applicant_real_name_snapshot` varchar(64) NULL DEFAULT NULL COMMENT 'Applicant real name snapshot' AFTER `service_scope_desc_snapshot`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry' AND column_name = 'category_snapshot_json'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD COLUMN `category_snapshot_json` longtext NULL COMMENT 'Service category snapshot JSON' AFTER `applicant_real_name_snapshot`;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'lb_merchant_entry' AND column_name = 'qualification_snapshot_json'
  ) THEN
    ALTER TABLE `lb_merchant_entry`
      ADD COLUMN `qualification_snapshot_json` longtext NULL COMMENT 'Qualification snapshot JSON' AFTER `category_snapshot_json`;
  END IF;
END$$
DELIMITER ;

CALL `upgrade_lb_merchant_entry_snapshots`();
DROP PROCEDURE IF EXISTS `upgrade_lb_merchant_entry_snapshots`;
