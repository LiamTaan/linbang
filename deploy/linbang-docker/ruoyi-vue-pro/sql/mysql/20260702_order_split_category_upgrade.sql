ALTER TABLE `lb_service_category`
  ADD COLUMN IF NOT EXISTS `quantity_unit_label` VARCHAR(32) DEFAULT NULL COMMENT 'quantity unit label' AFTER `support_split`,
  ADD COLUMN IF NOT EXISTS `quantity_split_enabled` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'quantity split enabled' AFTER `quantity_unit_label`,
  ADD COLUMN IF NOT EXISTS `split_default_mode` VARCHAR(32) DEFAULT 'DIRECT' COMMENT 'default split mode' AFTER `quantity_split_enabled`,
  ADD COLUMN IF NOT EXISTS `engineering_category_flag` BIT(1) NOT NULL DEFAULT b'0' COMMENT 'engineering category flag' AFTER `split_default_mode`;

UPDATE `lb_service_category`
SET
  `quantity_unit_label` = CASE `id`
    WHEN 340001 THEN '小时'
    WHEN 340002 THEN '项'
    WHEN 340003 THEN '台'
    WHEN 340004 THEN '单'
    WHEN 340005 THEN '项'
    WHEN 340101 THEN '小时'
    WHEN 340102 THEN '次'
    WHEN 340201 THEN '项'
    WHEN 340202 THEN '扇'
    WHEN 340301 THEN '台'
    WHEN 340302 THEN '台'
    WHEN 340401 THEN '单'
    WHEN 340402 THEN '单'
    WHEN 340501 THEN '件'
    WHEN 340502 THEN '件'
    ELSE COALESCE(`quantity_unit_label`, '份')
  END,
  `quantity_split_enabled` = CASE `id`
    WHEN 340101 THEN b'1'
    WHEN 340202 THEN b'1'
    WHEN 340301 THEN b'1'
    WHEN 340302 THEN b'1'
    WHEN 340501 THEN b'1'
    WHEN 340502 THEN b'1'
    ELSE COALESCE(`quantity_split_enabled`, b'0')
  END,
  `split_default_mode` = CASE `id`
    WHEN 340002 THEN 'BY_PROCESS'
    WHEN 340005 THEN 'BY_PROCESS'
    WHEN 340101 THEN 'BY_PROGRESS'
    WHEN 340102 THEN 'BY_CONTENT'
    WHEN 340201 THEN 'BY_PROCESS'
    WHEN 340202 THEN 'BY_CONTENT'
    WHEN 340301 THEN 'BY_CONTENT'
    WHEN 340302 THEN 'BY_CONTENT'
    WHEN 340501 THEN 'BY_PROCESS'
    WHEN 340502 THEN 'BY_PROCESS'
    ELSE COALESCE(`split_default_mode`, 'DIRECT')
  END,
  `engineering_category_flag` = CASE `id`
    WHEN 340002 THEN b'1'
    WHEN 340005 THEN b'1'
    WHEN 340201 THEN b'1'
    WHEN 340202 THEN b'1'
    WHEN 340501 THEN b'1'
    WHEN 340502 THEN b'1'
    ELSE COALESCE(`engineering_category_flag`, b'0')
  END;
