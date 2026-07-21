SET NAMES utf8mb4;
ALTER TABLE `infra_api_access_log`
  MODIFY COLUMN `response_body` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '响应结果';
