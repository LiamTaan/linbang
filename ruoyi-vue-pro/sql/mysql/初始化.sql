-- 邻里互助 - 基础框架精简初始化
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

BEGIN;

DELETE FROM `system_role_menu` WHERE `role_id` IN (1, 20001, 20002, 20003, 20004, 20005);
DELETE FROM `system_user_role` WHERE `user_id` = 1 OR `role_id` IN (1, 20001, 20002, 20003, 20004, 20005);
DELETE FROM `system_user_post` WHERE `user_id` = 1;
DELETE FROM `system_users` WHERE `id` = 1;
DELETE FROM `system_post` WHERE `id` IN (1, 2, 3, 4, 5);
DELETE FROM `system_dept` WHERE `id` IN (100, 101, 102, 103, 104, 105);
DELETE FROM `system_role` WHERE `id` IN (1, 20001, 20002, 20003, 20004, 20005);
DELETE FROM `system_oauth2_client` WHERE `id` = 1 OR `client_id` = 'default';
DELETE FROM `system_social_user_bind`;
DELETE FROM `system_social_user`;
DELETE FROM `infra_config` WHERE `config_key` IN (
  'system.user.init-password',
  'system.user.register-enabled',
  'linbang.admin.dynamic-key.password',
  'linbang.qualification.expire-remind-days',
  'linbang.agreement.service',
  'linbang.agreement.privacy',
  'linbang.agreement.register-version',
  'linbang.agreement.register-title',
  'linbang.agreement.register-content',
  'linbang.agreement.beneficiary-notice',
  'linbang.agreement.trade-version',
  'linbang.agreement.trade-title',
  'linbang.agreement.project-escrow',
  'linbang.agreement.project-escrow-title',
  'linbang.app.tax-reminder',
  'linbang.app.license-agent-entry-url',
  'linbang.app.license-agent-entry-title',
  'linbang.app.amap-js-key',
  'linbang.app.amap-security-js-code',
  'linbang.app.order-price-detail-enabled',
  'linbang.app.mall-entry-enabled',
  'linbang.app.mall-entry-title',
  'linbang.app.mall-entry-url',
  'linbang.app.withdraw-notice',
  'linbang.sensitive.comment-strategy',
  'linbang.sensitive.message-strategy',
  'linbang.sensitive.promote-strategy',
  'linbang.sensitive.complaint-strategy',
  'linbang.sensitive.appeal-strategy',
  'linbang.sensitive.order-publish-strategy',
  'linbang.ocr.enabled',
  'linbang.ocr.provider',
  'linbang.ocr.fallback-mode',
  'linbang.ocr.generic.endpoint',
  'linbang.ocr.generic.api-key'
);
DELETE FROM `infra_file_config`;
DELETE FROM `pay_channel` WHERE `id` IN (1, 2, 3);
DELETE FROM `pay_app` WHERE `id` = 1;
DELETE FROM `system_sms_log`;
DELETE FROM `system_sms_code`;
DELETE FROM `system_sms_template`;
DELETE FROM `system_sms_channel`;
DELETE FROM `system_dict_data`
WHERE `dict_type` IN (
  'system_user_sex', 'infra_job_status', 'infra_config_type', 'infra_operate_type',
  'common_status', 'system_menu_type', 'system_role_type', 'system_data_scope',
  'system_login_result', 'user_type', 'system_sms_channel_code',
  'system_sms_template_type', 'system_sms_send_status', 'system_sms_receive_status',
  'system_login_type', 'infra_file_storage', 'system_oauth2_grant_type',
  'infra_api_error_log_process_status',
  'pay_channel_code', 'pay_notify_status', 'pay_order_status',
  'pay_refund_status', 'pay_transfer_status'
);
DELETE FROM `system_dict_type`
WHERE `type` IN (
  'system_user_sex', 'infra_job_status', 'infra_config_type', 'infra_operate_type',
  'common_status', 'system_menu_type', 'system_role_type', 'system_data_scope',
  'system_login_result', 'user_type', 'system_sms_channel_code',
  'system_sms_template_type', 'system_sms_send_status', 'system_sms_receive_status',
  'system_login_type', 'infra_file_storage', 'system_oauth2_grant_type',
  'infra_api_error_log_process_status',
  'pay_channel_code', 'pay_notify_status', 'pay_order_status',
  'pay_refund_status', 'pay_transfer_status'
);
DELETE FROM `system_menu` WHERE `id` BETWEEN 100170 AND 100179;
DELETE FROM `system_menu` WHERE `id` BETWEEN 110000 AND 110999;
DELETE FROM `lb_member_point_record`;
DELETE FROM `lb_user_reminder`;
DELETE FROM `lb_reward_order_participation`;

INSERT INTO `system_dept`
(`id`, `name`, `parent_id`, `sort`, `leader_user_id`, `phone`, `email`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(100, '邻里互助平台', 0, 1, 1, '18800000000', 'admin@linbang.local', 0, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(101, '运营中心', 100, 1, 1, '18800000001', 'operate@linbang.local', 0, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(102, '财务中心', 100, 2, 1, '18800000002', 'finance@linbang.local', 0, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(103, '风控中心', 100, 3, 1, '18800000003', 'risk@linbang.local', 0, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(104, '审核中心', 100, 4, 1, '18800000004', 'audit@linbang.local', 0, 'admin', NOW(), 'admin', NOW(), b'0', 1),
(105, '合作商管理', 100, 5, 1, '18800000005', 'partner@linbang.local', 0, 'admin', NOW(), 'admin', NOW(), b'0', 1);

INSERT INTO `system_post`
(`id`, `code`, `name`, `sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(1, 'platform_owner', '平台主管', 1, 0, '邻里互助平台默认岗位', 'admin', NOW(), 'admin', NOW(), b'0', 1),
(2, 'operation_manager', '运营经理', 2, 0, '负责平台日常运营', 'admin', NOW(), 'admin', NOW(), b'0', 1),
(3, 'finance_manager', '财务经理', 3, 0, '负责资金审核和对账', 'admin', NOW(), 'admin', NOW(), b'0', 1),
(4, 'risk_manager', '风控经理', 4, 0, '负责风控规则和异常处置', 'admin', NOW(), 'admin', NOW(), b'0', 1),
(5, 'auditor', '审核专员', 5, 0, '负责实名、资质、入驻等审核', 'admin', NOW(), 'admin', NOW(), b'0', 1);

INSERT INTO `system_role`
(`id`, `name`, `code`, `sort`, `data_scope`, `data_scope_dept_ids`, `status`, `type`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(1, '平台超级管理员', 'super_admin', 1, 1, '', 0, 1, '邻里互助平台超级管理员', 'admin', NOW(), 'admin', NOW(), b'0', 1),
(20001, '运营管理员', 'linbang_operator', 2, 1, '', 0, 2, '邻里互助运营管理员', 'admin', NOW(), 'admin', NOW(), b'0', 1),
(20002, '财务管理员', 'linbang_finance', 3, 1, '', 0, 2, '邻里互助财务管理员', 'admin', NOW(), 'admin', NOW(), b'0', 1),
(20003, '风控管理员', 'linbang_risk_admin', 4, 1, '', 0, 2, '邻里互助风控管理员', 'admin', NOW(), 'admin', NOW(), b'0', 1),
(20004, '审核专员', 'linbang_auditor', 5, 1, '', 0, 2, '邻里互助审核专员', 'admin', NOW(), 'admin', NOW(), b'0', 1),
(20005, '区域合作商后台角色', 'linbang_partner_admin', 6, 2, '', 0, 2, '邻里互助区域合作商后台角色', 'admin', NOW(), 'admin', NOW(), b'0', 1);

INSERT INTO `system_users`
(`id`, `username`, `password`, `nickname`, `remark`, `dept_id`, `post_ids`, `email`, `mobile`, `sex`, `avatar`, `status`, `login_ip`, `login_date`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(1, 'admin', '$2a$04$.vd8nPeLwxt6hnSzmAoAyul8BOLX7Cib6QhcxRe30rfvrIPQHH1OG', '邻里互助管理员', '邻里互助平台初始管理员，默认密码 admin123', 100, '[1]', 'admin@linbang.local', '18800000000', 1, '', 0, '', NULL, 'admin', NOW(), 'admin', NOW(), b'0', 1);

INSERT INTO `system_user_post`
(`user_id`, `post_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(1, 1, 'admin', NOW(), 'admin', NOW(), b'0', 1);

INSERT INTO `system_user_role`
(`user_id`, `role_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(1, 1, 'admin', NOW(), 'admin', NOW(), b'0', 1);

INSERT INTO `system_oauth2_client`
(`id`, `client_id`, `secret`, `name`, `logo`, `description`, `status`, `access_token_validity_seconds`, `refresh_token_validity_seconds`, `redirect_uris`, `authorized_grant_types`, `scopes`, `auto_approve_scopes`, `authorities`, `resource_ids`, `additional_information`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(1, 'default', 'admin123', '邻里互助管理后台', '', '邻里互助后台默认登录客户端', 0, 1800, 2592000,
 '["http://127.0.0.1:3000","http://localhost:3000"]',
 '["password","authorization_code","implicit","refresh_token","client_credentials"]',
 '["user.read","user.write"]', '[]', '["user.read","user.write"]', '[]', '{}',
 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `infra_config`
(`id`, `category`, `type`, `name`, `config_key`, `value`, `visible`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(2, 'biz', 1, '用户管理-账号初始密码', 'system.user.init-password', 'admin123', b'0', '邻里互助初始密码', 'admin', NOW(), 'admin', NOW(), b'0'),
(13, 'biz', 2, '用户管理-注册开关', 'system.user.register-enabled', 'true', b'0', '邻里互助前台注册开关', 'admin', NOW(), 'admin', NOW(), b'0'),
(14, 'linbang', 2, '邻里互助-后台动态密钥', 'linbang.admin.dynamic-key.password', 'linbang@2026', b'0', '管理端敏感操作统一二次口令', 'admin', NOW(), 'admin', NOW(), b'0'),
(15, 'linbang', 2, '邻里互助-资质到期提醒阈值', 'linbang.qualification.expire-remind-days', '7,1', b'0', '资质到期前提醒天数，逗号分隔', 'admin', NOW(), 'admin', NOW(), b'0'),
(30, 'linbang_agreement', 2, '邻里互助-服务协议', 'linbang.agreement.service', '欢迎使用邻里互助平台服务。您在平台发布需求、下单、接单、交易、提现等行为，均受平台服务协议约束。', b'1', 'App 协议配置：服务协议', 'admin', NOW(), 'admin', NOW(), b'0'),
(31, 'linbang_agreement', 2, '邻里互助-隐私协议', 'linbang.agreement.privacy', '邻里互助将按照法律法规和平台隐私规则收集、使用、存储和保护您的个人信息，并在必要范围内用于实名认证、资质审核、交易履约与风控。', b'1', 'App 协议配置：隐私协议', 'admin', NOW(), 'admin', NOW(), b'0'),
(32, 'linbang_agreement', 2, '邻里互助-注册协议版本', 'linbang.agreement.register-version', '2026.06.28', b'1', '注册协议版本号', 'admin', NOW(), 'admin', NOW(), b'0'),
(33, 'linbang_agreement', 2, '邻里互助-注册协议标题', 'linbang.agreement.register-title', '邻里互助平台用户注册协议', b'1', '注册协议标题', 'admin', NOW(), 'admin', NOW(), b'0'),
(34, 'linbang_agreement', 2, '邻里互助-注册协议内容', 'linbang.agreement.register-content', '注册成为邻里互助用户前，您应当完整阅读并同意本协议、服务协议、隐私协议及平台交易规则。未完成实名、资质、入驻、绑卡等必要步骤前，平台可限制接单、提现等交易能力。', b'1', '注册协议正文', 'admin', NOW(), 'admin', NOW(), b'0'),
(35, 'linbang_agreement', 2, '邻里互助-受益人连带责任说明', 'linbang.agreement.beneficiary-notice', '如您以个人、企业、子账号或经办人身份在平台发起注册、实名、资质、入驻或收款操作，即视为您已知悉并同意受益人连带责任说明，对提供资料、交易履约、收款结算及违规风险承担相应责任。', b'1', '注册时必须确认的受益人连带责任说明', 'admin', NOW(), 'admin', NOW(), b'0'),
(36, 'linbang_agreement', 2, '邻里互助-交易担保版本', 'linbang.agreement.trade-version', '2026.06.28', b'1', '交易担保协议版本号', 'admin', NOW(), 'admin', NOW(), b'0'),
(37, 'linbang_agreement', 2, '邻里互助-交易担保标题', 'linbang.agreement.trade-title', '邻里互助交易担保说明', b'1', '交易担保协议标题', 'admin', NOW(), 'admin', NOW(), b'0'),
(38, 'linbang_agreement', 2, '邻里互助-项目托管说明', 'linbang.agreement.project-escrow', '订单支付成功后，平台按照订单与单元状态对资金进行托管、解锁、退款与结算管理。存在证件过期、实名失效、资质缺失、银行卡未绑定等情况时，平台可限制接单或结算。', b'1', '项目托管协议正文', 'admin', NOW(), 'admin', NOW(), b'0'),
(39, 'linbang_agreement', 2, '邻里互助-项目托管标题', 'linbang.agreement.project-escrow-title', '邻里互助项目资金托管说明', b'1', '项目托管协议标题', 'admin', NOW(), 'admin', NOW(), b'0'),
(16, 'linbang_platform', 2, '订单价格明细展示开关', 'linbang.app.order-price-detail-enabled', 'true', b'1', '控制 App 订单详情是否展示价格明细', 'admin', NOW(), 'admin', NOW(), b'0'),
(17, 'linbang_platform', 2, '地方商城入口展示开关', 'linbang.app.mall-entry-enabled', 'false', b'1', '控制订单详情是否展示地方商城入口', 'admin', NOW(), 'admin', NOW(), b'0'),
(18, 'linbang_platform', 2, '地方商城入口标题', 'linbang.app.mall-entry-title', '地方商城', b'1', '订单详情展示的地方商城入口标题', 'admin', NOW(), 'admin', NOW(), b'0'),
(19, 'linbang_platform', 2, '地方商城入口链接', 'linbang.app.mall-entry-url', '', b'1', '订单详情展示的地方商城入口链接', 'admin', NOW(), 'admin', NOW(), b'0'),
(20, 'linbang_risk', 2, '评价敏感内容策略', 'linbang.sensitive.comment-strategy', 'REPLACE', b'0', '普通敏感词默认替换后放行', 'admin', NOW(), 'admin', NOW(), b'0'),
(21, 'linbang_risk', 2, '站内消息敏感内容策略', 'linbang.sensitive.message-strategy', 'REPLACE', b'0', '普通敏感词默认替换后放行', 'admin', NOW(), 'admin', NOW(), b'0'),
(22, 'linbang_risk', 2, '推广内容敏感内容策略', 'linbang.sensitive.promote-strategy', 'REPLACE', b'0', '普通敏感词默认替换后放行', 'admin', NOW(), 'admin', NOW(), b'0'),
(23, 'linbang_risk', 2, '投诉敏感内容策略', 'linbang.sensitive.complaint-strategy', 'REPLACE', b'0', '普通敏感词默认替换后放行', 'admin', NOW(), 'admin', NOW(), b'0'),
(24, 'linbang_risk', 2, '申诉敏感内容策略', 'linbang.sensitive.appeal-strategy', 'REPLACE', b'0', '普通敏感词默认替换后放行', 'admin', NOW(), 'admin', NOW(), b'0'),
(25, 'linbang_risk', 2, '发单敏感内容策略', 'linbang.sensitive.order-publish-strategy', 'BLOCK', b'0', '发单场景命中手机号、微信、二维码、外链默认阻断', 'admin', NOW(), 'admin', NOW(), b'0'),
(26, 'linbang_risk', 2, 'OCR 是否启用', 'linbang.ocr.enabled', 'false', b'0', '是否启用图片 OCR 与二维码识别；关闭后发单图片不做 OCR 检测', 'admin', NOW(), 'admin', NOW(), b'0'),
(27, 'linbang_risk', 2, 'OCR 供应商', 'linbang.ocr.provider', 'LOCAL_DISABLED', b'0', '默认关闭，生产可切换 REMOTE_GENERIC', 'admin', NOW(), 'admin', NOW(), b'0'),
(28, 'linbang_risk', 2, 'OCR 失败兜底策略', 'linbang.ocr.fallback-mode', 'BLOCK', b'0', '发单场景默认严格阻断，非发单场景进入失败留痕', 'admin', NOW(), 'admin', NOW(), b'0'),
(29, 'linbang_risk', 2, 'OCR 通用接口地址', 'linbang.ocr.generic.endpoint', '', b'0', 'REMOTE_GENERIC 供应商接入地址，默认留空待生产配置', 'admin', NOW(), 'admin', NOW(), b'0'),
(40, 'linbang_risk', 2, 'OCR 通用接口密钥', 'linbang.ocr.generic.api-key', '', b'0', 'REMOTE_GENERIC 供应商接入密钥，默认留空待生产配置', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `infra_file_config`
(`id`, `name`, `storage`, `remark`, `master`, `config`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(4, '本地存储', 10, '邻里互助默认本地存储，可直接用于开发和联调', b'1',
 '{"@class":"cn.iocoder.yudao.module.infra.framework.file.core.client.local.LocalFileClientConfig","basePath":"D:\\\\user_wuyou\\\\local_life_helper\\\\upload-files","domain":"http://192.168.1.13:48080"}',
 'admin', NOW(), 'admin', NOW(), b'0'),
(5, '阿里云 OSS 存储', 20, '邻里互助 OSS 存储，请替换为正式 bucket 和密钥', b'0',
 '{"@class":"cn.iocoder.yudao.module.infra.framework.file.core.client.s3.S3FileClientConfig","endpoint":"oss-cn-hangzhou.aliyuncs.com","domain":"https://your-bucket.oss-cn-hangzhou.aliyuncs.com","bucket":"your-bucket","accessKey":"your-access-key","accessSecret":"your-access-secret","enablePathStyleAccess":false,"enablePublicAccess":true}',
 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `pay_app`
(`id`, `app_key`, `name`, `status`, `remark`, `order_notify_url`, `refund_notify_url`, `transfer_notify_url`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(1, 'linbang-app', '邻里互助支付应用', 0, '统一接入第三方聚合支付，不按微信/支付宝拆分商户配置',
 'http://127.0.0.1:48080/app-api/linbang/pay/order/update-paid',
 'http://127.0.0.1:48080/app-api/pay/refund/update-refunded',
 'http://127.0.0.1:48080/admin-api/wallet/withdraw/update-transferred',
 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `pay_channel`
(`id`, `code`, `status`, `fee_rate`, `remark`, `app_id`, `config`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(1, 'aggregate', 0, 0, '邻里互助唯一聚合支付通道；baseUrl 按生产聚合支付网关补齐，退款/提现需补充银盛证书与网关配置', 1, '{"@class":"cn.iocoder.yudao.module.pay.framework.pay.core.client.impl.aggregate.AggregatePayClientConfig","baseUrl":"","merchantNo":"826584873720104","merchantName":"深圳市旺佳盈科技有限公司","partnerId":"826584873720104","openApiGatewayUrl":"https://openapi.ysepay.com/gateway.do","transferGatewayUrl":"https://df.ysepay.com/gateway.do","privateKeyFilePath":"","privateKeyPassword":"","ysepayPublicKeyFilePath":"","signType":"RSA","charset":"utf-8","version":"3.0"}', 'admin', NOW(), 'admin', NOW(), b'0', 1);

INSERT INTO `system_sms_channel`
(`id`, `signature`, `code`, `status`, `remark`, `api_key`, `api_secret`, `callback_url`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(1, '邻里互助', 'ALIYUN', 0, '默认阿里云短信渠道，请替换为正式 AccessKey 和签名', 'your-access-key-id', 'your-access-key-secret',
 'http://127.0.0.1:48080/admin-api/system/sms/callback/aliyun',
 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_sms_template`
(`id`, `type`, `status`, `code`, `name`, `content`, `params`, `remark`, `api_template_id`, `channel_id`, `channel_code`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(1, 1, 0, 'user-sms-login', '登录验证码', '您的验证码为 {code}，5 分钟内有效，请勿泄露给他人。', '["code"]',
 '默认阿里云验证码模板，请替换为正式短信模板编号', 'SMS_000000001', 1, 'ALIYUN',
 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_dict_type`
(`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
VALUES
(800001, '用户性别', 'system_user_sex', 0, '系统通用字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800002, '定时任务状态', 'infra_job_status', 0, '基础设施字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800003, '参数类型', 'infra_config_type', 0, '基础设施字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800004, '操作类型', 'infra_operate_type', 0, '基础设施字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800005, '系统状态', 'common_status', 0, '系统通用字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800006, '菜单类型', 'system_menu_type', 0, '系统通用字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800007, '角色类型', 'system_role_type', 0, '系统通用字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800008, '数据权限范围', 'system_data_scope', 0, '系统通用字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800009, '登录结果', 'system_login_result', 0, '系统日志字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800010, '用户类型', 'user_type', 0, '系统通用字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800011, '短信渠道编码', 'system_sms_channel_code', 0, '短信模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800012, '短信模板类型', 'system_sms_template_type', 0, '短信模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800013, '短信发送状态', 'system_sms_send_status', 0, '短信模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800014, '短信接收状态', 'system_sms_receive_status', 0, '短信模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800015, '登录日志类型', 'system_login_type', 0, '系统日志字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800016, '文件存储器', 'infra_file_storage', 0, '文件模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800017, 'OAuth 2.0 授权类型', 'system_oauth2_grant_type', 0, '认证模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800018, '支付渠道编码', 'pay_channel_code', 0, '支付模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800019, '支付回调状态', 'pay_notify_status', 0, '支付模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800020, '支付订单状态', 'pay_order_status', 0, '支付模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800021, '退款订单状态', 'pay_refund_status', 0, '支付模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800022, '转账订单状态', 'pay_transfer_status', 0, '支付模块字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(800023, 'API 异常日志处理状态', 'infra_api_error_log_process_status', 0, '基础设施字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL);

INSERT INTO `system_dict_data`
(`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(810001, 1, '男', '1', 'system_user_sex', 0, 'primary', '', '性别男', 'admin', NOW(), 'admin', NOW(), b'0'),
(810002, 2, '女', '2', 'system_user_sex', 0, 'success', '', '性别女', 'admin', NOW(), 'admin', NOW(), b'0'),
(810003, 1, '正常', '1', 'infra_job_status', 0, 'success', '', '正常状态', 'admin', NOW(), 'admin', NOW(), b'0'),
(810004, 2, '暂停', '2', 'infra_job_status', 0, 'danger', '', '暂停状态', 'admin', NOW(), 'admin', NOW(), b'0'),
(810005, 1, '系统内置', '1', 'infra_config_type', 0, 'danger', '', '系统内置参数', 'admin', NOW(), 'admin', NOW(), b'0'),
(810006, 2, '自定义', '2', 'infra_config_type', 0, 'primary', '', '自定义参数', 'admin', NOW(), 'admin', NOW(), b'0'),
(810007, 0, '其它', '0', 'infra_operate_type', 0, 'default', '', '其它操作', 'admin', NOW(), 'admin', NOW(), b'0'),
(810008, 1, '查询', '1', 'infra_operate_type', 0, 'info', '', '查询操作', 'admin', NOW(), 'admin', NOW(), b'0'),
(810009, 2, '新增', '2', 'infra_operate_type', 0, 'primary', '', '新增操作', 'admin', NOW(), 'admin', NOW(), b'0'),
(810010, 3, '修改', '3', 'infra_operate_type', 0, 'warning', '', '修改操作', 'admin', NOW(), 'admin', NOW(), b'0'),
(810011, 4, '删除', '4', 'infra_operate_type', 0, 'danger', '', '删除操作', 'admin', NOW(), 'admin', NOW(), b'0'),
(810012, 5, '导出', '5', 'infra_operate_type', 0, 'default', '', '导出操作', 'admin', NOW(), 'admin', NOW(), b'0'),
(810013, 1, '开启', '0', 'common_status', 0, 'primary', '', '开启状态', 'admin', NOW(), 'admin', NOW(), b'0'),
(810014, 2, '关闭', '1', 'common_status', 0, 'info', '', '关闭状态', 'admin', NOW(), 'admin', NOW(), b'0'),
(810015, 1, '目录', '1', 'system_menu_type', 0, '', '', '目录', 'admin', NOW(), 'admin', NOW(), b'0'),
(810016, 2, '菜单', '2', 'system_menu_type', 0, '', '', '菜单', 'admin', NOW(), 'admin', NOW(), b'0'),
(810017, 3, '按钮', '3', 'system_menu_type', 0, '', '', '按钮', 'admin', NOW(), 'admin', NOW(), b'0'),
(810018, 1, '内置', '1', 'system_role_type', 0, 'danger', '', '内置角色', 'admin', NOW(), 'admin', NOW(), b'0'),
(810019, 2, '自定义', '2', 'system_role_type', 0, 'primary', '', '自定义角色', 'admin', NOW(), 'admin', NOW(), b'0'),
(810020, 1, '全部数据权限', '1', 'system_data_scope', 0, '', '', '全部数据权限', 'admin', NOW(), 'admin', NOW(), b'0'),
(810021, 2, '指定部门数据权限', '2', 'system_data_scope', 0, '', '', '指定部门数据权限', 'admin', NOW(), 'admin', NOW(), b'0'),
(810022, 3, '本部门数据权限', '3', 'system_data_scope', 0, '', '', '本部门数据权限', 'admin', NOW(), 'admin', NOW(), b'0'),
(810023, 4, '本部门及以下数据权限', '4', 'system_data_scope', 0, '', '', '本部门及以下数据权限', 'admin', NOW(), 'admin', NOW(), b'0'),
(810024, 5, '仅本人数据权限', '5', 'system_data_scope', 0, '', '', '仅本人数据权限', 'admin', NOW(), 'admin', NOW(), b'0'),
(810025, 0, '成功', '0', 'system_login_result', 0, 'success', '', '登录成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(810026, 10, '账号或密码不正确', '10', 'system_login_result', 0, 'primary', '', '账号或密码不正确', 'admin', NOW(), 'admin', NOW(), b'0'),
(810027, 20, '用户被禁用', '20', 'system_login_result', 0, 'warning', '', '用户被禁用', 'admin', NOW(), 'admin', NOW(), b'0'),
(810028, 30, '验证码不存在', '30', 'system_login_result', 0, 'info', '', '验证码不存在', 'admin', NOW(), 'admin', NOW(), b'0'),
(810029, 31, '验证码不正确', '31', 'system_login_result', 0, 'info', '', '验证码不正确', 'admin', NOW(), 'admin', NOW(), b'0'),
(810030, 100, '未知异常', '100', 'system_login_result', 0, 'danger', '', '未知异常', 'admin', NOW(), 'admin', NOW(), b'0'),
(810031, 1, '会员', '1', 'user_type', 0, 'primary', '', '会员', 'admin', NOW(), 'admin', NOW(), b'0'),
(810032, 2, '管理员', '2', 'user_type', 0, 'success', '', '管理员', 'admin', NOW(), 'admin', NOW(), b'0'),
(810033, 1, '阿里云', 'ALIYUN', 'system_sms_channel_code', 0, 'primary', '', '阿里云短信', 'admin', NOW(), 'admin', NOW(), b'0'),
(810034, 2, '腾讯云', 'TENCENT', 'system_sms_channel_code', 0, 'success', '', '腾讯云短信', 'admin', NOW(), 'admin', NOW(), b'0'),
(810035, 3, '华为云', 'HUAWEI', 'system_sms_channel_code', 0, 'info', '', '华为云短信', 'admin', NOW(), 'admin', NOW(), b'0'),
(810036, 4, '七牛云', 'QINIU', 'system_sms_channel_code', 0, 'warning', '', '七牛云短信', 'admin', NOW(), 'admin', NOW(), b'0'),
(810037, 1, '验证码', '1', 'system_sms_template_type', 0, 'warning', '', '验证码模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(810038, 2, '通知', '2', 'system_sms_template_type', 0, 'primary', '', '通知模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(810039, 3, '营销', '3', 'system_sms_template_type', 0, 'danger', '', '营销模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(810040, 0, '初始化', '0', 'system_sms_send_status', 0, 'primary', '', '初始化', 'admin', NOW(), 'admin', NOW(), b'0'),
(810041, 10, '发送成功', '10', 'system_sms_send_status', 0, 'success', '', '发送成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(810042, 20, '发送失败', '20', 'system_sms_send_status', 0, 'danger', '', '发送失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(810043, 30, '不发送', '30', 'system_sms_send_status', 0, 'info', '', '不发送', 'admin', NOW(), 'admin', NOW(), b'0'),
(810044, 0, '等待结果', '0', 'system_sms_receive_status', 0, 'primary', '', '等待结果', 'admin', NOW(), 'admin', NOW(), b'0'),
(810045, 10, '接收成功', '10', 'system_sms_receive_status', 0, 'success', '', '接收成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(810046, 20, '接收失败', '20', 'system_sms_receive_status', 0, 'danger', '', '接收失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(810047, 100, '账号登录', '100', 'system_login_type', 0, 'primary', '', '账号登录', 'admin', NOW(), 'admin', NOW(), b'0'),
(810048, 101, '社交登录', '101', 'system_login_type', 0, 'info', '', '社交登录', 'admin', NOW(), 'admin', NOW(), b'0'),
(810049, 103, '短信登录', '103', 'system_login_type', 0, 'warning', '', '短信登录', 'admin', NOW(), 'admin', NOW(), b'0'),
(810050, 200, '主动登出', '200', 'system_login_type', 0, 'primary', '', '主动登出', 'admin', NOW(), 'admin', NOW(), b'0'),
(810051, 202, '强制登出', '202', 'system_login_type', 0, 'danger', '', '强制登出', 'admin', NOW(), 'admin', NOW(), b'0'),
(810052, 1, '数据库', '1', 'infra_file_storage', 0, 'primary', '', '数据库存储', 'admin', NOW(), 'admin', NOW(), b'0'),
(810053, 10, '本地文件系统', '10', 'infra_file_storage', 0, 'success', '', '本地文件系统', 'admin', NOW(), 'admin', NOW(), b'0'),
(810054, 12, 'SFTP', '12', 'infra_file_storage', 0, 'warning', '', 'SFTP 存储', 'admin', NOW(), 'admin', NOW(), b'0'),
(810055, 20, '对象存储', '20', 'infra_file_storage', 0, 'info', '', '对象存储', 'admin', NOW(), 'admin', NOW(), b'0'),
(810056, 1, '密码模式', 'password', 'system_oauth2_grant_type', 0, 'primary', '', '密码模式', 'admin', NOW(), 'admin', NOW(), b'0'),
(810057, 2, '授权码模式', 'authorization_code', 'system_oauth2_grant_type', 0, 'success', '', '授权码模式', 'admin', NOW(), 'admin', NOW(), b'0'),
(810058, 3, '简化模式', 'implicit', 'system_oauth2_grant_type', 0, 'warning', '', '简化模式', 'admin', NOW(), 'admin', NOW(), b'0'),
(810059, 4, '刷新模式', 'refresh_token', 'system_oauth2_grant_type', 0, 'info', '', '刷新模式', 'admin', NOW(), 'admin', NOW(), b'0'),
(810060, 5, '客户端模式', 'client_credentials', 'system_oauth2_grant_type', 0, 'default', '', '客户端模式', 'admin', NOW(), 'admin', NOW(), b'0'),
(810061, 0, '聚合支付', 'aggregate', 'pay_channel_code', 0, 'warning', '', '第三方聚合支付，邻里互助唯一支付通道', 'admin', NOW(), 'admin', NOW(), b'0'),
(810074, 0, '等待通知', '0', 'pay_notify_status', 0, 'primary', '', '等待通知', 'admin', NOW(), 'admin', NOW(), b'0'),
(810075, 10, '通知成功', '10', 'pay_notify_status', 0, 'success', '', '通知成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(810076, 20, '通知失败', '20', 'pay_notify_status', 0, 'danger', '', '通知失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(810077, 21, '请求成功但是结果失败', '21', 'pay_notify_status', 0, 'warning', '', '请求成功但是结果失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(810078, 22, '请求失败', '22', 'pay_notify_status', 0, 'warning', '', '请求失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(810079, 0, '待支付', '0', 'pay_order_status', 0, 'warning', '', '待支付', 'admin', NOW(), 'admin', NOW(), b'0'),
(810080, 10, '支付成功', '10', 'pay_order_status', 0, 'success', '', '支付成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(810081, 20, '已退款', '20', 'pay_order_status', 0, 'info', '', '已退款', 'admin', NOW(), 'admin', NOW(), b'0'),
(810082, 30, '已关闭', '30', 'pay_order_status', 0, 'default', '', '已关闭', 'admin', NOW(), 'admin', NOW(), b'0'),
(810083, 0, '待退款', '0', 'pay_refund_status', 0, 'warning', '', '待退款', 'admin', NOW(), 'admin', NOW(), b'0'),
(810084, 10, '退款成功', '10', 'pay_refund_status', 0, 'success', '', '退款成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(810085, 20, '退款失败', '20', 'pay_refund_status', 0, 'danger', '', '退款失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(810086, 0, '等待转账', '0', 'pay_transfer_status', 0, 'default', '', '等待转账', 'admin', NOW(), 'admin', NOW(), b'0'),
(810087, 5, '转账进行中', '5', 'pay_transfer_status', 0, 'info', '', '转账进行中', 'admin', NOW(), 'admin', NOW(), b'0'),
(810088, 10, '转账成功', '10', 'pay_transfer_status', 0, 'success', '', '转账成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(810089, 20, '转账失败', '20', 'pay_transfer_status', 0, 'danger', '', '转账失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(810090, 0, '未处理', '0', 'infra_api_error_log_process_status', 0, 'primary', '', '未处理', 'admin', NOW(), 'admin', NOW(), b'0'),
(810091, 1, '已处理', '1', 'infra_api_error_log_process_status', 0, 'success', '', '已处理', 'admin', NOW(), 'admin', NOW(), b'0'),
(810092, 2, '已忽略', '2', 'infra_api_error_log_process_status', 0, 'danger', '', '已忽略', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_menu`
(`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(100000, '系统管理', '', 1, 900, 0, '/system', 'ep:setting', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100100, '后台用户', '', 2, 10, 100000, 'user', 'ep:user', 'system/user/index', 'SystemUser', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100101, '用户查询', 'system:user:query', 3, 1, 100100, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100102, '用户新增', 'system:user:create', 3, 2, 100100, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100103, '用户修改', 'system:user:update', 3, 3, 100100, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100104, '用户删除', 'system:user:delete', 3, 4, 100100, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100105, '用户导出', 'system:user:export', 3, 5, 100100, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100106, '用户重置密码', 'system:user:update-password', 3, 6, 100100, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100107, '用户分配角色', 'system:permission:assign-user-role', 3, 7, 100100, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100110, '角色权限', '', 2, 20, 100000, 'role', 'ep:avatar', 'system/role/index', 'SystemRole', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100111, '角色查询', 'system:role:query', 3, 1, 100110, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100112, '角色新增', 'system:role:create', 3, 2, 100110, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100113, '角色修改', 'system:role:update', 3, 3, 100110, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100114, '角色删除', 'system:role:delete', 3, 4, 100110, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100115, '角色导出', 'system:role:export', 3, 5, 100110, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100116, '角色菜单权限', 'system:permission:assign-role-menu', 3, 6, 100110, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100117, '角色数据权限', 'system:permission:assign-role-data-scope', 3, 7, 100110, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100120, '菜单管理', '', 2, 30, 100000, 'menu', 'ep:menu', 'system/menu/index', 'SystemMenu', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100121, '菜单查询', 'system:menu:query', 3, 1, 100120, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100122, '菜单新增', 'system:menu:create', 3, 2, 100120, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100123, '菜单修改', 'system:menu:update', 3, 3, 100120, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100124, '菜单删除', 'system:menu:delete', 3, 4, 100120, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100125, '部门管理', '', 2, 35, 100000, 'dept', 'ep:office-building', 'system/dept/index', 'SystemDept', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100126, '部门查询', 'system:dept:query', 3, 1, 100125, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100127, '部门新增', 'system:dept:create', 3, 2, 100125, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100128, '部门修改', 'system:dept:update', 3, 3, 100125, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100129, '部门删除', 'system:dept:delete', 3, 4, 100125, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100130, '字典管理', '', 2, 40, 100000, 'dict', 'ep:collection', 'system/dict/index', 'SystemDict', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100131, '字典查询', 'system:dict:query', 3, 1, 100130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100132, '字典新增', 'system:dict:create', 3, 2, 100130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100133, '字典修改', 'system:dict:update', 3, 3, 100130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100134, '字典删除', 'system:dict:delete', 3, 4, 100130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100135, '字典导出', 'system:dict:export', 3, 5, 100130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100143, '配置管理', '', 2, 45, 100000, 'config', 'ep:setting', 'infra/config/index', 'InfraConfig', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100144, '参数查询', 'infra:config:query', 3, 1, 100143, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100145, '参数新增', 'infra:config:create', 3, 2, 100143, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100146, '参数修改', 'infra:config:update', 3, 3, 100143, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100147, '参数删除', 'infra:config:delete', 3, 4, 100143, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100148, '参数导出', 'infra:config:export', 3, 5, 100143, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100140, '操作日志', '', 2, 50, 100000, 'operatelog', 'ep:document', 'system/operatelog/index', 'SystemOperateLog', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100141, '操作日志查询', 'system:operate-log:query', 3, 1, 100140, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100142, '操作日志导出', 'system:operate-log:export', 3, 2, 100140, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100150, '登录日志', '', 2, 60, 100000, 'loginlog', 'ep:key', 'system/loginlog/index', 'SystemLoginLog', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100151, '登录日志查询', 'system:login-log:query', 3, 1, 100150, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100152, '登录日志导出', 'system:login-log:export', 3, 2, 100150, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100160, '文件管理', '', 2, 70, 100000, 'file', 'ep:folder', 'infra/file/index', 'InfraFile', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100161, '文件查询', 'infra:file:query', 3, 1, 100160, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100162, '文件删除', 'infra:file:delete', 3, 2, 100160, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100163, '文件存储配置', '', 2, 80, 100000, 'file-config', 'ep:files', 'infra/fileConfig/index', 'InfraFileConfig', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100164, '文件配置查询', 'infra:file-config:query', 3, 1, 100163, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100165, '文件配置新增', 'infra:file-config:create', 3, 2, 100163, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100166, '文件配置修改', 'infra:file-config:update', 3, 3, 100163, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100167, '文件配置删除', 'infra:file-config:delete', 3, 4, 100163, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100168, '文件配置导出', 'infra:file-config:export', 3, 5, 100163, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100170, '支付中心', '', 1, 950, 0, '/pay', 'ep:wallet', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100171, '支付应用', '', 2, 10, 100170, 'app', 'ep:grid', 'pay/app/index', 'PayApp', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100172, '支付应用查询', 'pay:app:query', 3, 1, 100171, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100173, '支付应用新增', 'pay:app:create', 3, 2, 100171, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100174, '支付应用修改', 'pay:app:update', 3, 3, 100171, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100175, '支付应用删除', 'pay:app:delete', 3, 4, 100171, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100179, '支付通知', 'pay:notify:query', 2, 20, 100170, 'notify', 'ep:bell', 'pay/notify/index', 'PayNotify', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100180, '短信渠道', '', 2, 100, 100000, 'sms-channel', 'ep:message', 'system/sms/channel/index', 'SystemSmsChannel', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100181, '短信渠道查询', 'system:sms-channel:query', 3, 1, 100180, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100182, '短信渠道新增', 'system:sms-channel:create', 3, 2, 100180, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100183, '短信渠道修改', 'system:sms-channel:update', 3, 3, 100180, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100184, '短信渠道删除', 'system:sms-channel:delete', 3, 4, 100180, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100190, '短信模板', '', 2, 110, 100000, 'sms-template', 'ep:chat-dot-round', 'system/sms/template/index', 'SystemSmsTemplate', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100191, '短信模板查询', 'system:sms-template:query', 3, 1, 100190, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100192, '短信模板新增', 'system:sms-template:create', 3, 2, 100190, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100193, '短信模板修改', 'system:sms-template:update', 3, 3, 100190, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100194, '短信模板删除', 'system:sms-template:delete', 3, 4, 100190, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100195, '短信模板导出', 'system:sms-template:export', 3, 5, 100190, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100196, '短信模板测试', 'system:sms-template:send-sms', 3, 6, 100190, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100197, '短信日志', '', 2, 120, 100000, 'sms-log', 'ep:document-copy', 'system/sms/log/index', 'SystemSmsLog', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100198, '短信日志查询', 'system:sms-log:query', 3, 1, 100197, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(100199, '短信日志导出', 'system:sms-log:export', 3, 2, 100197, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, `id`, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM `system_menu`
WHERE `id` BETWEEN 100000 AND 100199;

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

-- 邻里互助 - 业务初始化
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

BEGIN;

DELETE FROM `system_role_menu` WHERE `menu_id` BETWEEN 110000 AND 111099;
DELETE FROM `system_menu` WHERE `id` BETWEEN 110000 AND 111099;
DELETE FROM `system_dict_data`
WHERE `dict_type` IN (
  'lb_pricing_mode', 'lb_role_code', 'lb_audit_status', 'lb_order_status',
  'lb_order_unit_status', 'lb_pay_status', 'lb_refund_status',
  'lb_withdraw_status', 'lb_complaint_status', 'lb_appeal_status',
  'lb_qualification_type',
  'lb_merchant_entry_status', 'lb_role_apply_status',
  'lb_price_report_audit_status', 'lb_message_push_task_status'
);
DELETE FROM `system_dict_type`
  WHERE `type` IN (
    'lb_pricing_mode', 'lb_role_code', 'lb_audit_status', 'lb_order_status',
    'lb_order_unit_status', 'lb_pay_status', 'lb_refund_status',
    'lb_withdraw_status', 'lb_complaint_status', 'lb_appeal_status',
    'lb_qualification_type',
    'lb_merchant_entry_status', 'lb_role_apply_status',
    'lb_price_report_audit_status', 'lb_message_push_task_status'
  );
DELETE FROM `lb_message_template` WHERE `id` BETWEEN 380001 AND 380099;
DELETE FROM `lb_message_template` WHERE `id` BETWEEN 380108 AND 380120;
DELETE FROM `lb_divide_rule` WHERE `id` BETWEEN 300001 AND 300099;
DELETE FROM `lb_risk_rule` WHERE `id` BETWEEN 310001 AND 310099;
DELETE FROM `lb_sensitive_word` WHERE `id` BETWEEN 320001 AND 320099;
DELETE FROM `lb_credit_rule` WHERE `id` BETWEEN 330001 AND 330099;
DELETE FROM `lb_credit_level_benefit` WHERE `id` BETWEEN 331001 AND 331099;
DELETE FROM `lb_credit_record` WHERE `id` BETWEEN 331001 AND 331099;
DELETE FROM `lb_help_faq` WHERE `id` BETWEEN 360001 AND 360099;
DELETE FROM `lb_service_category` WHERE `id` BETWEEN 340001 AND 340199;
DELETE FROM `system_notify_template` WHERE `code` IN (
  'lb_order_created', 'lb_refund_audited', 'lb_withdraw_audited',
  'lb_merchant_accept_enabled', 'lb_bind_bank_card_required', 'lb_cert_exemption_audited'
);

INSERT INTO `system_dict_type`
(`id`, `name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
VALUES
(900001, '邻里互助计价方式', 'lb_pricing_mode', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900002, '邻里互助角色标签', 'lb_role_code', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900003, '邻里互助审核状态', 'lb_audit_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900004, '邻里互助订单状态', 'lb_order_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900005, '邻里互助单元订单状态', 'lb_order_unit_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900006, '邻里互助支付状态', 'lb_pay_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900007, '邻里互助退款状态', 'lb_refund_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900008, '邻里互助提现状态', 'lb_withdraw_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900009, '邻里互助投诉状态', 'lb_complaint_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900010, '邻里互助申诉状态', 'lb_appeal_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900011, '邻里互助入驻状态', 'lb_merchant_entry_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900012, '邻里互助身份申请状态', 'lb_role_apply_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900013, '邻里互助价格申报审核状态', 'lb_price_report_audit_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900014, '邻里互助消息推送任务状态', 'lb_message_push_task_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900015, '邻里互助消息分类', 'lb_message_category', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900016, '邻里互助消息渠道', 'lb_message_channel_type', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900017, '邻里互助消息已读状态', 'lb_message_read_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900018, '邻里互助消息投放来源', 'lb_message_campaign_source_type', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900019, '邻里互助消息投放审核状态', 'lb_message_campaign_audit_status', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900020, '邻里互助消息投放目标模式', 'lb_message_target_mode', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL),
(900021, '邻里互助资质类型', 'lb_qualification_type', 0, '邻里互助业务字典', 'admin', NOW(), 'admin', NOW(), b'0', NULL);

INSERT INTO `system_dict_data`
(`id`, `sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(910001, 1, '一口价', 'FIXED_PRICE', 'lb_pricing_mode', 0, 'primary', '', '固定总价计价', 'admin', NOW(), 'admin', NOW(), b'0'),
(910002, 2, '承包', 'CONTRACT', 'lb_pricing_mode', 0, 'success', '', '按整包/整项目承包计价', 'admin', NOW(), 'admin', NOW(), b'0'),
(910003, 3, '外发', 'OUTSOURCING', 'lb_pricing_mode', 0, 'warning', '', '按外发协作计价', 'admin', NOW(), 'admin', NOW(), b'0'),
(910004, 4, '计时', 'HOURLY', 'lb_pricing_mode', 0, 'info', '', '按小时/时长计价', 'admin', NOW(), 'admin', NOW(), b'0'),
(910092, 5, '按单位', 'BY_UNIT', 'lb_pricing_mode', 0, 'danger', '', '按件/台/米/平方等单位计价', 'admin', NOW(), 'admin', NOW(), b'0'),
(910005, 1, '普通用户', 'USER', 'lb_role_code', 0, 'primary', '', '普通用户', 'admin', NOW(), 'admin', NOW(), b'0'),
(910006, 2, '服务商', 'MERCHANT', 'lb_role_code', 0, 'success', '', '服务商', 'admin', NOW(), 'admin', NOW(), b'0'),
(910007, 3, '推广员', 'PROMOTER', 'lb_role_code', 0, 'warning', '', '推广员', 'admin', NOW(), 'admin', NOW(), b'0'),
(910008, 4, '区域合作商', 'PARTNER', 'lb_role_code', 0, 'danger', '', '区域合作商', 'admin', NOW(), 'admin', NOW(), b'0'),
(910009, 1, '待审核', 'PENDING', 'lb_audit_status', 0, 'warning', '', '待审核', 'admin', NOW(), 'admin', NOW(), b'0'),
(910010, 2, '已通过', 'APPROVED', 'lb_audit_status', 0, 'success', '', '已通过', 'admin', NOW(), 'admin', NOW(), b'0'),
(910011, 3, '已驳回', 'REJECTED', 'lb_audit_status', 0, 'danger', '', '已驳回', 'admin', NOW(), 'admin', NOW(), b'0'),
(910012, 1, '待支付', 'PENDING_PAY', 'lb_order_status', 0, 'warning', '', '待支付', 'admin', NOW(), 'admin', NOW(), b'0'),
(910013, 2, '待接单', 'PENDING_ACCEPT', 'lb_order_status', 0, 'primary', '', '待接单', 'admin', NOW(), 'admin', NOW(), b'0'),
(910014, 3, '已接单', 'ACCEPTED', 'lb_order_status', 0, 'info', '', '已接单', 'admin', NOW(), 'admin', NOW(), b'0'),
(910015, 4, '服务中', 'SERVING', 'lb_order_status', 0, 'primary', '', '服务中', 'admin', NOW(), 'admin', NOW(), b'0'),
(910016, 5, '待确认', 'PENDING_CONFIRM', 'lb_order_status', 0, 'warning', '', '待确认', 'admin', NOW(), 'admin', NOW(), b'0'),
(910017, 6, '售后中', 'AFTER_SALE', 'lb_order_status', 0, 'danger', '', '售后中', 'admin', NOW(), 'admin', NOW(), b'0'),
(910018, 7, '已完成', 'FINISHED', 'lb_order_status', 0, 'success', '', '已完成', 'admin', NOW(), 'admin', NOW(), b'0'),
(910019, 8, '已退款', 'REFUNDED', 'lb_order_status', 0, 'info', '', '已退款', 'admin', NOW(), 'admin', NOW(), b'0'),
(910020, 9, '已关闭', 'CLOSED', 'lb_order_status', 0, 'default', '', '已关闭', 'admin', NOW(), 'admin', NOW(), b'0'),
(910021, 1, '待生成', 'PENDING_CREATE', 'lb_order_unit_status', 0, 'warning', '', '待生成', 'admin', NOW(), 'admin', NOW(), b'0'),
(910022, 2, '待接单', 'PENDING_ACCEPT', 'lb_order_unit_status', 0, 'primary', '', '待接单', 'admin', NOW(), 'admin', NOW(), b'0'),
(910023, 3, '已接单', 'ACCEPTED', 'lb_order_unit_status', 0, 'info', '', '已接单', 'admin', NOW(), 'admin', NOW(), b'0'),
(910024, 4, '服务中', 'SERVING', 'lb_order_unit_status', 0, 'primary', '', '服务中', 'admin', NOW(), 'admin', NOW(), b'0'),
(910025, 5, '待验收', 'PENDING_CONFIRM', 'lb_order_unit_status', 0, 'warning', '', '待验收', 'admin', NOW(), 'admin', NOW(), b'0'),
(910026, 6, '已完成', 'FINISHED', 'lb_order_unit_status', 0, 'success', '', '已完成', 'admin', NOW(), 'admin', NOW(), b'0'),
(910027, 7, '申诉中', 'APPEALING', 'lb_order_unit_status', 0, 'danger', '', '申诉中', 'admin', NOW(), 'admin', NOW(), b'0'),
(910028, 8, '已退款', 'REFUNDED', 'lb_order_unit_status', 0, 'info', '', '已退款', 'admin', NOW(), 'admin', NOW(), b'0'),
(910029, 9, '已关闭', 'CLOSED', 'lb_order_unit_status', 0, 'default', '', '已关闭', 'admin', NOW(), 'admin', NOW(), b'0'),
(910030, 1, '待支付', 'WAITING', 'lb_pay_status', 0, 'warning', '', '待支付', 'admin', NOW(), 'admin', NOW(), b'0'),
(910031, 2, '支付成功', 'SUCCESS', 'lb_pay_status', 0, 'success', '', '支付成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(910032, 3, '支付失败', 'FAILED', 'lb_pay_status', 0, 'danger', '', '支付失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(910033, 4, '已关闭', 'CLOSED', 'lb_pay_status', 0, 'default', '', '已关闭', 'admin', NOW(), 'admin', NOW(), b'0'),
(910034, 1, '待审核', 'PENDING', 'lb_refund_status', 0, 'warning', '', '待审核', 'admin', NOW(), 'admin', NOW(), b'0'),
(910035, 2, '已通过', 'APPROVED', 'lb_refund_status', 0, 'primary', '', '已通过', 'admin', NOW(), 'admin', NOW(), b'0'),
(910036, 3, '已驳回', 'REJECTED', 'lb_refund_status', 0, 'danger', '', '已驳回', 'admin', NOW(), 'admin', NOW(), b'0'),
(910037, 4, '退款成功', 'SUCCESS', 'lb_refund_status', 0, 'success', '', '退款成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(910038, 5, '退款失败', 'FAILED', 'lb_refund_status', 0, 'danger', '', '退款失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(910039, 1, '待审核', 'PENDING', 'lb_withdraw_status', 0, 'warning', '', '待审核', 'admin', NOW(), 'admin', NOW(), b'0'),
(910040, 2, '审核通过', 'APPROVED', 'lb_withdraw_status', 0, 'primary', '', '审核通过', 'admin', NOW(), 'admin', NOW(), b'0'),
(910041, 3, '审核驳回', 'REJECTED', 'lb_withdraw_status', 0, 'danger', '', '审核驳回', 'admin', NOW(), 'admin', NOW(), b'0'),
(910042, 4, '打款成功', 'SUCCESS', 'lb_withdraw_status', 0, 'success', '', '打款成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(910043, 5, '打款失败', 'FAILED', 'lb_withdraw_status', 0, 'danger', '', '打款失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(910044, 1, '待受理', 'PENDING', 'lb_complaint_status', 0, 'warning', '', '待受理', 'admin', NOW(), 'admin', NOW(), b'0'),
(910045, 2, '处理中', 'PROCESSING', 'lb_complaint_status', 0, 'primary', '', '处理中', 'admin', NOW(), 'admin', NOW(), b'0'),
(910046, 3, '已完结', 'FINISHED', 'lb_complaint_status', 0, 'success', '', '已完结', 'admin', NOW(), 'admin', NOW(), b'0'),
(910047, 4, '已驳回', 'REJECTED', 'lb_complaint_status', 0, 'danger', '', '已驳回', 'admin', NOW(), 'admin', NOW(), b'0'),
(910048, 1, '待审核', 'PENDING', 'lb_appeal_status', 0, 'warning', '', '待审核', 'admin', NOW(), 'admin', NOW(), b'0'),
(910049, 2, '处理中', 'PROCESSING', 'lb_appeal_status', 0, 'primary', '', '处理中', 'admin', NOW(), 'admin', NOW(), b'0'),
(910050, 3, '通过', 'APPROVED', 'lb_appeal_status', 0, 'success', '', '通过', 'admin', NOW(), 'admin', NOW(), b'0'),
(910051, 4, '驳回', 'REJECTED', 'lb_appeal_status', 0, 'danger', '', '驳回', 'admin', NOW(), 'admin', NOW(), b'0'),
(910052, 5, '已完结', 'FINISHED', 'lb_appeal_status', 0, 'info', '', '已完结', 'admin', NOW(), 'admin', NOW(), b'0'),
(910053, 1, '待审核', 'PENDING', 'lb_merchant_entry_status', 0, 'warning', '', '待审核', 'admin', NOW(), 'admin', NOW(), b'0'),
(910054, 2, '初审通过', 'FIRST_APPROVED', 'lb_merchant_entry_status', 0, 'primary', '', '初审通过', 'admin', NOW(), 'admin', NOW(), b'0'),
(910055, 3, '终审通过', 'APPROVED', 'lb_merchant_entry_status', 0, 'success', '', '终审通过', 'admin', NOW(), 'admin', NOW(), b'0'),
(910056, 4, '已驳回', 'REJECTED', 'lb_merchant_entry_status', 0, 'danger', '', '已驳回', 'admin', NOW(), 'admin', NOW(), b'0'),
(910057, 1, '待审核', 'PENDING', 'lb_role_apply_status', 0, 'warning', '', '身份申请待审核', 'admin', NOW(), 'admin', NOW(), b'0'),
(910058, 2, '已通过', 'APPROVED', 'lb_role_apply_status', 0, 'success', '', '身份申请审核通过', 'admin', NOW(), 'admin', NOW(), b'0'),
(910059, 3, '已驳回', 'REJECTED', 'lb_role_apply_status', 0, 'danger', '', '身份申请审核驳回', 'admin', NOW(), 'admin', NOW(), b'0'),
(910060, 1, '待审核', 'PENDING', 'lb_price_report_audit_status', 0, 'warning', '', '价格申报待审核', 'admin', NOW(), 'admin', NOW(), b'0'),
(910061, 2, '已通过', 'APPROVED', 'lb_price_report_audit_status', 0, 'success', '', '价格申报审核通过', 'admin', NOW(), 'admin', NOW(), b'0'),
(910062, 3, '已驳回', 'REJECTED', 'lb_price_report_audit_status', 0, 'danger', '', '价格申报审核驳回', 'admin', NOW(), 'admin', NOW(), b'0'),
(910063, 1, '待执行', 'PENDING', 'lb_message_push_task_status', 0, 'warning', '', '推送任务待执行', 'admin', NOW(), 'admin', NOW(), b'0'),
(910064, 2, '执行中', 'PROCESSING', 'lb_message_push_task_status', 0, 'primary', '', '推送任务执行中', 'admin', NOW(), 'admin', NOW(), b'0'),
(910065, 3, '执行成功', 'SUCCESS', 'lb_message_push_task_status', 0, 'success', '', '推送任务执行成功', 'admin', NOW(), 'admin', NOW(), b'0'),
(910066, 4, '部分失败', 'PARTIAL_FAILED', 'lb_message_push_task_status', 0, 'warning', '', '推送任务部分失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(910067, 5, '执行失败', 'FAILED', 'lb_message_push_task_status', 0, 'danger', '', '推送任务执行失败', 'admin', NOW(), 'admin', NOW(), b'0'),
(910068, 6, '已取消', 'CANCELLED', 'lb_message_push_task_status', 0, 'info', '', '推送任务已取消', 'admin', NOW(), 'admin', NOW(), b'0'),
(910069, 1, '系统类', 'SYSTEM', 'lb_message_category', 0, 'info', '', '系统消息', 'admin', NOW(), 'admin', NOW(), b'0'),
(910070, 2, '金额类', 'FINANCE', 'lb_message_category', 0, 'warning', '', '金额变动消息', 'admin', NOW(), 'admin', NOW(), b'0'),
(910071, 3, '订单类', 'ORDER', 'lb_message_category', 0, 'primary', '', '订单状态与核销消息', 'admin', NOW(), 'admin', NOW(), b'0'),
(910072, 4, '合规类', 'COMPLIANCE', 'lb_message_category', 0, 'danger', '', '证书到期与合规消息', 'admin', NOW(), 'admin', NOW(), b'0'),
(910073, 5, '纠纷类', 'DISPUTE', 'lb_message_category', 0, 'warning', '', '投诉申诉消息', 'admin', NOW(), 'admin', NOW(), b'0'),
(910074, 6, '营销类', 'MARKETING', 'lb_message_category', 0, 'success', '', '广告与运营消息', 'admin', NOW(), 'admin', NOW(), b'0'),
(910075, 1, '站内弹窗/小程序弹窗', 'APP_POPUP', 'lb_message_channel_type', 0, 'primary', '', '消息中心与前台弹窗通道', 'admin', NOW(), 'admin', NOW(), b'0'),
(910076, 2, '公众号/订阅消息', 'WECHAT_MP_TEMPLATE', 'lb_message_channel_type', 0, 'success', '', '公众号/小程序模板消息通道', 'admin', NOW(), 'admin', NOW(), b'0'),
(910077, 3, '短信', 'SMS', 'lb_message_channel_type', 0, 'warning', '', '短信发送通道', 'admin', NOW(), 'admin', NOW(), b'0'),
(910078, 4, 'App语音朗读', 'APP_VOICE', 'lb_message_channel_type', 0, 'info', '', 'App 客户端语音朗读通道', 'admin', NOW(), 'admin', NOW(), b'0'),
(910079, 1, '未读', 'UNREAD', 'lb_message_read_status', 0, 'warning', '', '消息未读', 'admin', NOW(), 'admin', NOW(), b'0'),
(910080, 2, '已读', 'READ', 'lb_message_read_status', 0, 'success', '', '消息已读', 'admin', NOW(), 'admin', NOW(), b'0'),
(910081, 1, '用户定向申请', 'USER_DIRECTED', 'lb_message_campaign_source_type', 0, 'warning', '', '用户发起定向推送申请', 'admin', NOW(), 'admin', NOW(), b'0'),
(910082, 2, '管理定向投放', 'ADMIN_DIRECTED', 'lb_message_campaign_source_type', 0, 'primary', '', '管理员直接发起投放', 'admin', NOW(), 'admin', NOW(), b'0'),
(910083, 3, '系统触发', 'SYSTEM_TRIGGER', 'lb_message_campaign_source_type', 0, 'info', '', '系统业务触发投放', 'admin', NOW(), 'admin', NOW(), b'0'),
(910084, 4, '广告投放', 'AD', 'lb_message_campaign_source_type', 0, 'success', '', '广告或运营活动投放', 'admin', NOW(), 'admin', NOW(), b'0'),
(910085, 1, '待审核', 'PENDING', 'lb_message_campaign_audit_status', 0, 'warning', '', '投放待审核', 'admin', NOW(), 'admin', NOW(), b'0'),
(910086, 2, '已通过', 'APPROVED', 'lb_message_campaign_audit_status', 0, 'success', '', '投放审核通过', 'admin', NOW(), 'admin', NOW(), b'0'),
(910087, 3, '已驳回', 'REJECTED', 'lb_message_campaign_audit_status', 0, 'danger', '', '投放审核驳回', 'admin', NOW(), 'admin', NOW(), b'0'),
(910088, 4, '已取消', 'CANCELLED', 'lb_message_campaign_audit_status', 0, 'info', '', '投放已取消', 'admin', NOW(), 'admin', NOW(), b'0'),
(910089, 1, '全平台', 'FULL_PLATFORM', 'lb_message_target_mode', 0, 'primary', '', '全平台用户', 'admin', NOW(), 'admin', NOW(), b'0'),
(910090, 2, '辖区', 'JURISDICTION', 'lb_message_target_mode', 0, 'success', '', '按辖区定向', 'admin', NOW(), 'admin', NOW(), b'0'),
(910091, 3, '自定义筛选', 'CUSTOM_FILTER', 'lb_message_target_mode', 0, 'warning', '', '按区域/类目/角色/时段筛选', 'admin', NOW(), 'admin', NOW(), b'0'),
(910093, 1, '营业执照', 'BUSINESS_LICENSE', 'lb_qualification_type', 0, 'primary', '', '企业/个体工商户营业执照', 'admin', NOW(), 'admin', NOW(), b'0'),
(910094, 2, '电工证', 'ELECTRICIAN', 'lb_qualification_type', 0, 'warning', '', '电工相关上岗资质', 'admin', NOW(), 'admin', NOW(), b'0'),
(910095, 3, '焊工证', 'WELDER', 'lb_qualification_type', 0, 'danger', '', '焊接作业相关资质', 'admin', NOW(), 'admin', NOW(), b'0'),
(910096, 4, '空调制冷证', 'HVAC_TECHNICIAN', 'lb_qualification_type', 0, 'info', '', '空调安装与制冷维修资质', 'admin', NOW(), 'admin', NOW(), b'0'),
(910097, 5, '管道作业证', 'PLUMBING_TECHNICIAN', 'lb_qualification_type', 0, 'success', '', '给排水、管道维修相关资质', 'admin', NOW(), 'admin', NOW(), b'0'),
(910098, 6, '保洁服务资质', 'CLEANING_SERVICE', 'lb_qualification_type', 0, 'primary', '', '专业保洁、清洗相关服务资质', 'admin', NOW(), 'admin', NOW(), b'0'),
(910099, 7, '安装服务资质', 'INSTALLATION_SERVICE', 'lb_qualification_type', 0, 'success', '', '家具、卫浴等安装服务资质', 'admin', NOW(), 'admin', NOW(), b'0'),
(910100, 8, '安全生产证', 'SAFETY_CERTIFICATE', 'lb_qualification_type', 0, 'danger', '', '安全生产、施工安全相关证件', 'admin', NOW(), 'admin', NOW(), b'0'),
(910101, 9, '特种作业操作证', 'SPECIAL_OPERATION', 'lb_qualification_type', 0, 'warning', '', '高处、电工作业等特种作业操作证', 'admin', NOW(), 'admin', NOW(), b'0'),
(910102, 10, '健康证', 'HEALTH_CERTIFICATE', 'lb_qualification_type', 0, 'info', '', '从业健康证明', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_service_category`
(`id`, `parent_id`, `category_name`, `category_level`, `sort_no`, `icon`, `default_pricing_mode`, `supported_pricing_modes`, `support_split`, `support_invoice`, `risk_level`, `labor_category_flag`, `force_agreement_type`, `invoice_rate_reminder_text`, `status`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(340001, 0, '家政保洁', 1, 10, 'ep:house', 'HOURLY', '["HOURLY","BY_UNIT"]', b'0', b'1', 'LOW', b'0', 'TRADE_GUARANTEE', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340002, 0, '家居维修', 1, 20, 'ep:tools', 'CONTRACT', '["FIXED_PRICE","CONTRACT","BY_UNIT"]', b'1', b'1', 'MEDIUM', b'1', 'PROJECT_ESCROW', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340003, 0, '家电清洗', 1, 30, 'ep:operation', 'FIXED_PRICE', '["FIXED_PRICE","BY_UNIT"]', b'0', b'1', 'LOW', b'0', 'TRADE_GUARANTEE', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340004, 0, '跑腿代办', 1, 40, 'ep:van', 'HOURLY', '["HOURLY","FIXED_PRICE"]', b'0', b'0', 'MEDIUM', b'0', 'TRADE_GUARANTEE', NULL, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340005, 0, '社区安装', 1, 50, 'ep:box', 'CONTRACT', '["FIXED_PRICE","CONTRACT","BY_UNIT"]', b'1', b'1', 'MEDIUM', b'1', 'PROJECT_ESCROW', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340101, 340001, '日常保洁', 2, 11, NULL, 'HOURLY', '["HOURLY"]', b'0', b'1', 'LOW', b'0', 'TRADE_GUARANTEE', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340102, 340001, '深度清洁', 2, 12, NULL, 'FIXED_PRICE', '["FIXED_PRICE","BY_UNIT"]', b'1', b'1', 'MEDIUM', b'0', 'TRADE_GUARANTEE', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340201, 340002, '水电维修', 2, 21, NULL, 'CONTRACT', '["FIXED_PRICE","CONTRACT","BY_UNIT"]', b'1', b'1', 'MEDIUM', b'1', 'PROJECT_ESCROW', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340202, 340002, '门锁门窗维修', 2, 22, NULL, 'FIXED_PRICE', '["FIXED_PRICE","BY_UNIT"]', b'1', b'1', 'MEDIUM', b'1', 'PROJECT_ESCROW', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340301, 340003, '空调清洗', 2, 31, NULL, 'FIXED_PRICE', '["FIXED_PRICE","BY_UNIT"]', b'0', b'1', 'LOW', b'0', 'TRADE_GUARANTEE', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340302, 340003, '油烟机清洗', 2, 32, NULL, 'FIXED_PRICE', '["FIXED_PRICE","BY_UNIT"]', b'0', b'1', 'LOW', b'0', 'TRADE_GUARANTEE', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340401, 340004, '同城跑腿', 2, 41, NULL, 'HOURLY', '["HOURLY","FIXED_PRICE"]', b'0', b'0', 'MEDIUM', b'0', 'TRADE_GUARANTEE', NULL, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340402, 340004, '代办代购', 2, 42, NULL, 'FIXED_PRICE', '["FIXED_PRICE"]', b'0', b'0', 'MEDIUM', b'0', 'TRADE_GUARANTEE', NULL, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340501, 340005, '家具安装', 2, 51, NULL, 'CONTRACT', '["FIXED_PRICE","CONTRACT","BY_UNIT"]', b'1', b'1', 'MEDIUM', b'1', 'PROJECT_ESCROW', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(340502, 340005, '卫浴安装', 2, 52, NULL, 'CONTRACT', '["FIXED_PRICE","CONTRACT","BY_UNIT"]', b'1', b'1', 'MEDIUM', b'1', 'PROJECT_ESCROW', '开票需求通常会降低接单率，请确认是否继续', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_order_split_rule`
(`id`, `rule_name`, `rule_code`, `match_mode`, `category_id`, `applicable_pricing_modes`, `min_order_amount`, `min_quantity`, `min_worker_count`, `split_mode`, `default_unit_count`, `unit_amount_limit`, `unit_template`, `sort_no`, `status`, `remark`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(350001, '用工安装类多人拆单', 'LABOR_INSTALL_BY_PERSON', 'ANY', 340005, '["CONTRACT","BY_UNIT"]', NULL, NULL, 2, 'BY_PERSON', 2, 500.00, '{"titlePrefix":"安装单元","contentTemplate":"安装服务单元 {seq}","lockReasonTemplate":"待前序安装单元完成"}', 10, 'ENABLE', '用工安装类按人数自动拆分', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(350002, '保洁深度服务金额拆单', 'CLEANING_AMOUNT_SPLIT', 'ANY', 340001, '["FIXED_PRICE","BY_UNIT"]', 300.00, NULL, NULL, 'BY_CONTENT', 2, 200.00, '{"titlePrefix":"保洁单元","contentTemplate":"保洁内容单元 {seq}","lockReasonTemplate":"待前序保洁单元完成"}', 20, 'ENABLE', '金额超过 300 的深度保洁订单自动拆分', 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_sensitive_word`
(`id`, `word`, `word_type`, `match_type`, `block_level`, `status`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(320001, '私彩', 'ILLEGAL', 'CONTAINS', 'BLOCK', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(320002, '刷单', 'RISK', 'CONTAINS', 'REVIEW', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(320003, '线下转账', 'CONTACT', 'CONTAINS', 'REVIEW', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(320004, '私下交易', 'RISK', 'CONTAINS', 'REVIEW', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(320005, '色情服务', 'ILLEGAL', 'CONTAINS', 'BLOCK', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_risk_rule`
(`id`, `rule_code`, `rule_name`, `rule_group`, `rule_value`, `value_type`, `status`, `remark`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(310001, 'ORDER_DAILY_CREATE_LIMIT', '用户单日发单次数上限', 'ORDER', '20', 'INTEGER', 'ENABLE', '超过阈值时转人工复核', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(310002, 'ORDER_PAY_TIMEOUT_MINUTES', '待支付超时分钟数', 'ORDER', '30', 'INTEGER', 'ENABLE', '超时自动关闭订单', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(310003, 'REFUND_NEED_AUDIT_AMOUNT', '退款人工审核阈值', 'REFUND', '30000', 'FEN', 'ENABLE', '超过 300 元进入人工审核', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(310004, 'WITHDRAW_NEED_AUDIT_AMOUNT', '提现人工审核阈值', 'WITHDRAW', '50000', 'FEN', 'ENABLE', '超过 500 元进入人工审核', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(310005, 'MERCHANT_ACCEPT_RADIUS_KM', '服务商抢单距离上限', 'MATCH', '15', 'DECIMAL', 'ENABLE', '超出服务半径不给予推送', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(310006, 'COMPLAINT_ESCALATION_HOURS', '投诉超时升级小时数', 'AFTER_SALE', '24', 'INTEGER', 'ENABLE', '超时未处理自动升级', 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_credit_rule`
(`id`, `rule_code`, `rule_name`, `score_change`, `trigger_type`, `status`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(330001, 'REAL_NAME_APPROVED', '实名认证通过', 10, 'AUTH', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(330002, 'MERCHANT_ENTRY_APPROVED', '服务商入驻通过', 15, 'MERCHANT', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(330003, 'ORDER_FINISHED_POSITIVE', '订单完结且评价良好', 5, 'ORDER', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(330007, 'ORDER_FINISHED_NEUTRAL', '订单完结中评', 0, 'ORDER', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(330008, 'ORDER_FINISHED_NEGATIVE', '订单完结差评', -10, 'ORDER', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(330004, 'ORDER_CANCELLED_TIMEOUT', '下单后超时取消', -8, 'ORDER', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(330005, 'COMPLAINT_CONFIRMED', '投诉核实成立', -15, 'AFTER_SALE', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(330006, 'APPEAL_APPROVED', '申诉审核通过', 6, 'AFTER_SALE', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(330009, 'VIOLATION_CONFIRMED', '违规核实成立', -20, 'RISK', 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_credit_level_benefit`
(`id`, `level_code`, `level_name`, `benefit_title`, `benefit_desc`, `sort_no`, `status`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(331001, 'EXCELLENT', '优秀', '优先展示', '可进入优质服务优先展示与推荐排序', 1, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(331002, 'EXCELLENT', '优秀', '优先池候选', '满足连续好评条件后可进入好评优先池', 2, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(331003, 'NORMAL', '正常', '标准排序', '可参与平台标准排序和正常接单流转', 1, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(331004, 'NORMAL', '正常', '信用可提升', '继续获得好评可提升到优秀等级', 2, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(331005, 'WARNING', '预警', '流量受限', '排序权重下降，并展示信用预警提示', 1, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(331006, 'WARNING', '预警', '需持续改善', '需持续获得好评或消除负向记录后恢复正常', 2, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(331007, 'DISABLED', '禁用', '风控限制展示', '仅做信用禁用展示和风控标记，不直接封禁登录', 1, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(331008, 'DISABLED', '禁用', '需人工复核', '需通过申诉、整改或人工处理后恢复信用等级', 2, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_divide_rule`
(`id`, `rule_name`, `city_level`, `category_id`, `merchant_rate`, `platform_rate`, `partner_rate`, `promoter_rate`, `tax_withhold_rate`, `min_withdraw_amount`, `status`, `effective_time`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(300001, '一线城市标准分账', 'TIER_1', 340201, 0.7800, 0.1200, 0.0500, 0.0200, 0.0300, 50.00, 'ENABLE', NOW(), 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(300002, '二线城市标准分账', 'TIER_2', 340101, 0.8000, 0.1100, 0.0400, 0.0200, 0.0300, 30.00, 'ENABLE', NOW(), 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(300003, '跑腿即时服务分账', 'TIER_2', 340401, 0.7600, 0.1400, 0.0500, 0.0200, 0.0300, 20.00, 'ENABLE', NOW(), 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_notify_template`
(`id`, `name`, `code`, `nickname`, `content`, `type`, `params`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(390001, '订单创建提醒', 'lb_order_created', '邻里互助系统', '您的订单 {orderNo} 已创建，请及时关注接单进度。', 2, '["orderNo"]', 0, '邻里互助业务通知模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(390002, '退款审核结果通知', 'lb_refund_audited', '邻里互助系统', '您的退款申请 {refundNo} 审核结果为 {auditStatus}。', 2, '["refundNo","auditStatus"]', 0, '邻里互助业务通知模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(390003, '提现审核结果通知', 'lb_withdraw_audited', '邻里互助系统', '您的提现申请 {withdrawNo} 审核结果为 {auditStatus}。', 2, '["withdrawNo","auditStatus"]', 0, '邻里互助业务通知模板', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_message_scene`
(`id`, `scene_code`, `scene_name`, `message_category`, `default_channels`, `mandatory_sms`, `voice_enabled`, `status`, `biz_type`, `remark`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(381001, 'SYSTEM_NOTICE', '系统通知', 'SYSTEM', 'APP_POPUP', b'0', b'1', 'ENABLE', 'SYSTEM', '默认历史迁移场景', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(381002, 'FINANCE_WITHDRAW_AUDIT', '提现审核通知', 'FINANCE', 'APP_POPUP,SMS', b'1', b'1', 'ENABLE', 'WITHDRAW', '金额变动类场景，短信必发', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(381003, 'FINANCE_REFUND_SUCCESS', '退款结果通知', 'FINANCE', 'APP_POPUP,SMS', b'1', b'1', 'ENABLE', 'REFUND', '金额变动类场景，短信必发', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(381004, 'QUALIFICATION_EXPIRE_REMINDER', '资质到期提醒', 'COMPLIANCE', 'APP_POPUP,APP_VOICE', b'0', b'1', 'ENABLE', 'QUALIFICATION_EXPIRY', '到期前 D-7 / D-1 提醒', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(381005, 'QUALIFICATION_EXPIRE_DISABLE', '资质到期禁接单通知', 'COMPLIANCE', 'APP_POPUP,SMS', b'0', b'1', 'ENABLE', 'QUALIFICATION_EXPIRY', '到期后禁接单通知', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(381006, 'ORDER_STATUS_CHANGED', '订单状态通知', 'ORDER', 'APP_POPUP,APP_VOICE', b'0', b'1', 'ENABLE', 'ORDER', '订单状态流转消息', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(381007, 'DISPUTE_CREATED', '纠纷发起通知', 'DISPUTE', 'APP_POPUP,SMS', b'0', b'1', 'ENABLE', 'COMPLAINT', '投诉创建通知', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(381008, 'DISPUTE_RESULT', '纠纷结果通知', 'DISPUTE', 'APP_POPUP,SMS', b'0', b'1', 'ENABLE', 'COMPLAINT', '投诉处理结果通知', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(381009, 'VERIFY_SUCCESS', '核销成功通知', 'ORDER', 'APP_POPUP,APP_VOICE', b'0', b'1', 'ENABLE', 'ORDER', '核销成功通知', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(381010, 'MARKETING_BROADCAST', '运营/广告投放', 'MARKETING', 'APP_POPUP', b'0', b'1', 'ENABLE', 'MARKETING', '运营推送与广告投放', 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_message_template`
(`id`, `template_code`, `template_name`, `scene_code`, `message_category`, `template_type`, `channel_type`, `title_template`, `content_template`, `route_type`, `route_value`, `mp_template_id`, `sms_template_code`, `voice_text_template`, `sort`, `status`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(380001, 'lb_role_apply_audited', '身份申请审核结果通知', 'SYSTEM_NOTICE', 'SYSTEM', 'BIZ', 'APP_POPUP', '身份申请审核结果', '您的身份申请审核结果已更新，请前往消息中心查看。', 'APP_PAGE', '/pages/message/detail', NULL, NULL, '您的身份申请审核结果已更新，请尽快查看。', 10, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380002, 'lb_merchant_entry_audited', '入驻审核结果通知', 'SYSTEM_NOTICE', 'SYSTEM', 'BIZ', 'APP_POPUP', '入驻审核结果', '您的服务商入驻审核结果已更新，请前往消息中心查看。', 'APP_PAGE', '/pages/message/detail', NULL, NULL, '您的入驻审核结果已更新，请查看。', 20, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380003, 'lb_price_report_audited', '价格申报审核结果通知', 'SYSTEM_NOTICE', 'SYSTEM', 'BIZ', 'APP_POPUP', '价格申报结果', '您的价格申报审核结果已更新，请前往消息中心查看。', 'APP_PAGE', '/pages/message/detail', NULL, NULL, '您的价格申报审核结果已更新，请查看。', 30, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380004, 'lb_refund_audited', '退款审核结果通知', 'FINANCE_REFUND_SUCCESS', 'FINANCE', 'BIZ', 'APP_POPUP', '退款结果通知', '您的退款申请审核结果已更新，请前往消息中心查看。', 'APP_PAGE', '/pages/wallet/refund', NULL, NULL, '您的退款结果已更新，请查看。', 40, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380005, 'lb_withdraw_audited', '提现审核结果通知', 'FINANCE_WITHDRAW_AUDIT', 'FINANCE', 'BIZ', 'APP_POPUP', '提现结果通知', '您的提现申请审核结果已更新，请前往消息中心查看。', 'APP_PAGE', '/pages/wallet/withdraw', NULL, NULL, '您的提现审核结果已更新，请查看。', 50, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380006, 'lb_qualification_expire_reminder', '资质到期提醒通知', 'QUALIFICATION_EXPIRE_REMINDER', 'COMPLIANCE', 'BIZ', 'APP_POPUP', '资质到期提醒', '您的资质临近到期，请尽快更新，避免影响接单。', 'APP_PAGE', '/pages/qualification/index', NULL, NULL, '您的资质即将到期，请尽快更新。', 60, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380007, 'lb_qualification_expire_disable', '资质到期限制接单通知', 'QUALIFICATION_EXPIRE_DISABLE', 'COMPLIANCE', 'BIZ', 'APP_POPUP', '资质已到期', '您的资质已到期，当前已限制接单，请完成更新后恢复。', 'APP_PAGE', '/pages/qualification/index', NULL, NULL, '您的资质已到期，当前已限制接单。', 70, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380008, 'lb_withdraw_audited_sms', '提现审核短信通知', 'FINANCE_WITHDRAW_AUDIT', 'FINANCE', 'BIZ', 'SMS', '提现审核短信通知', '您的提现申请审核结果已更新，请注意查收。', 'APP_PAGE', '/pages/wallet/withdraw', NULL, 'lb_withdraw_audited', NULL, 80, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380009, 'lb_refund_audited_sms', '退款结果短信通知', 'FINANCE_REFUND_SUCCESS', 'FINANCE', 'BIZ', 'SMS', '退款结果短信通知', '您的退款申请审核结果已更新，请注意查收。', 'APP_PAGE', '/pages/wallet/refund', NULL, 'lb_refund_audited', NULL, 90, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380100, 'lb_match_pushed', '匹配推送通知', 'SYSTEM_NOTICE', 'ORDER', 'BIZ', 'APP_POPUP', '新的待抢订单', '系统已为您推送新的待抢订单，请尽快处理。', 'APP_PAGE', '/pages/order/grab', NULL, NULL, '系统已为您推送新的待抢订单，请尽快处理。', 100, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380101, 'lb_grab_countdown', '抢单倒计时提醒', 'SYSTEM_NOTICE', 'ORDER', 'BIZ', 'APP_VOICE', '抢单倒计时提醒', '您有新的抢单倒计时，请在 1 分钟内完成接单。', 'APP_PAGE', '/pages/order/grab', NULL, NULL, '您有新的抢单倒计时，请在1分钟内完成接单。', 110, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380102, 'lb_order_flow_advice', '流单建议通知', 'SYSTEM_NOTICE', 'ORDER', 'BIZ', 'APP_POPUP', '订单暂未匹配成功', '订单暂未匹配成功，建议您适当降低条件或加价。', 'APP_PAGE', '/pages/order/detail', NULL, NULL, '订单暂未匹配成功，建议您适当降低条件或加价。', 120, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380103, 'lb_order_flow_refunded', '流单退款结果通知', 'FINANCE_REFUND_SUCCESS', 'FINANCE', 'BIZ', 'APP_POPUP', '流单退款结果通知', '流单单元已发起原路退款，请留意退款结果。', 'APP_PAGE', '/pages/wallet/refund', NULL, NULL, '流单单元已发起原路退款，请留意退款结果。', 130, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380104, 'lb_priority_pool_entered', '优先池入池通知', 'SYSTEM_NOTICE', 'SYSTEM', 'BIZ', 'APP_POPUP', '已进入优先池', '恭喜您进入优先推送池，后续将获得更高层内排序。', 'APP_PAGE', '/pages/merchant/priority', NULL, NULL, '恭喜您进入优先推送池，后续将获得更高层内排序。', 140, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380105, 'lb_priority_pool_exited', '优先池出池通知', 'SYSTEM_NOTICE', 'SYSTEM', 'BIZ', 'APP_POPUP', '已退出优先池', '您已退出优先推送池，请关注评价和投诉状态。', 'APP_PAGE', '/pages/merchant/priority', NULL, NULL, '您已退出优先推送池，请关注评价和投诉状态。', 150, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380106, 'lb_special_cert_audited', '专项资质审核通知', 'SYSTEM_NOTICE', 'COMPLIANCE', 'BIZ', 'APP_POPUP', '专项资质审核结果', '您的专项资质审核结果已更新，请及时查看。', 'APP_PAGE', '/pages/qualification/index', NULL, NULL, '您的专项资质审核结果已更新，请及时查看。', 160, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380107, 'lb_showcase_reward_audited', '晒单优先审核通知', 'SYSTEM_NOTICE', 'SYSTEM', 'BIZ', 'APP_POPUP', '晒单优先审核结果', '您的晒单优先申请审核结果已更新，请及时查看。', 'APP_PAGE', '/pages/merchant/showcase', NULL, NULL, '您的晒单优先申请审核结果已更新，请及时查看。', 170, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380108, 'lb_merchant_accept_enabled', '接单权限开通提醒', 'SYSTEM_NOTICE', 'SYSTEM', 'BIZ', 'APP_POPUP', '接单权限已开通', '您的服务商终审已通过，接单权限现已开通，请及时前往接单。', 'APP_PAGE', '/pages/merchant/entry', NULL, NULL, '您的接单权限已开通，请及时前往接单。', 180, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380109, 'lb_bind_bank_card_required', '银行卡绑定提醒', 'SYSTEM_NOTICE', 'SYSTEM', 'BIZ', 'APP_POPUP', '请先绑定银行卡', '您的入驻终审已通过，但尚未绑定有效银行卡，当前暂不可接单，请先完成绑卡。', 'APP_PAGE', '/pages/wallet/bank-card', NULL, NULL, '您尚未绑定有效银行卡，请先完成绑卡后再接单。', 190, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380110, 'lb_cert_exemption_audited', '证件豁免审核结果通知', 'SYSTEM_NOTICE', 'COMPLIANCE', 'BIZ', 'APP_POPUP', '证件豁免审核结果', '您的证件豁免申请审核结果已更新，请及时查看处理。', 'APP_PAGE', '/pages/qualification/index', NULL, NULL, '您的证件豁免审核结果已更新，请及时查看。', 200, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `lb_match_strategy`
(`id`, `strategy_code`, `strategy_name`, `stage_config_json`, `max_stage_count`, `max_radius_km`, `flow_advice_template`,
 `auto_dispatch_enabled`, `auto_refund_enabled`, `auto_refund_retry_times`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
VALUES
(1, 'DEFAULT', '默认分钟级匹配策略',
 '[{\"stageNo\":1,\"radiusStartKm\":0.00,\"radiusEndKm\":0.50,\"durationSeconds\":60},{\"stageNo\":2,\"radiusStartKm\":0.50,\"radiusEndKm\":1.00,\"durationSeconds\":60},{\"stageNo\":3,\"radiusStartKm\":1.00,\"radiusEndKm\":2.00,\"durationSeconds\":60},{\"stageNo\":4,\"radiusStartKm\":2.00,\"radiusEndKm\":5.00,\"durationSeconds\":60},{\"stageNo\":5,\"radiusStartKm\":5.00,\"radiusEndKm\":999.00,\"durationSeconds\":60}]',
 5, 999.00, '当前附近服务商暂未接单，建议适当降低条件或增加预算，我们将继续为您匹配。',
 b'1', b'1', 3, 'ENABLE', 'admin', NOW(), 'admin', NOW(), b'0', 0);

UPDATE `lb_message_template`
SET `scene_code` = IFNULL(`scene_code`, 'SYSTEM_NOTICE'),
    `message_category` = IFNULL(`message_category`, 'SYSTEM')
WHERE 1 = 1;

UPDATE `lb_message_push_task`
SET `scene_code` = IFNULL(`scene_code`, 'SYSTEM_NOTICE'),
    `message_category` = IFNULL(`message_category`, 'SYSTEM'),
    `execute_status` = IFNULL(`execute_status`, `status`)
WHERE 1 = 1;

UPDATE `lb_message_record`
SET `scene_code` = IFNULL(`scene_code`, 'SYSTEM_NOTICE'),
    `message_category` = IFNULL(`message_category`, 'SYSTEM'),
    `channel_type` = CASE WHEN `channel_type` = 'INTERNAL_MESSAGE' THEN 'APP_POPUP' ELSE `channel_type` END,
    `read_status` = IFNULL(`read_status`, 'READ'),
    `content_snapshot` = IFNULL(`content_snapshot`, `title`)
WHERE 1 = 1;

INSERT INTO `system_menu`
(`id`, `name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
-- 平台工作台为单独一级菜单页面，不承载其它父级目录
(110000, '平台工作台', 'linbang:dashboard:query', 2, 10, 0, '/linbang-dashboard', 'ep:data-analysis', 'linbang/dashboard/index', 'LinbangDashboard', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110100, '用户中心', '', 1, 20, 0, '/linbang-member', 'ep:user', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110110, '用户中心概览', '', 2, 1, 110100, 'overview', 'ep:histogram', 'linbang/member/index', 'LinbangMemberOverview', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110120, '用户列表', 'linbang:member-user:query', 2, 10, 110100, 'member-user', 'ep:user-filled', 'linbang/memberuser/index', 'LinbangMemberUser', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110121, '用户新增', 'linbang:member-user:create', 3, 1, 110120, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110122, '用户修改', 'linbang:member-user:update', 3, 2, 110120, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110123, '用户删除', 'linbang:member-user:delete', 3, 3, 110120, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110124, '用户导出', 'linbang:member-user:export', 3, 4, 110120, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110130, '实名认证审核', 'linbang:member-user-real-name:query', 2, 20, 110100, 'member-real-name', 'ep:checked', 'linbang/memberrealname/index', 'LinbangMemberRealName', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110131, '实名认证新增', 'linbang:member-user-real-name:create', 3, 1, 110130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110132, '实名认证修改', 'linbang:member-user-real-name:update', 3, 2, 110130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110133, '实名认证删除', 'linbang:member-user-real-name:delete', 3, 3, 110130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110134, '实名认证导出', 'linbang:member-user-real-name:export', 3, 4, 110130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110135, '实名认证审核动作', 'linbang:member:real-name:audit', 3, 5, 110130, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110140, '资质认证审核', 'linbang:member:qualification:query', 2, 30, 110100, 'member-qualification', 'ep:document-checked', 'linbang/memberqualification/index', 'LinbangMemberQualification', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110141, '资质审核动作', 'linbang:member:qualification:audit', 3, 1, 110140, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110150, '地址管理', 'linbang:member-user-address:query', 2, 40, 110100, 'member-address', 'ep:location', 'linbang/memberaddress/index', 'LinbangMemberAddress', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110151, '地址新增', 'linbang:member-user-address:create', 3, 1, 110150, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110152, '地址修改', 'linbang:member-user-address:update', 3, 2, 110150, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110153, '地址删除', 'linbang:member-user-address:delete', 3, 3, 110150, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110154, '地址导出', 'linbang:member-user-address:export', 3, 4, 110150, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110160, '身份申请审核', 'linbang:member:role-apply:query', 2, 50, 110100, 'member-role-apply', 'ep:user', 'linbang/memberroleapply/index', 'LinbangMemberRoleApply', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110161, '身份申请审核动作', 'linbang:member:role-apply:audit', 3, 1, 110160, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110200, '服务商中心', '', 1, 30, 0, '/linbang-merchant', 'ep:shop', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110210, '服务商中心概览', '', 2, 1, 110200, 'overview', 'ep:office-building', 'linbang/merchant/index', 'LinbangMerchantOverview', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110220, '服务商列表', 'linbang:merchant-info:query', 2, 10, 110200, 'merchant-info', 'ep:briefcase', 'linbang/merchantinfo/index', 'LinbangMerchantInfo', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110221, '服务商新增', 'linbang:merchant-info:create', 3, 1, 110220, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110222, '服务商修改', 'linbang:merchant-info:update', 3, 2, 110220, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110223, '服务商删除', 'linbang:merchant-info:delete', 3, 3, 110220, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110224, '服务商导出', 'linbang:merchant-info:export', 3, 4, 110220, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110230, '入驻审核', 'linbang:merchant-entry:query', 2, 20, 110200, 'merchant-entry', 'ep:stamp', 'linbang/merchantentry/index', 'LinbangMerchantEntry', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110231, '入驻新增', 'linbang:merchant-entry:create', 3, 1, 110230, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110232, '入驻修改', 'linbang:merchant-entry:update', 3, 2, 110230, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110233, '入驻删除', 'linbang:merchant-entry:delete', 3, 3, 110230, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110234, '入驻导出', 'linbang:merchant-entry:export', 3, 4, 110230, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110235, '入驻审核动作', 'linbang:merchant:entry:audit', 3, 5, 110230, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110240, '服务类目管理', 'linbang:merchant-service-category:query', 2, 30, 110200, 'merchant-category', 'ep:collection-tag', 'linbang/merchantcategory/index', 'LinbangMerchantCategory', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110241, '类目新增', 'linbang:merchant-service-category:create', 3, 1, 110240, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110242, '类目修改', 'linbang:merchant-service-category:update', 3, 2, 110240, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110243, '类目删除', 'linbang:merchant-service-category:delete', 3, 3, 110240, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110244, '类目导出', 'linbang:merchant-service-category:export', 3, 4, 110240, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110250, '服务点管理', 'linbang:merchant:service-point:query', 2, 40, 110200, 'merchant-service-point', 'ep:map-location', 'linbang/merchantservicepoint/index', 'LinbangMerchantServicePoint', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110251, '服务点新增', 'linbang:merchant:service-point:create', 3, 1, 110250, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110252, '服务点修改', 'linbang:merchant:service-point:update', 3, 2, 110250, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110253, '服务点删除', 'linbang:merchant:service-point:delete', 3, 3, 110250, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110254, '服务点导出', 'linbang:merchant:service-point:export', 3, 4, 110250, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110260, '晒单悬赏审核', 'linbang:showcase:reward:query', 2, 50, 110200, 'showcase-reward', 'ep:picture-rounded', 'linbang/showcasereward/index', 'LinbangShowcaseReward', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110261, '晒单悬赏审核动作', 'linbang:showcase:reward:audit', 3, 1, 110260, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110262, '优先池管理', 'linbang:priority-pool:query', 2, 60, 110200, 'priority-pool', 'ep:trophy', 'linbang/prioritypool/index', 'LinbangPriorityPool', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110263, '优先池资格变更', 'linbang:priority-pool:update', 3, 1, 110262, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110264, '证件豁免审核', 'linbang:member:cert-exemption:query', 2, 65, 110200, 'cert-exemption', 'ep:document-remove', 'linbang/certexemption/index', 'LinbangCertExemption', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110265, '证件豁免审核动作', 'linbang:member:cert-exemption:audit', 3, 1, 110264, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110266, '子账号管理', 'linbang:merchant:sub-account:query', 2, 66, 110200, 'merchant-sub-account', 'ep:user-filled', 'linbang/merchantsubaccount/index', 'LinbangMerchantSubAccount', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110267, '子账号新增', 'linbang:merchant:sub-account:create', 3, 1, 110266, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110268, '子账号修改', 'linbang:merchant:sub-account:update', 3, 2, 110266, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110300, '订单中心', '', 1, 40, 0, '/linbang-order', 'ep:document', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110310, '订单中心概览', '', 2, 1, 110300, 'overview', 'ep:tickets', 'linbang/order/index', 'LinbangOrderOverview', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110320, '订单列表', 'linbang:order:info:query', 2, 10, 110300, 'order-info', 'ep:document-copy', 'linbang/orderinfo/index', 'LinbangOrderInfo', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110321, '订单新增', 'linbang:order:info:create', 3, 1, 110320, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110322, '订单修改', 'linbang:order:info:update', 3, 2, 110320, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110323, '订单删除', 'linbang:order:info:delete', 3, 3, 110320, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110324, '订单导出', 'linbang:order:info:export', 3, 4, 110320, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110325, '标记异常', 'linbang:order:abnormal:create', 3, 5, 110320, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110330, '拆分订单管理', 'linbang:order:unit:query', 2, 20, 110300, 'order-unit', 'ep:grid', 'linbang/orderunit/index', 'LinbangOrderUnit', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110331, '单元新增', 'linbang:order:unit:create', 3, 1, 110330, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110332, '单元修改', 'linbang:order:unit:update', 3, 2, 110330, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110333, '单元删除', 'linbang:order:unit:delete', 3, 3, 110330, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110334, '单元导出', 'linbang:order:unit:export', 3, 4, 110330, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110335, '人工解锁', 'linbang:order:unit:unlock', 3, 5, 110330, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110340, '抢单记录', 'linbang:order:accept-record:query', 2, 30, 110300, 'accept-record', 'ep:timer', 'linbang/orderacceptrecord/index', 'LinbangOrderAcceptRecord', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110341, '抢单记录新增', 'linbang:order:accept-record:create', 3, 1, 110340, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110342, '抢单记录修改', 'linbang:order:accept-record:update', 3, 2, 110340, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110343, '抢单记录删除', 'linbang:order:accept-record:delete', 3, 3, 110340, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110344, '抢单记录导出', 'linbang:order:accept-record:export', 3, 4, 110340, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110350, '匹配推送记录', 'linbang:order:match-record:query', 2, 40, 110300, 'match-record', 'ep:share', 'linbang/ordermatchrecord/index', 'LinbangOrderMatchRecord', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110351, '匹配记录新增', 'linbang:order:match-record:create', 3, 1, 110350, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110352, '匹配记录修改', 'linbang:order:match-record:update', 3, 2, 110350, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110353, '匹配记录删除', 'linbang:order:match-record:delete', 3, 3, 110350, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110354, '匹配记录导出', 'linbang:order:match-record:export', 3, 4, 110350, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110355, '推送批次监控', 'linbang:match:push-batch:query', 2, 45, 110300, 'match-push-batch', 'ep:connection', 'linbang/matchpushbatch/index', 'LinbangMatchPushBatch', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110356, '匹配策略配置', 'linbang:match:strategy:query', 2, 46, 110300, 'match-strategy', 'ep:operation', 'linbang/matchstrategy/index', 'LinbangMatchStrategy', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110357, '匹配策略保存', 'linbang:match:strategy:update', 3, 1, 110356, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110360, '异常订单处理', 'linbang:order:abnormal:query', 2, 50, 110300, 'order-abnormal', 'ep:warning', 'linbang/orderabnormal/index', 'LinbangOrderAbnormal', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110361, '异常单新增', 'linbang:order:abnormal:create', 3, 1, 110360, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110362, '异常单修改', 'linbang:order:abnormal:update', 3, 2, 110360, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110363, '异常单删除', 'linbang:order:abnormal:delete', 3, 3, 110360, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110364, '异常单导出', 'linbang:order:abnormal:export', 3, 4, 110360, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110365, '流单退款看板', 'linbang:order:flow:query', 2, 60, 110300, 'order-flow', 'ep:refresh-left', 'linbang/orderflow/index', 'LinbangOrderFlow', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110366, '流单退款重试', 'linbang:order:flow:update', 3, 1, 110365, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110400, '资金中心', '', 1, 50, 0, '/linbang-wallet', 'ep:wallet', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110420, '钱包账户', 'linbang:wallet:account:query', 2, 10, 110400, 'wallet-account', 'ep:credit-card', 'linbang/walletaccount/index', 'LinbangWalletAccount', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110421, '账户新增', 'linbang:wallet:account:create', 3, 1, 110420, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110422, '账户修改', 'linbang:wallet:account:update', 3, 2, 110420, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110423, '账户删除', 'linbang:wallet:account:delete', 3, 3, 110420, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110424, '账户导出', 'linbang:wallet:account:export', 3, 4, 110420, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110430, '资金流水', 'linbang:wallet:flow:query', 2, 20, 110400, 'wallet-flow', 'ep:list', 'linbang/walletflow/index', 'LinbangWalletFlow', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110431, '流水新增', 'linbang:wallet:flow:create', 3, 1, 110430, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110432, '流水修改', 'linbang:wallet:flow:update', 3, 2, 110430, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110433, '流水删除', 'linbang:wallet:flow:delete', 3, 3, 110430, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110434, '流水导出', 'linbang:wallet:flow:export', 3, 4, 110430, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110440, '提现审核', 'linbang:wallet:withdraw:query', 2, 30, 110400, 'wallet-withdraw', 'ep:takeaway-box', 'linbang/walletwithdraw/index', 'LinbangWalletWithdraw', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110441, '提现新增', 'linbang:wallet:withdraw:create', 3, 1, 110440, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110442, '提现修改', 'linbang:wallet:withdraw:update', 3, 2, 110440, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110443, '提现删除', 'linbang:wallet:withdraw:delete', 3, 3, 110440, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110444, '提现导出', 'linbang:wallet:withdraw:export', 3, 4, 110440, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110445, '提现审核动作', 'linbang:wallet:withdraw:audit', 3, 5, 110440, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110450, '退款审核', 'pay:refund:query', 2, 40, 110400, 'pay-refund', 'ep:refresh-left', 'pay/refund/index', 'PayRefundAudit', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110451, '退款导出', 'pay:refund:export', 3, 1, 110450, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110452, '退款审核动作', 'linbang:pay:refund:audit', 3, 2, 110450, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110460, '分账规则', 'linbang:wallet:divide-rule:query', 2, 50, 110400, 'divide-rule', 'ep:coin', 'linbang/dividerule/index', 'LinbangDivideRule', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110461, '分账规则新增', 'linbang:wallet:divide-rule:create', 3, 1, 110460, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110462, '分账规则修改', 'linbang:wallet:divide-rule:update', 3, 2, 110460, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110463, '分账规则删除', 'linbang:wallet:divide-rule:delete', 3, 3, 110460, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110464, '分账规则导出', 'linbang:wallet:divide-rule:export', 3, 4, 110460, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110470, '银行卡管理', 'linbang:wallet:bank-card:query', 2, 60, 110400, 'bank-card', 'ep:credit-card', 'linbang/walletbankcard/index', 'LinbangWalletBankCard', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110471, '银行卡新增', 'linbang:wallet:bank-card:create', 3, 1, 110470, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110472, '银行卡修改', 'linbang:wallet:bank-card:update', 3, 2, 110470, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110473, '银行卡删除', 'linbang:wallet:bank-card:delete', 3, 3, 110470, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110474, '银行卡导出', 'linbang:wallet:bank-card:export', 3, 4, 110470, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110475, '分账明细', 'linbang:wallet:divide-record:query', 2, 70, 110400, 'divide-record', 'ep:histogram', 'linbang/orderdividerecord/index', 'LinbangOrderDivideRecord', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110476, '托管凭证', 'linbang:wallet:escrow-proof:query', 2, 80, 110400, 'escrow-proof', 'ep:document-checked', 'linbang/escrowproof/index', 'LinbangEscrowProof', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110500, '风控中心', '', 1, 60, 0, '/linbang-risk', 'ep:warning', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110510, '风控中心概览', '', 2, 1, 110500, 'overview', 'ep:monitor', 'linbang/risk/index', 'LinbangRiskOverview', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110520, '风控规则', 'linbang:risk-rule:query', 2, 10, 110500, 'risk-rule', 'ep:set-up', 'linbang/riskrule/index', 'LinbangRiskRule', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110521, '风控规则新增', 'linbang:risk-rule:create', 3, 1, 110520, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110522, '风控规则修改', 'linbang:risk-rule:update', 3, 2, 110520, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110523, '风控规则删除', 'linbang:risk-rule:delete', 3, 3, 110520, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110524, '风控规则导出', 'linbang:risk-rule:export', 3, 4, 110520, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110530, '敏感词管理', 'linbang:sensitive-word:query', 2, 20, 110500, 'sensitive-word', 'ep:remove', 'linbang/sensitiveword/index', 'LinbangSensitiveWord', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110531, '敏感词新增', 'linbang:sensitive-word:create', 3, 1, 110530, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110532, '敏感词修改', 'linbang:sensitive-word:update', 3, 2, 110530, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110533, '敏感词删除', 'linbang:sensitive-word:delete', 3, 3, 110530, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110534, '敏感词导出', 'linbang:sensitive-word:export', 3, 4, 110530, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110535, '敏感命中日志', 'linbang:risk:sensitive-hit-log:query', 2, 25, 110500, 'sensitive-hit-log', 'ep:warning-filled', 'linbang/sensitivehitlog/index', 'LinbangSensitiveHitLog', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110536, '图片OCR明细', 'linbang:risk:sensitive-image-scan-result:query', 2, 26, 110500, 'sensitive-image-scan-result', 'ep:picture', 'linbang/sensitiveimagescanresult/index', 'LinbangSensitiveImageScanResult', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110537, '用户自定义脱敏词', 'linbang:risk:user-sensitive-custom-word:query', 2, 27, 110500, 'user-sensitive-custom-word', 'ep:edit-pen', 'linbang/usersensitivecustomword/index', 'LinbangUserSensitiveCustomWord', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110538, '订单拆分规则', 'linbang:order:split-rule:query', 2, 61, 110300, 'order-split-rule', 'ep:files', 'linbang/ordersplitrule/index', 'LinbangOrderSplitRule', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110539, '订单拆分规则新增', 'linbang:order:split-rule:create', 3, 1, 110538, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110540, '订单拆分规则修改', 'linbang:order:split-rule:update', 3, 2, 110538, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110541, '订单拆分规则删除', 'linbang:order:split-rule:delete', 3, 3, 110538, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110600, '信用与售后', '', 1, 70, 0, '/linbang-review', 'ep:chat-round', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110610, '信用与售后概览', '', 2, 1, 110600, 'overview', 'ep:comment', 'linbang/review/index', 'LinbangReviewOverview', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110620, '投诉管理', 'linbang:review:complaint:query', 2, 10, 110600, 'complaint', 'ep:chat-dot-round', 'linbang/complaint/index', 'LinbangComplaint', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110621, '投诉新增', 'linbang:review:complaint:create', 3, 1, 110620, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110622, '投诉修改', 'linbang:review:complaint:update', 3, 2, 110620, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110623, '投诉删除', 'linbang:review:complaint:delete', 3, 3, 110620, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110624, '投诉导出', 'linbang:review:complaint:export', 3, 4, 110620, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110625, '投诉处理', 'linbang:review:complaint:process', 3, 5, 110620, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110630, '申诉管理', 'linbang:review:appeal:query', 2, 20, 110600, 'appeal', 'ep:guide', 'linbang/appeal/index', 'LinbangAppeal', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110631, '申诉新增', 'linbang:review:appeal:create', 3, 1, 110630, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110632, '申诉修改', 'linbang:review:appeal:update', 3, 2, 110630, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110633, '申诉删除', 'linbang:review:appeal:delete', 3, 3, 110630, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110634, '申诉导出', 'linbang:review:appeal:export', 3, 4, 110630, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110635, '申诉审核', 'linbang:review:appeal:audit', 3, 5, 110630, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110640, '评价管理', 'linbang:review:comment:query', 2, 30, 110600, 'review-comment', 'ep:star', 'linbang/reviewcomment/index', 'LinbangReviewComment', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110641, '评价新增', 'linbang:review:comment:create', 3, 1, 110640, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110642, '评价修改', 'linbang:review:comment:update', 3, 2, 110640, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110643, '评价删除', 'linbang:review:comment:delete', 3, 3, 110640, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110644, '评价导出', 'linbang:review:comment:export', 3, 4, 110640, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110650, '信用分规则', 'linbang:review:credit-rule:query', 2, 40, 110600, 'credit-rule', 'ep:medal', 'linbang/creditrule/index', 'LinbangCreditRule', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110660, '信用分记录', 'linbang:review:credit-record:query', 2, 50, 110600, 'credit-record', 'ep:document-checked', 'linbang/creditrecord/index', 'LinbangCreditRecord', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110651, '信用规则新增', 'linbang:review:credit-rule:create', 3, 1, 110650, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110652, '信用规则修改', 'linbang:review:credit-rule:update', 3, 2, 110650, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110653, '信用规则删除', 'linbang:review:credit-rule:delete', 3, 3, 110650, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110654, '信用规则导出', 'linbang:review:credit-rule:export', 3, 4, 110650, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110661, '信用记录导出', 'linbang:review:credit-record:export', 3, 1, 110660, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
-- 推广中心为独立一级父级菜单，不挂在平台工作台下
(110680, '推广中心', '', 1, 75, 0, '/linbang-promote', 'ep:share', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110681, '推广中心概览', '', 2, 1, 110680, 'overview', 'ep:promotion', 'linbang/promote/index', 'LinbangPromote', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110682, '推广员管理', 'linbang:promote:user:query', 2, 10, 110680, 'promoter', 'ep:user-filled', 'linbang/promoter/index', 'LinbangPromoter', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110683, '推广员详情', 'linbang:promote:user:get', 3, 1, 110682, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110684, '佣金记录', 'linbang:promote:commission:query', 2, 20, 110680, 'commission-order', 'ep:wallet-filled', 'linbang/commissionorder/index', 'LinbangCommissionOrder', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110685, '佣金记录详情', 'linbang:promote:commission:get', 3, 1, 110684, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110700, '合作商中心', '', 1, 80, 0, '/linbang-partner', 'ep:office-building', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110720, '价格申报审核', 'linbang:partner:price-report:query', 2, 10, 110700, 'merchant-price-report', 'ep:money', 'linbang/merchantpricereport/index', 'LinbangMerchantPriceReport', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110721, '价格申报审核动作', 'linbang:partner:price-report:audit', 3, 1, 110720, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110760, '平台运营', 'linbang:help:feedback:query', 2, 90, 0, '/linbang-platform', 'ep:service', 'linbang/platform/index', 'LinbangPlatform', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110761, '帮助反馈导出', 'linbang:help:feedback:export', 3, 1, 110760, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110762, '常见问题查询', 'linbang:help:faq:query', 3, 2, 110760, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110763, '常见问题新增', 'linbang:help:faq:create', 3, 3, 110760, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110764, '常见问题修改', 'linbang:help:faq:update', 3, 4, 110760, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110765, '常见问题删除', 'linbang:help:faq:delete', 3, 5, 110760, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110766, '常见问题导出', 'linbang:help:faq:export', 3, 6, 110760, '', '', '', '', 0, b'0', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110800, '消息中心', '', 1, 90, 0, '/linbang-message', 'ep:message', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
(110830, '推送任务', 'linbang:message:push-task:query', 2, 10, 110800, 'message-push-task', 'ep:promotion', 'linbang/messagepushtask/index', 'LinbangMessagePushTask', 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 1, `id`, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM `system_menu`
WHERE `id` BETWEEN 110000 AND 110999;

INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 20001, `id`, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM `system_menu`
WHERE (`id` BETWEEN 110000 AND 110399) OR (`id` BETWEEN 110600 AND 110685) OR (`id` BETWEEN 110700 AND 110830);

INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 20002, `id`, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM `system_menu`
WHERE `id` = 110000 OR (`id` BETWEEN 110300 AND 110320) OR (`id` BETWEEN 110400 AND 110476);

INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 20003, `id`, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM `system_menu`
WHERE `id` = 110000 OR (`id` BETWEEN 110500 AND 110534) OR (`id` BETWEEN 110620 AND 110635);
INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 20003, `id`, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM `system_menu`
WHERE `id` IN (110535, 110536, 110537);

INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 20001, `id`, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM `system_menu`
WHERE `id` IN (110538, 110539, 110540, 110541);

INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 20004, `id`, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM `system_menu`
WHERE `id` IN (
  110000,
  110100, 110130, 110131, 110132, 110133, 110134, 110135,
  110140, 110141,
  110160, 110161,
  110200, 110230, 110231, 110232, 110233, 110234, 110235, 110250, 110251, 110252, 110253, 110254, 110260, 110261, 110262, 110263, 110264, 110265, 110266, 110267, 110268,
  110300, 110350, 110355, 110356, 110357, 110365, 110366,
  110700, 110720, 110721,
  110400, 110440, 110441, 110442, 110443, 110444, 110445, 110450, 110451, 110452, 110475, 110476,
  110600, 110620, 110625, 110630, 110635
  ,110680, 110681, 110682, 110683, 110684, 110685
);

INSERT INTO `system_role_menu`
(`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 20005, `id`, 'admin', NOW(), 'admin', NOW(), b'0', 1
FROM `system_menu`
WHERE `id` IN (
  110000,
  110100, 110110, 110120, 110130, 110140, 110150,
  110160,
  110200, 110210, 110220, 110230, 110240, 110250, 110260, 110262, 110264, 110266,
  110300, 110310, 110320, 110330, 110340, 110350, 110355, 110356, 110360, 110365,
  110400, 110475, 110476,
  110600, 110610, 110620, 110630,
  110680, 110681, 110682, 110684,
  110700, 110720
);

INSERT INTO `lb_help_faq`
(`id`, `category_code`, `category_name`, `title`, `content`, `icon`, `sort_no`, `status`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(360001, 'FUNDS', '资金与提现', '资金与提现常见问题', '平台资金分为可提现金额、冻结金额和托管金额。订单支付成功后进入托管金额；单元完工并确认后，托管金额按规则解锁为可提现金额。提现需完成实名认证并绑定可用银行卡，申请后按后台审核和打款结果更新状态。', 'fund', 10, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(360002, 'ORDER', '订单相关', '订单相关常见问题', '订单状态包括待付款、待接单、已接单、服务中、待确认、已完成、售后、已退款和已关闭。只有待付款、待接单阶段允许修改地址、预约说明、需求描述等信息；取消、退款、申诉会记录操作日志。', 'order', 20, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(360003, 'AUTH', '认证与入驻', '认证与入驻常见问题', '接单、提现、资质服务等关键能力需要完成实名认证、服务商入驻或对应资质认证。证件即将到期时，平台会通过站内消息提醒；资质失效可能影响接单、结算或展示。', 'verify', 30, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(360004, 'MATCH', '抢单与匹配', '抢单与匹配常见问题', '抢单与匹配会综合订单状态、服务类目、服务区域、接单状态、信用和风控限制判断。若无法接单，请先确认服务商身份已开通、接单状态为开启、资质未过期且账号不存在风控限制。', 'order_match', 40, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(360005, 'PROMOTE', '推广与佣金', '推广与佣金常见问题', '推广收益按平台分账和结算规则生成，常见状态包括待结算、已结算和已失效。订单退款、售后或风控异常可能影响佣金结算；具体金额和时间以佣金记录与钱包流水为准。', 'voice', 50, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0');

INSERT INTO `system_notify_template` (`id`, `name`, `code`, `nickname`, `content`, `type`, `params`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(390004, '提现到账通知', 'lb_withdraw_arrived', '邻里互助系统', '您的提现申请 {withdrawNo} 已到账，请前往钱包查看。', 2, '["withdrawNo"]', 0, '邻里互助业务通知模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(390005, '提现失败通知', 'lb_withdraw_failed', '邻里互助系统', '您的提现申请 {withdrawNo} 打款失败，金额已退回可提现余额。', 2, '["withdrawNo"]', 0, '邻里互助业务通知模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(390006, '托管解锁通知', 'lb_escrow_unlocked', '邻里互助系统', '您的订单 {orderNo} 已完成结算，托管资金已解锁。', 2, '["orderNo"]', 0, '邻里互助业务通知模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(390007, '接单权限开通提醒', 'lb_merchant_accept_enabled', '邻里互助系统', '您的服务商终审已通过，接单权限现已开通。', 2, '[]', 0, '邻里互助业务通知模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(390008, '银行卡绑定提醒', 'lb_bind_bank_card_required', '邻里互助系统', '您的入驻终审已通过，但尚未绑定有效银行卡，当前暂不可接单。', 2, '[]', 0, '邻里互助业务通知模板', 'admin', NOW(), 'admin', NOW(), b'0'),
(390009, '证件豁免审核结果通知', 'lb_cert_exemption_audited', '邻里互助系统', '您的证件豁免申请审核结果已更新，请及时查看。', 2, '[]', 0, '邻里互助业务通知模板', 'admin', NOW(), 'admin', NOW(), b'0')
ON DUPLICATE KEY UPDATE
`name` = VALUES(`name`),
`nickname` = VALUES(`nickname`),
`content` = VALUES(`content`),
`params` = VALUES(`params`),
`update_time` = NOW();

INSERT INTO `lb_message_template` (`id`, `template_code`, `template_name`, `scene_code`, `message_category`, `template_type`, `channel_type`, `title_template`, `content_template`, `route_type`, `route_value`, `mp_template_id`, `sms_template_code`, `voice_text_template`, `sort`, `status`, `tenant_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(380010, 'lb_withdraw_arrived', '提现到账通知', 'FINANCE_WITHDRAW_SUCCESS', 'FINANCE', 'BIZ', 'APP_POPUP', '提现到账通知', '您的提现申请已到账，请前往钱包查看。', 'APP_PAGE', '/pages/wallet/withdraw', NULL, NULL, '您的提现申请已到账，请前往钱包查看。', 180, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380011, 'lb_withdraw_failed', '提现失败通知', 'FINANCE_WITHDRAW_AUDIT', 'FINANCE', 'BIZ', 'APP_POPUP', '提现失败通知', '您的提现申请打款失败，金额已退回可提现余额。', 'APP_PAGE', '/pages/wallet/withdraw', NULL, NULL, '您的提现申请打款失败，金额已退回可提现余额。', 190, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380012, 'lb_escrow_unlocked', '托管解锁通知', 'ORDER_STATUS_CHANGED', 'ORDER', 'BIZ', 'APP_POPUP', '托管解锁通知', '您的订单已完成结算，托管资金已解锁。', 'APP_PAGE', '/pages/order/detail', NULL, NULL, '您的订单已完成结算，托管资金已解锁。', 200, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380013, 'lb_finance_payment_success', '支付成功通知', 'FINANCE_PAYMENT_SUCCESS', 'FINANCE', 'BIZ', 'APP_POPUP', '支付成功通知', '您的订单支付已成功，请等待服务商接单。', 'APP_PAGE', '/pages/order/detail', NULL, NULL, '您的订单支付已成功，请等待服务商接单。', 210, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380014, 'lb_finance_payment_success_sms', '支付成功短信通知', 'FINANCE_PAYMENT_SUCCESS', 'FINANCE', 'BIZ', 'SMS', '支付成功短信通知', '您的订单支付已成功，请等待服务商接单。', 'APP_PAGE', '/pages/order/detail', NULL, 'lb_finance_payment_success', NULL, 220, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380015, 'lb_order_status_changed', '订单状态通知', 'ORDER_STATUS_CHANGED', 'ORDER', 'BIZ', 'APP_POPUP', '订单状态通知', '您的订单状态已更新，请进入订单详情查看。', 'APP_PAGE', '/pages/order/detail', NULL, NULL, '您的订单状态已更新，请进入订单详情查看。', 230, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380016, 'lb_dispute_created', '纠纷发起通知', 'DISPUTE_CREATED', 'DISPUTE', 'BIZ', 'APP_POPUP', '纠纷发起通知', '您有一条新的纠纷消息，请及时查看处理。', 'APP_PAGE', '/pages/order/detail', NULL, NULL, '您有一条新的纠纷消息，请及时查看处理。', 240, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380017, 'lb_dispute_result', '纠纷结果通知', 'DISPUTE_RESULT', 'DISPUTE', 'BIZ', 'APP_POPUP', '纠纷结果通知', '您的纠纷处理结果已更新，请及时查看。', 'APP_PAGE', '/pages/order/detail', NULL, NULL, '您的纠纷处理结果已更新，请及时查看。', 250, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380018, 'lb_verify_success', '核销成功通知', 'VERIFY_SUCCESS', 'ORDER', 'BIZ', 'APP_POPUP', '核销成功通知', '您的订单单元已核销完成，请查看订单进度。', 'APP_PAGE', '/pages/order/detail', NULL, NULL, '您的订单单元已核销完成，请查看订单进度。', 260, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380019, 'lb_verify_exception', '核销异常通知', 'VERIFY_EXCEPTION', 'ORDER', 'BIZ', 'APP_POPUP', '核销异常通知', '您的订单单元核销出现异常，请核对订单信息后重试。', 'APP_PAGE', '/pages/order/detail', NULL, NULL, '您的订单单元核销出现异常，请核对订单信息后重试。', 270, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0'),
(380020, 'lb_commission_settled', '佣金结算通知', 'FINANCE_COMMISSION_SETTLED', 'FINANCE', 'BIZ', 'APP_POPUP', '佣金结算通知', '您的推广佣金已入账，请前往收益中心查看。', 'APP_PAGE', '/pages/promote/index', NULL, NULL, '您的推广佣金已入账，请前往收益中心查看。', 280, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0')
ON DUPLICATE KEY UPDATE
`template_name` = VALUES(`template_name`),
`content_template` = VALUES(`content_template`),
`status` = VALUES(`status`),
`update_time` = NOW();

INSERT INTO `infra_config`
(`id`, `category`, `type`, `name`, `config_key`, `value`, `visible`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
VALUES
(620101, 'linbang_platform', 2, 'App 税务提醒文案', 'linbang.app.tax-reminder', '建议尽快办理个体执照；若未办理，平台将按规则代扣 20% 个税。', b'1', '资金域 P0 默认配置', 'admin', NOW(), 'admin', NOW(), b'0'),
(620102, 'linbang_platform', 2, 'App 个体执照代办入口地址', 'linbang.app.license-agent-entry-url', 'https://example.com/license-agent', b'1', '资金域 P0 默认配置', 'admin', NOW(), 'admin', NOW(), b'0'),
(620103, 'linbang_platform', 2, 'App 个体执照代办入口标题', 'linbang.app.license-agent-entry-title', '个体执照代办', b'1', '资金域 P0 默认配置', 'admin', NOW(), 'admin', NOW(), b'0'),
(620104, 'linbang_platform', 2, 'App 提现说明', 'linbang.app.withdraw-notice', '提现申请审核通过后预计 T+1 到账，实际到账时间以银行处理为准。', b'1', '资金域 P0 默认配置', 'admin', NOW(), 'admin', NOW(), b'0'),
(620105, 'linbang_platform', 2, '高德地图 JS API Key', 'linbang.app.amap-js-key', '', b'1', 'App H5 首页真实地图使用的高德 JS API Key', 'admin', NOW(), 'admin', NOW(), b'0'),
(620106, 'linbang_platform', 2, '高德地图 JS API 安全密钥', 'linbang.app.amap-security-js-code', '', b'1', '高德 JS API securityJsCode，未启用安全密钥时可为空', 'admin', NOW(), 'admin', NOW(), b'0');

COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
