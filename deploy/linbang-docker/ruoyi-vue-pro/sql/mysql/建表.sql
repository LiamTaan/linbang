-- 邻里互助 - 基础框架精简建表
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `infra_config`;
CREATE TABLE `infra_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数分组',
  `type` tinyint NOT NULL COMMENT '参数类型',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数键名',
  `value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '参数键值',
  `visible` bit(1) NOT NULL COMMENT '是否可见',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '参数配置表';

DROP TABLE IF EXISTS `infra_file`;
CREATE TABLE `infra_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件编号',
  `config_id` bigint NULL DEFAULT NULL COMMENT '配置编号',
  `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名',
  `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件 URL',
  `type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型',
  `size` int NOT NULL COMMENT '文件大小',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2260 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件表';

DROP TABLE IF EXISTS `infra_file_config`;
CREATE TABLE `infra_file_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名',
  `storage` tinyint NOT NULL COMMENT '存储器',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `master` bit(1) NOT NULL COMMENT '是否为主配置',
  `config` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储配置',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件配置表';

DROP TABLE IF EXISTS `infra_file_content`;
CREATE TABLE `infra_file_content`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `config_id` bigint NOT NULL COMMENT '配置编号',
  `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径',
  `content` mediumblob NOT NULL COMMENT '文件内容',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_config_id_path`(`config_id` ASC, `path` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 288 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件表';

DROP TABLE IF EXISTS `infra_job`;
CREATE TABLE `infra_job`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务编号',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `status` tinyint NOT NULL COMMENT '任务状态',
  `handler_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '处理器的名字',
  `handler_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理器的参数',
  `cron_expression` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'CRON 表达式',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
  `retry_interval` int NOT NULL DEFAULT 0 COMMENT '重试间隔',
  `monitor_timeout` int NOT NULL DEFAULT 0 COMMENT '监控超时时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务表';

DROP TABLE IF EXISTS `infra_job_log`;
CREATE TABLE `infra_job_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志编号',
  `job_id` bigint NOT NULL COMMENT '任务编号',
  `handler_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '处理器的名字',
  `handler_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理器的参数',
  `execute_index` tinyint NOT NULL DEFAULT 1 COMMENT '第几次执行',
  `begin_time` datetime NOT NULL COMMENT '开始执行时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束执行时间',
  `duration` int NULL DEFAULT NULL COMMENT '执行时长',
  `status` tinyint NOT NULL COMMENT '任务状态',
  `result` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '结果数据',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_job_id`(`job_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 987 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务日志表';

DROP TABLE IF EXISTS `infra_api_error_log`;
CREATE TABLE `infra_api_error_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '链路追踪编号',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号',
  `user_type` tinyint NOT NULL DEFAULT 0 COMMENT '用户类型',
  `application_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求方法名',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求地址',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求参数',
  `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户 IP',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浏览器 UA',
  `exception_time` datetime NOT NULL COMMENT '异常发生时间',
  `exception_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '异常名',
  `exception_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常导致的消息',
  `exception_root_cause_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常导致的根消息',
  `exception_stack_trace` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常的栈轨迹',
  `exception_class_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常发生的类全名',
  `exception_file_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常发生的类文件',
  `exception_method_name` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '异常发生的方法名',
  `exception_line_number` int NOT NULL COMMENT '异常发生的方法所在行',
  `process_status` tinyint NOT NULL COMMENT '处理状态',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `process_user_id` int NULL DEFAULT 0 COMMENT '处理用户编号',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24233 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统异常日志';

DROP TABLE IF EXISTS `system_dept`;
CREATE TABLE `system_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '部门名称',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父部门id',
  `sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `leader_user_id` bigint NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NOT NULL COMMENT '部门状态（0正常 1停用）',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表';

DROP TABLE IF EXISTS `system_dict_data`;
CREATE TABLE `system_dict_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `sort` int NOT NULL DEFAULT 0 COMMENT '字典排序',
  `label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典标签',
  `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典类型',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `color_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '颜色类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'css 样式',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1061137 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典数据表';

DROP TABLE IF EXISTS `system_dict_type`;
CREATE TABLE `system_dict_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典名称',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '字典类型',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `deleted_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1061099 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型表';

DROP TABLE IF EXISTS `system_login_log`;
CREATE TABLE `system_login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `log_type` bigint NOT NULL COMMENT '日志类型',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '链路追踪编号',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户编号',
  `user_type` tinyint NOT NULL DEFAULT 0 COMMENT '用户类型',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户账号',
  `result` tinyint NOT NULL COMMENT '登陆结果',
  `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户 IP',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '浏览器 UA',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5226 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录';

DROP TABLE IF EXISTS `system_menu`;
CREATE TABLE `system_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `permission` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '权限标识',
  `type` tinyint NOT NULL COMMENT '菜单类型',
  `sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
  `component_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件名',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '菜单状态',
  `visible` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否可见',
  `keep_alive` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否缓存',
  `always_show` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否总是显示',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6735 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表';

DROP TABLE IF EXISTS `system_notify_message`;
CREATE TABLE `system_notify_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `user_type` tinyint NOT NULL COMMENT '用户类型',
  `template_id` bigint NOT NULL COMMENT '模版编号',
  `template_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
  `template_nickname` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版发送人名称',
  `template_content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版内容',
  `template_type` int NOT NULL COMMENT '模版类型',
  `template_params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版参数',
  `read_status` bit(1) NOT NULL COMMENT '是否已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id_user_type_read_status`(`user_id` ASC, `user_type` ASC, `read_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '站内信消息表';

DROP TABLE IF EXISTS `system_notify_template`;
CREATE TABLE `system_notify_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版编码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '发送人名称',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模版内容',
  `type` tinyint NOT NULL COMMENT '类型',
  `params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数数组',
  `status` tinyint NOT NULL COMMENT '状态',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '站内信模板表';

DROP TABLE IF EXISTS `system_oauth2_access_token`;
CREATE TABLE `system_oauth2_access_token`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `user_type` tinyint NOT NULL COMMENT '用户类型',
  `user_info` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户信息',
  `access_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '访问令牌',
  `refresh_token` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '刷新令牌',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
  `scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '授权范围',
  `expires_time` datetime NOT NULL COMMENT '过期时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_access_token`(`access_token` ASC) USING BTREE,
  INDEX `idx_refresh_token`(`refresh_token` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 107056 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 访问令牌';

DROP TABLE IF EXISTS `system_oauth2_approve`;
CREATE TABLE `system_oauth2_approve`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `user_type` tinyint NOT NULL COMMENT '用户类型',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
  `scope` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '授权范围',
  `approved` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否接受',
  `expires_time` datetime NOT NULL COMMENT '过期时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id_user_type_client_id`(`user_id` ASC, `user_type` ASC, `client_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 批准表';

DROP TABLE IF EXISTS `system_oauth2_client`;
CREATE TABLE `system_oauth2_client`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
  `secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端密钥',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用图标',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '应用描述',
  `status` tinyint NOT NULL COMMENT '状态',
  `access_token_validity_seconds` int NOT NULL COMMENT '访问令牌的有效期',
  `refresh_token_validity_seconds` int NOT NULL COMMENT '刷新令牌的有效期',
  `redirect_uris` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '可重定向的 URI 地址',
  `authorized_grant_types` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '授权类型',
  `scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '授权范围',
  `auto_approve_scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自动通过的授权范围',
  `authorities` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限',
  `resource_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '资源',
  `additional_information` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附加信息',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_client_id`(`client_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 客户端表';

DROP TABLE IF EXISTS `system_social_user`;
CREATE TABLE `system_social_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `type` tinyint NOT NULL COMMENT '社交平台的类型',
  `openid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '社交 openid',
  `token` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '社交 token',
  `raw_token_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始 Token 数据',
  `nickname` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像',
  `raw_user_info` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始用户数据',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '最后一次的认证 code',
  `state` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后一次的认证 state',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_type_openid` (`type`, `openid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社交用户表';

DROP TABLE IF EXISTS `system_social_user_bind`;
CREATE TABLE `system_social_user_bind`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '关联的用户编号',
  `user_type` tinyint NOT NULL COMMENT '用户类型',
  `social_user_id` bigint NOT NULL COMMENT '社交平台的用户编号',
  `social_type` tinyint NOT NULL COMMENT '社交平台的类型',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_type_user_id_social_type` (`user_type`, `user_id`, `social_type`) USING BTREE,
  KEY `idx_user_type_social_user_id` (`user_type`, `social_user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社交用户绑定表';

DROP TABLE IF EXISTS `system_oauth2_code`;
CREATE TABLE `system_oauth2_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `user_type` tinyint NOT NULL COMMENT '用户类型',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '授权码',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
  `scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '授权范围',
  `expires_time` datetime NOT NULL COMMENT '过期时间',
  `redirect_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '可重定向的 URI 地址',
  `state` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '状态',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 155 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 授权码表';

DROP TABLE IF EXISTS `system_oauth2_refresh_token`;
CREATE TABLE `system_oauth2_refresh_token`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `refresh_token` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '刷新令牌',
  `user_type` tinyint NOT NULL COMMENT '用户类型',
  `client_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端编号',
  `scopes` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '授权范围',
  `expires_time` datetime NOT NULL COMMENT '过期时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_refresh_token`(`refresh_token` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3244 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'OAuth2 刷新令牌';

DROP TABLE IF EXISTS `system_operate_log`;
CREATE TABLE `system_operate_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '链路追踪编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `user_type` tinyint NOT NULL DEFAULT 0 COMMENT '用户类型',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作模块类型',
  `sub_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作名',
  `biz_id` bigint NOT NULL COMMENT '操作数据模块编号',
  `action` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '操作内容',
  `success` bit(1) NOT NULL DEFAULT b'1' COMMENT '操作结果',
  `extra` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '拓展字段',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求方法名',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求地址',
  `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户 IP',
  `user_agent` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '浏览器 UA',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9207 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录 V2 版本';

DROP TABLE IF EXISTS `system_post`;
CREATE TABLE `system_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
  `sort` int NOT NULL COMMENT '显示顺序',
  `status` tinyint NOT NULL COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '岗位信息表';

DROP TABLE IF EXISTS `system_role`;
CREATE TABLE `system_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串',
  `sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` tinyint NOT NULL DEFAULT 1 COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `data_scope_dept_ids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '数据范围(指定部门数组)',
  `status` tinyint NOT NULL COMMENT '角色状态（0正常 1停用）',
  `type` tinyint NOT NULL COMMENT '角色类型',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 160 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表';

DROP TABLE IF EXISTS `system_role_menu`;
CREATE TABLE `system_role_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增编号',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6692 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色和菜单关联表';

DROP TABLE IF EXISTS `system_sms_channel`;
CREATE TABLE `system_sms_channel`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `signature` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信签名',
  `code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '渠道编码',
  `status` tinyint NOT NULL COMMENT '开启状态',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `api_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信 API 的账号',
  `api_secret` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信 API 的秘钥',
  `callback_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信发送回调 URL',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短信渠道';

DROP TABLE IF EXISTS `system_sms_code`;
CREATE TABLE `system_sms_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `code` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '验证码',
  `create_ip` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建 IP',
  `scene` tinyint NOT NULL COMMENT '发送场景',
  `today_index` tinyint NOT NULL COMMENT '今日发送的第几条',
  `used` tinyint NOT NULL COMMENT '是否使用',
  `used_time` datetime NULL DEFAULT NULL COMMENT '使用时间',
  `used_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '使用 IP',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_mobile`(`mobile` ASC) USING BTREE COMMENT '手机号'
) ENGINE = InnoDB AUTO_INCREMENT = 694 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '手机验证码';

DROP TABLE IF EXISTS `system_sms_log`;
CREATE TABLE `system_sms_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `channel_id` bigint NOT NULL COMMENT '短信渠道编号',
  `channel_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信渠道编码',
  `template_id` bigint NOT NULL COMMENT '模板编号',
  `template_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
  `template_type` tinyint NOT NULL COMMENT '短信类型',
  `template_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信内容',
  `template_params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信参数',
  `api_template_id` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信 API 的模板编号',
  `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户编号',
  `user_type` tinyint NULL DEFAULT NULL COMMENT '用户类型',
  `send_status` tinyint NOT NULL DEFAULT 0 COMMENT '发送状态',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `api_send_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信 API 发送结果的编码',
  `api_send_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信 API 发送失败的提示',
  `api_request_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信 API 发送返回的唯一请求 ID',
  `api_serial_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信 API 发送返回的序号',
  `receive_status` tinyint NOT NULL DEFAULT 0 COMMENT '接收状态',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '接收时间',
  `api_receive_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API 接收结果的编码',
  `api_receive_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API 接收结果的说明',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1568 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短信日志';

DROP TABLE IF EXISTS `system_sms_template`;
CREATE TABLE `system_sms_template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `type` tinyint NOT NULL COMMENT '模板类型',
  `status` tinyint NOT NULL COMMENT '开启状态',
  `code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
  `name` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板内容',
  `params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '参数数组',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `api_template_id` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信 API 的模板编号',
  `channel_id` bigint NOT NULL COMMENT '短信渠道编号',
  `channel_code` varchar(63) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '短信渠道编码',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '短信模板';

DROP TABLE IF EXISTS `system_user_post`;
CREATE TABLE `system_user_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` bigint NOT NULL DEFAULT 0 COMMENT '用户ID',
  `post_id` bigint NOT NULL DEFAULT 0 COMMENT '岗位ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 130 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户岗位表';

DROP TABLE IF EXISTS `system_user_role`;
CREATE TABLE `system_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增编号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户和角色关联表';

DROP TABLE IF EXISTS `system_users`;
CREATE TABLE `system_users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `post_ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '岗位编号数组',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` tinyint NULL DEFAULT 0 COMMENT '用户性别',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '头像地址',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_mobile`(`mobile` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 225 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表';

DROP TABLE IF EXISTS `pay_app`;
CREATE TABLE `pay_app` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_key` varchar(64) NOT NULL,
  `name` varchar(64) NOT NULL,
  `status` tinyint NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `order_notify_url` varchar(1024) NOT NULL,
  `refund_notify_url` varchar(1024) NOT NULL,
  `transfer_notify_url` varchar(1024) NOT NULL,
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_app_app_key` (`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付应用表';

DROP TABLE IF EXISTS `pay_channel`;
CREATE TABLE `pay_channel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL,
  `status` tinyint NOT NULL,
  `fee_rate` double NOT NULL DEFAULT 0,
  `remark` varchar(255) DEFAULT NULL,
  `app_id` bigint NOT NULL,
  `config` longtext NOT NULL,
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `tenant_id` bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_channel_app_code` (`app_id`,`code`),
  KEY `idx_pay_channel_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付渠道表';

DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` bigint NOT NULL,
  `channel_id` bigint DEFAULT NULL,
  `channel_code` varchar(32) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `user_type` tinyint DEFAULT NULL,
  `merchant_order_id` varchar(64) NOT NULL,
  `subject` varchar(128) NOT NULL,
  `body` varchar(255) NOT NULL,
  `notify_url` varchar(1024) NOT NULL,
  `price` int NOT NULL,
  `channel_fee_rate` double DEFAULT 0,
  `channel_fee_price` int DEFAULT 0,
  `status` tinyint NOT NULL,
  `user_ip` varchar(50) NOT NULL,
  `expire_time` datetime NOT NULL,
  `success_time` datetime DEFAULT NULL,
  `extension_id` bigint DEFAULT NULL,
  `no` varchar(64) DEFAULT NULL,
  `refund_price` int NOT NULL DEFAULT 0,
  `channel_user_id` varchar(255) DEFAULT NULL,
  `channel_order_no` varchar(64) DEFAULT NULL,
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_order_merchant_order_id` (`merchant_order_id`),
  KEY `idx_pay_order_app_id` (`app_id`),
  KEY `idx_pay_order_status` (`status`),
  KEY `idx_pay_order_no` (`no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付订单表';

DROP TABLE IF EXISTS `pay_order_extension`;
CREATE TABLE `pay_order_extension` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `no` varchar(64) NOT NULL,
  `order_id` bigint NOT NULL,
  `channel_id` bigint NOT NULL,
  `channel_code` varchar(32) NOT NULL,
  `user_ip` varchar(50) DEFAULT NULL,
  `status` tinyint NOT NULL,
  `channel_extras` longtext DEFAULT NULL,
  `channel_error_code` varchar(64) DEFAULT NULL,
  `channel_error_msg` varchar(256) DEFAULT NULL,
  `channel_notify_data` longtext DEFAULT NULL,
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_order_extension_no` (`no`),
  KEY `idx_pay_order_extension_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付订单扩展表';

DROP TABLE IF EXISTS `pay_refund`;
CREATE TABLE `pay_refund` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `no` varchar(64) NOT NULL,
  `app_id` bigint NOT NULL,
  `channel_id` bigint NOT NULL,
  `channel_code` varchar(32) NOT NULL,
  `order_id` bigint NOT NULL,
  `order_no` varchar(64) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `user_type` tinyint DEFAULT NULL,
  `merchant_order_id` varchar(64) NOT NULL,
  `merchant_refund_id` varchar(64) NOT NULL,
  `notify_url` varchar(1024) NOT NULL,
  `status` tinyint NOT NULL,
  `need_audit` bit(1) NOT NULL DEFAULT b'0',
  `audit_status` varchar(32) NOT NULL DEFAULT 'APPROVED',
  `audit_remark` varchar(255) DEFAULT NULL,
  `reject_reason` varchar(255) DEFAULT NULL,
  `audit_by` bigint DEFAULT NULL,
  `audit_time` datetime DEFAULT NULL,
  `pay_price` int NOT NULL,
  `refund_price` int NOT NULL,
  `reason` varchar(256) NOT NULL,
  `user_ip` varchar(50) DEFAULT NULL,
  `channel_order_no` varchar(64) NOT NULL,
  `channel_refund_no` varchar(64) DEFAULT NULL,
  `success_time` datetime DEFAULT NULL,
  `channel_error_code` varchar(128) DEFAULT NULL,
  `channel_error_msg` varchar(256) DEFAULT NULL,
  `channel_notify_data` longtext DEFAULT NULL,
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_refund_no` (`no`),
  UNIQUE KEY `uk_pay_refund_merchant_refund_id` (`merchant_refund_id`),
  KEY `idx_pay_refund_order_id` (`order_id`),
  KEY `idx_pay_refund_status` (`status`),
  KEY `idx_pay_refund_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付退款表';

DROP TABLE IF EXISTS `pay_notify_task`;
CREATE TABLE `pay_notify_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `app_id` bigint NOT NULL,
  `type` tinyint NOT NULL,
  `data_id` bigint NOT NULL,
  `merchant_order_id` varchar(64) DEFAULT NULL,
  `merchant_refund_id` varchar(64) DEFAULT NULL,
  `merchant_transfer_id` varchar(64) DEFAULT NULL,
  `status` tinyint NOT NULL,
  `next_notify_time` datetime DEFAULT NULL,
  `last_execute_time` datetime DEFAULT NULL,
  `notify_times` int NOT NULL DEFAULT 0,
  `max_notify_times` int NOT NULL DEFAULT 8,
  `notify_url` varchar(1024) NOT NULL,
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `tenant_id` bigint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_pay_notify_task_type_data_id` (`type`,`data_id`),
  KEY `idx_pay_notify_task_status` (`status`),
  KEY `idx_pay_notify_task_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付通知任务表';

DROP TABLE IF EXISTS `pay_notify_log`;
CREATE TABLE `pay_notify_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL,
  `notify_times` int NOT NULL,
  `response` varchar(1024) NOT NULL,
  `status` tinyint NOT NULL,
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_pay_notify_log_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付通知日志表';

DROP TABLE IF EXISTS `pay_transfer`;
CREATE TABLE `pay_transfer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `no` varchar(64) NOT NULL,
  `app_id` bigint NOT NULL,
  `channel_id` bigint NOT NULL,
  `channel_code` varchar(32) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `user_type` tinyint DEFAULT NULL,
  `merchant_transfer_id` varchar(64) NOT NULL,
  `subject` varchar(256) NOT NULL,
  `price` int NOT NULL,
  `user_account` varchar(256) NOT NULL,
  `user_name` varchar(64) DEFAULT NULL,
  `status` tinyint NOT NULL,
  `success_time` datetime DEFAULT NULL,
  `notify_url` varchar(1024) DEFAULT NULL,
  `user_ip` varchar(50) DEFAULT NULL,
  `channel_extras` longtext DEFAULT NULL,
  `channel_transfer_no` varchar(64) DEFAULT NULL,
  `channel_error_code` varchar(128) DEFAULT NULL,
  `channel_error_msg` varchar(256) DEFAULT NULL,
  `channel_notify_data` longtext DEFAULT NULL,
  `channel_package_info` longtext DEFAULT NULL,
  `creator` varchar(64) DEFAULT '',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_transfer_no` (`no`),
  UNIQUE KEY `uk_pay_transfer_merchant_transfer_id` (`merchant_transfer_id`),
  KEY `idx_pay_transfer_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付转账表';

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS`  (
                                     `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                     `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `IS_DURABLE` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                     `JOB_DATA` blob NULL,
                                     PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
                                     INDEX `IDX_QRTZ_J_REQ_RECOVERY`(`SCHED_NAME` ASC, `REQUESTS_RECOVERY` ASC) USING BTREE,
                                     INDEX `IDX_QRTZ_J_GRP`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS`  (
                                  `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `DESCRIPTION` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                  `NEXT_FIRE_TIME` bigint NULL DEFAULT NULL,
                                  `PREV_FIRE_TIME` bigint NULL DEFAULT NULL,
                                  `PRIORITY` int NULL DEFAULT NULL,
                                  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `START_TIME` bigint NOT NULL,
                                  `END_TIME` bigint NULL DEFAULT NULL,
                                  `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                  `MISFIRE_INSTR` smallint NULL DEFAULT NULL,
                                  `JOB_DATA` blob NULL,
                                  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                  INDEX `IDX_QRTZ_T_J`(`SCHED_NAME` ASC, `JOB_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_JG`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_C`(`SCHED_NAME` ASC, `CALENDAR_NAME` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_G`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_STATE`(`SCHED_NAME` ASC, `TRIGGER_STATE` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_N_STATE`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_N_G_STATE`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_NEXT_FIRE_TIME`(`SCHED_NAME` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_NFT_ST`(`SCHED_NAME` ASC, `TRIGGER_STATE` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_NFT_MISFIRE`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC, `TRIGGER_STATE` ASC) USING BTREE,
                                  INDEX `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP`(`SCHED_NAME` ASC, `MISFIRE_INSTR` ASC, `NEXT_FIRE_TIME` ASC, `TRIGGER_GROUP` ASC, `TRIGGER_STATE` ASC) USING BTREE,
                                  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS`  (
                                         `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `REPEAT_COUNT` bigint NOT NULL,
                                         `REPEAT_INTERVAL` bigint NOT NULL,
                                         `TIMES_TRIGGERED` bigint NOT NULL,
                                         PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                         CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS`  (
                                       `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `CRON_EXPRESSION` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                       PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                       CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS`  (
                                          `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                          `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                          `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                          `STR_PROP_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                          `STR_PROP_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                          `STR_PROP_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                          `INT_PROP_1` int NULL DEFAULT NULL,
                                          `INT_PROP_2` int NULL DEFAULT NULL,
                                          `LONG_PROP_1` bigint NULL DEFAULT NULL,
                                          `LONG_PROP_2` bigint NULL DEFAULT NULL,
                                          `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
                                          `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
                                          `BOOL_PROP_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                          `BOOL_PROP_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                          PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                          CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS`  (
                                       `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                       `BLOB_DATA` blob NULL,
                                       PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                       INDEX `SCHED_NAME`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
                                       CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS`  (
                                   `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                   `CALENDAR_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                   `CALENDAR` blob NOT NULL,
                                   PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS`  (
                                             `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                             `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                             PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS`  (
                                        `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `ENTRY_ID` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `TRIGGER_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `TRIGGER_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `FIRED_TIME` bigint NOT NULL,
                                        `SCHED_TIME` bigint NOT NULL,
                                        `PRIORITY` int NOT NULL,
                                        `STATE` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `JOB_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                        `JOB_GROUP` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                        `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                        `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                        PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE,
                                        INDEX `IDX_QRTZ_FT_TRIG_INST_NAME`(`SCHED_NAME` ASC, `INSTANCE_NAME` ASC) USING BTREE,
                                        INDEX `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY`(`SCHED_NAME` ASC, `INSTANCE_NAME` ASC, `REQUESTS_RECOVERY` ASC) USING BTREE,
                                        INDEX `IDX_QRTZ_FT_J_G`(`SCHED_NAME` ASC, `JOB_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
                                        INDEX `IDX_QRTZ_FT_JG`(`SCHED_NAME` ASC, `JOB_GROUP` ASC) USING BTREE,
                                        INDEX `IDX_QRTZ_FT_T_G`(`SCHED_NAME` ASC, `TRIGGER_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE,
                                        INDEX `IDX_QRTZ_FT_TG`(`SCHED_NAME` ASC, `TRIGGER_GROUP` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE`  (
                                         `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `INSTANCE_NAME` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                                         `LAST_CHECKIN_TIME` bigint NOT NULL,
                                         `CHECKIN_INTERVAL` bigint NOT NULL,
                                         PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS`  (
                               `SCHED_NAME` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                               `LOCK_NAME` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                               PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- 邻里互助 - 业务建表
SET NAMES utf8mb4;

DROP TABLE IF EXISTS `lb_user`;
CREATE TABLE IF NOT EXISTS `lb_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_no` VARCHAR(32) NOT NULL COMMENT '用户编号',
  `username` VARCHAR(64) DEFAULT NULL COMMENT '账号用户名',
  `password` VARCHAR(255) DEFAULT NULL COMMENT '账号密码摘要',
  `account_type` VARCHAR(32) NOT NULL DEFAULT 'PERSONAL' COMMENT '账户类型：PERSONAL 个人、ENTERPRISE 企业、SUB_ACCOUNT 子账号',
  `mobile` VARCHAR(20) NOT NULL COMMENT '手机号',
  `nickname` VARCHAR(64) NOT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `gender` TINYINT DEFAULT 0 COMMENT '性别',
  `birthday` DATE DEFAULT NULL COMMENT '生日',
  `register_source` VARCHAR(32) DEFAULT NULL COMMENT '注册来源',
  `register_agreement_version` VARCHAR(64) DEFAULT NULL COMMENT '注册协议版本',
  `register_agreement_confirmed_time` DATETIME DEFAULT NULL COMMENT '注册协议确认时间',
  `register_source_detail` VARCHAR(64) DEFAULT NULL COMMENT '注册来源细分',
  `current_role_code` VARCHAR(32) NOT NULL DEFAULT 'USER' COMMENT '当前角色编码',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(64) DEFAULT NULL COMMENT '最后登录IP',
  `point_balance` INT NOT NULL DEFAULT 0 COMMENT '当前积分余额',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_user_no` (`user_no`),
  UNIQUE KEY `uk_lb_user_mobile` (`mobile`),
  KEY `idx_lb_user_tenant_id` (`tenant_id`),
  KEY `idx_lb_user_status` (`status`),
  KEY `idx_lb_user_role` (`current_role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户主表';

DROP TABLE IF EXISTS `lb_member_point_record`;
CREATE TABLE IF NOT EXISTS `lb_member_point_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '邻里用户 ID，关联 lb_user.id',
  `biz_type` VARCHAR(32) DEFAULT NULL COMMENT '业务类型',
  `biz_id` VARCHAR(64) DEFAULT NULL COMMENT '业务编号',
  `title` VARCHAR(100) NOT NULL COMMENT '积分标题',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '积分描述',
  `point` INT NOT NULL COMMENT '本次变动积分，正数增加，负数扣减',
  `total_point` INT NOT NULL DEFAULT 0 COMMENT '变动后总积分',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_member_point_record_user_id` (`user_id`),
  KEY `idx_lb_member_point_record_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邻里用户积分记录表';

DROP TABLE IF EXISTS `lb_user_reminder`;
CREATE TABLE IF NOT EXISTS `lb_user_reminder` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '邻里用户 ID',
  `reminder_type` VARCHAR(32) NOT NULL COMMENT '提醒类型',
  `title` VARCHAR(100) NOT NULL COMMENT '提醒标题',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '提醒内容',
  `event_time` DATETIME NOT NULL COMMENT '事件时间',
  `next_remind_time` DATETIME DEFAULT NULL COMMENT '下一次提醒时间',
  `last_trigger_time` DATETIME DEFAULT NULL COMMENT '最近一次触发时间',
  `repeat_type` VARCHAR(32) NOT NULL DEFAULT 'NONE' COMMENT '重复规则',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '提醒状态',
  `route_type` VARCHAR(32) DEFAULT NULL COMMENT '提醒路由类型',
  `route_value` VARCHAR(255) DEFAULT NULL COMMENT '提醒路由值',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_reminder_user_id` (`user_id`),
  KEY `idx_lb_user_reminder_next_remind_time` (`next_remind_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户提醒表';

DROP TABLE IF EXISTS `lb_user_real_name`;
CREATE TABLE IF NOT EXISTS `lb_user_real_name` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `real_name` VARCHAR(64) NOT NULL COMMENT '真实姓名',
  `id_card_no` VARCHAR(32) NOT NULL COMMENT '身份证号',
  `id_card_front_file_id` BIGINT DEFAULT NULL COMMENT '身份证正面文件ID',
  `id_card_back_file_id` BIGINT DEFAULT NULL COMMENT '身份证反面文件ID',
  `hold_card_file_id` BIGINT DEFAULT NULL COMMENT '手持证件文件ID',
  `hold_card_video_file_id` BIGINT DEFAULT NULL COMMENT '手持身份证视频文件ID',
  `id_card_valid_from` DATE DEFAULT NULL COMMENT '身份证有效期开始',
  `id_card_valid_end` DATE DEFAULT NULL COMMENT '身份证有效期结束',
  `liveness_result` VARCHAR(32) DEFAULT NULL COMMENT '活体结果',
  `face_verify_result` VARCHAR(32) DEFAULT NULL COMMENT '人脸核验结果',
  `wechat_real_name_status` VARCHAR(32) NOT NULL DEFAULT 'UNBOUND' COMMENT '微信实名关联状态',
  `alipay_real_name_status` VARCHAR(32) NOT NULL DEFAULT 'UNBOUND' COMMENT '支付宝实名关联状态',
  `verify_provider` VARCHAR(64) DEFAULT NULL COMMENT '核验供应商',
  `verify_flow_no` VARCHAR(64) DEFAULT NULL COMMENT '核验流水号',
  `verify_started_time` DATETIME DEFAULT NULL COMMENT '核验发起时间',
  `verify_completed_time` DATETIME DEFAULT NULL COMMENT '核验完成时间',
  `verify_fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '核验失败原因',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_user_real_name_user_id` (`user_id`),
  KEY `idx_lb_user_real_name_tenant_id` (`tenant_id`),
  KEY `idx_lb_user_real_name_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实名认证表';

DROP TABLE IF EXISTS `lb_user_qualification`;
CREATE TABLE IF NOT EXISTS `lb_user_qualification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `qualification_type` VARCHAR(32) NOT NULL COMMENT '资质类型',
  `qualification_name` VARCHAR(128) NOT NULL COMMENT '资质名称',
  `qualification_no` VARCHAR(64) DEFAULT NULL COMMENT '资质编号',
  `file_id` BIGINT DEFAULT NULL COMMENT '文件ID',
  `evidence_file_ids_json` VARCHAR(2000) DEFAULT NULL COMMENT '多图凭证JSON',
  `video_file_id` BIGINT DEFAULT NULL COMMENT '视频凭证文件ID',
  `valid_start_date` DATE DEFAULT NULL COMMENT '有效开始日期',
  `valid_end_date` DATE DEFAULT NULL COMMENT '有效结束日期',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `priority_enabled` BIT(1) NOT NULL DEFAULT b'0' COMMENT '优先权益是否生效',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_qualification_user_id` (`user_id`),
  KEY `idx_lb_user_qualification_type` (`qualification_type`),
  KEY `idx_lb_user_qualification_audit_status` (`audit_status`),
  KEY `idx_lb_user_qualification_valid_end_date` (`valid_end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户资质表';

DROP TABLE IF EXISTS `lb_user_address`;
CREATE TABLE IF NOT EXISTS `lb_user_address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `receiver_name` VARCHAR(64) NOT NULL COMMENT '联系人',
  `receiver_mobile` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `province` VARCHAR(64) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(64) DEFAULT NULL COMMENT '市',
  `district` VARCHAR(64) DEFAULT NULL COMMENT '区',
  `street` VARCHAR(64) DEFAULT NULL COMMENT '街道',
  `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `longitude` DECIMAL(12,6) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(12,6) DEFAULT NULL COMMENT '纬度',
  `adcode` VARCHAR(16) DEFAULT NULL COMMENT '区域编码',
  `is_default` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否默认',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_address_user_id` (`user_id`),
  KEY `idx_lb_user_address_tenant_id` (`tenant_id`),
  KEY `idx_lb_user_address_adcode` (`adcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

DROP TABLE IF EXISTS `lb_merchant_info`;
CREATE TABLE IF NOT EXISTS `lb_merchant_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `merchant_name` VARCHAR(128) NOT NULL COMMENT '服务商名称',
  `contact_name` VARCHAR(64) NOT NULL COMMENT '联系人',
  `contact_mobile` VARCHAR(20) NOT NULL COMMENT '联系人手机号',
  `service_scope_desc` VARCHAR(255) DEFAULT NULL COMMENT '服务范围说明',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `accept_status` VARCHAR(32) NOT NULL DEFAULT 'DISABLE' COMMENT '接单状态',
  `credit_score` INT NOT NULL DEFAULT 100 COMMENT '信用分',
  `credit_level` VARCHAR(32) DEFAULT 'NORMAL' COMMENT '信用等级',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_merchant_info_user_id` (`user_id`),
  KEY `idx_lb_merchant_info_tenant_id` (`tenant_id`),
  KEY `idx_lb_merchant_info_status` (`status`),
  KEY `idx_lb_merchant_info_accept_status` (`accept_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务商信息表';

DROP TABLE IF EXISTS `lb_merchant_entry`;
CREATE TABLE IF NOT EXISTS `lb_merchant_entry` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` BIGINT DEFAULT NULL COMMENT '服务商ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `entry_no` VARCHAR(32) NOT NULL COMMENT '入驻单号',
  `region_code` VARCHAR(16) DEFAULT NULL COMMENT '区域编码',
  `first_audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '初审状态',
  `first_audit_by` BIGINT DEFAULT NULL COMMENT '初审人',
  `first_audit_time` DATETIME DEFAULT NULL COMMENT '初审时间',
  `final_audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '终审状态',
  `final_audit_by` BIGINT DEFAULT NULL COMMENT '终审人',
  `final_audit_time` DATETIME DEFAULT NULL COMMENT '终审时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `progress_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING_FIRST_AUDIT' COMMENT '入驻进度状态',
  `current_stage_name` VARCHAR(64) DEFAULT NULL COMMENT '当前阶段名称',
  `current_stage_time` DATETIME DEFAULT NULL COMMENT '当前阶段时间',
  `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '驳回原因',
  `onboarding_blocked_reason` VARCHAR(255) DEFAULT NULL COMMENT '当前阻塞原因',
  `accept_enabled` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否已开通接单',
  `bank_card_required` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否必须先绑卡才可接单',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_merchant_entry_no` (`entry_no`),
  KEY `idx_lb_merchant_entry_tenant_id` (`tenant_id`),
  KEY `idx_lb_merchant_entry_user_id` (`user_id`),
  KEY `idx_lb_merchant_entry_status` (`status`),
  KEY `idx_lb_merchant_entry_region_code` (`region_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务商入驻申请表';

DROP TABLE IF EXISTS `lb_service_category`;
CREATE TABLE IF NOT EXISTS `lb_service_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父级ID',
  `category_name` VARCHAR(64) NOT NULL COMMENT '类目名称',
  `category_level` TINYINT NOT NULL COMMENT '层级',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
  `default_pricing_mode` VARCHAR(32) DEFAULT NULL COMMENT '默认计价方式',
  `supported_pricing_modes` JSON DEFAULT NULL COMMENT '支持计价方式 JSON 数组',
  `support_split` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否支持拆单',
  `support_invoice` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否支持开票',
  `risk_level` VARCHAR(32) DEFAULT 'LOW' COMMENT '风险等级',
  `labor_category_flag` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否用工类',
  `force_agreement_type` VARCHAR(32) NOT NULL DEFAULT 'TRADE_GUARANTEE' COMMENT '强制协议类型',
  `invoice_rate_reminder_text` VARCHAR(255) DEFAULT NULL COMMENT '开票影响接单率提醒文案',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_service_category_parent_id` (`parent_id`),
  KEY `idx_lb_service_category_tenant_id` (`tenant_id`),
  KEY `idx_lb_service_category_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务类目表';

DROP TABLE IF EXISTS `lb_merchant_category_rel`;
CREATE TABLE IF NOT EXISTS `lb_merchant_category_rel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `category_id` BIGINT NOT NULL COMMENT '类目ID',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_merchant_category_rel` (`merchant_id`,`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务商类目关联表';

DROP TABLE IF EXISTS `lb_merchant_service_point`;
CREATE TABLE IF NOT EXISTS `lb_merchant_service_point` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `point_name` VARCHAR(128) NOT NULL COMMENT '服务点名称',
  `province` VARCHAR(64) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(64) DEFAULT NULL COMMENT '市',
  `district` VARCHAR(64) DEFAULT NULL COMMENT '区',
  `street` VARCHAR(64) DEFAULT NULL COMMENT '街道',
  `detail_address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
  `longitude` DECIMAL(12,6) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(12,6) DEFAULT NULL COMMENT '纬度',
  `service_radius_km` DECIMAL(8,2) DEFAULT NULL COMMENT '服务半径公里',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_merchant_service_point_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务点表';

DROP TABLE IF EXISTS `lb_order_split_rule`;
CREATE TABLE IF NOT EXISTS `lb_order_split_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_name` VARCHAR(64) NOT NULL COMMENT '规则名称',
  `rule_code` VARCHAR(64) NOT NULL COMMENT '规则编码',
  `match_mode` VARCHAR(16) NOT NULL DEFAULT 'ANY' COMMENT '命中模式：ANY 任一条件命中、ALL 全部条件命中',
  `category_id` BIGINT DEFAULT NULL COMMENT '命中类目ID，命中当前及子类目',
  `applicable_pricing_modes` JSON DEFAULT NULL COMMENT '适用计价方式 JSON 数组，为空表示全部适用',
  `min_order_amount` DECIMAL(18,2) DEFAULT NULL COMMENT '触发最小订单金额',
  `min_quantity` DECIMAL(18,2) DEFAULT NULL COMMENT '触发最小数量',
  `min_worker_count` INT DEFAULT NULL COMMENT '触发最小服务人数',
  `split_mode` VARCHAR(32) NOT NULL DEFAULT 'DIRECT' COMMENT '拆分方式：DIRECT/BY_PROGRESS/BY_PROCESS/BY_CONTENT/BY_PERSON',
  `default_unit_count` INT NOT NULL DEFAULT 2 COMMENT '默认拆分单元数',
  `unit_amount_limit` DECIMAL(18,2) NOT NULL DEFAULT 200.00 COMMENT '单元金额上限',
  `unit_template` JSON DEFAULT NULL COMMENT '拆分单元模板 JSON',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号，越小优先级越高',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态：ENABLE/DISABLE',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_order_split_rule_rule_code` (`rule_code`),
  KEY `idx_lb_order_split_rule_category_id` (`category_id`),
  KEY `idx_lb_order_split_rule_status_sort_no` (`status`, `sort_no`),
  KEY `idx_lb_order_split_rule_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单拆单规则表';

DROP TABLE IF EXISTS `lb_order_info`;
CREATE TABLE IF NOT EXISTS `lb_order_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '下单用户ID',
  `merchant_id` BIGINT DEFAULT NULL COMMENT '服务商ID',
  `category_id` BIGINT NOT NULL COMMENT '类目ID',
  `pricing_mode` VARCHAR(32) NOT NULL COMMENT '计价方式',
  `budget_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '预算金额',
  `order_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '订单金额',
  `service_duration_desc` VARCHAR(64) DEFAULT NULL COMMENT '服务时长说明',
  `quantity` DECIMAL(18,2) DEFAULT NULL COMMENT '数量',
  `worker_count` INT NOT NULL DEFAULT 1 COMMENT '服务人数',
  `require_desc` TEXT COMMENT '需求描述',
  `province` VARCHAR(64) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(64) DEFAULT NULL COMMENT '市',
  `district` VARCHAR(64) DEFAULT NULL COMMENT '区',
  `street` VARCHAR(64) DEFAULT NULL COMMENT '街道',
  `detail_address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
  `longitude` DECIMAL(12,6) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(12,6) DEFAULT NULL COMMENT '纬度',
  `need_invoice` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否开票',
  `need_split` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否拆单',
  `split_rule_id` BIGINT DEFAULT NULL COMMENT '命中拆单规则ID',
  `split_status` VARCHAR(32) DEFAULT 'UNSPLIT' COMMENT '拆单状态',
  `split_mode` VARCHAR(32) NOT NULL DEFAULT 'DIRECT' COMMENT '拆分方式',
  `split_rule_snapshot` LONGTEXT DEFAULT NULL COMMENT '拆单规则快照 JSON',
  `agreement_confirmed` BIT(1) NOT NULL DEFAULT b'0' COMMENT '协议是否确认',
  `trade_agreement_version` VARCHAR(64) DEFAULT NULL COMMENT '交易保障协议版本',
  `trade_agreement_confirmed_time` DATETIME DEFAULT NULL COMMENT '交易保障协议确认时间',
  `anti_escape_confirmed` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否确认防逃单提醒',
  `pay_order_id` BIGINT DEFAULT NULL COMMENT '支付订单ID',
  `deposit_required` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否需要保证金',
  `deposit_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '保证金金额',
  `deposit_pay_status` VARCHAR(32) DEFAULT 'NOT_REQUIRED' COMMENT '保证金支付状态',
  `deposit_pay_order_id` BIGINT DEFAULT NULL COMMENT '保证金支付订单ID',
  `deposit_paid_time` DATETIME DEFAULT NULL COMMENT '保证金支付时间',
  `mall_consume_record_id` BIGINT DEFAULT NULL COMMENT '关联商城消费记录ID',
  `mall_consume_record_no` VARCHAR(64) DEFAULT NULL COMMENT '关联商城消费记录编号',
  `mall_consume_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '关联商城消费金额',
  `mall_consume_status` VARCHAR(32) DEFAULT NULL COMMENT '关联商城消费状态',
  `promote_deduct_amount` DECIMAL(18,2) DEFAULT 0.00 COMMENT '推广抵扣金额',
  `promote_deduct_source_type` VARCHAR(32) DEFAULT NULL COMMENT '推广抵扣来源类型',
  `promote_deduct_source_id` BIGINT DEFAULT NULL COMMENT '推广抵扣来源对象ID',
  `promote_deduct_source_no` VARCHAR(64) DEFAULT NULL COMMENT '推广抵扣来源对象编号',
  `payable_amount_after_deduct` DECIMAL(18,2) DEFAULT 0.00 COMMENT '抵扣后订单应付金额',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING_PAY' COMMENT '订单状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_order_info_order_no` (`order_no`),
  KEY `idx_lb_order_info_user_id` (`user_id`),
  KEY `idx_lb_order_info_merchant_id` (`merchant_id`),
  KEY `idx_lb_order_info_status` (`status`),
  KEY `idx_lb_order_info_category_id` (`category_id`),
  KEY `idx_lb_order_info_tenant_id` (`tenant_id`),
  KEY `idx_lb_order_info_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

DROP TABLE IF EXISTS `lb_order_price_item`;
CREATE TABLE IF NOT EXISTS `lb_order_price_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `item_type` VARCHAR(32) NOT NULL COMMENT '明细类型',
  `item_name` VARCHAR(64) NOT NULL COMMENT '明细名称',
  `item_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '金额',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_order_price_item_order_id` (`order_id`),
  KEY `idx_lb_order_price_item_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单价格明细表';

DROP TABLE IF EXISTS `lb_order_attachment`;
CREATE TABLE IF NOT EXISTS `lb_order_attachment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `file_id` BIGINT NOT NULL COMMENT '文件ID',
  `file_type` VARCHAR(32) DEFAULT NULL COMMENT '文件类型',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_order_attachment_order_id` (`order_id`),
  KEY `idx_lb_order_attachment_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单附件表';

DROP TABLE IF EXISTS `lb_order_accept_record`;
CREATE TABLE IF NOT EXISTS `lb_order_accept_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单元ID',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `accept_time` DATETIME NOT NULL COMMENT '接单时间',
  `distance_km` DECIMAL(8,2) DEFAULT NULL COMMENT '距离公里',
  `accept_result` VARCHAR(32) NOT NULL COMMENT '接单结果',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_order_accept_record_order_id` (`order_id`),
  KEY `idx_lb_order_accept_record_unit_id` (`unit_id`),
  KEY `idx_lb_order_accept_record_merchant_id` (`merchant_id`),
  KEY `idx_lb_order_accept_record_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抢单记录表';

DROP TABLE IF EXISTS `lb_order_match_record`;
CREATE TABLE IF NOT EXISTS `lb_order_match_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单元ID',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `match_rule_code` VARCHAR(64) DEFAULT NULL COMMENT '命中规则编码',
  `match_score` DECIMAL(10,2) DEFAULT NULL COMMENT '匹配分值',
  `distance_km` DECIMAL(8,2) DEFAULT NULL COMMENT '距离公里',
  `push_time` DATETIME NOT NULL COMMENT '推送时间',
  `stage_no` INT DEFAULT NULL COMMENT '推送阶段号',
  `push_batch_no` INT DEFAULT NULL COMMENT '推送批次号',
  `priority_layer` VARCHAR(64) DEFAULT NULL COMMENT '优先层',
  `priority_pool_flag` BIT(1) DEFAULT b'0' COMMENT '是否优先池',
  `category_match_level` VARCHAR(32) DEFAULT NULL COMMENT '品类命中等级',
  `accept_deadline_time` DATETIME DEFAULT NULL COMMENT '接单截止时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PUSHED' COMMENT '状态',
  `expired_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `final_result` VARCHAR(32) DEFAULT NULL COMMENT '最终结果',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_order_match_record_unit_merchant_stage` (`unit_id`, `merchant_id`, `stage_no`),
  KEY `idx_lb_order_match_record_order_id` (`order_id`),
  KEY `idx_lb_order_match_record_unit_id` (`unit_id`),
  KEY `idx_lb_order_match_record_merchant_id` (`merchant_id`),
  KEY `idx_lb_order_match_record_stage_no` (`stage_no`),
  KEY `idx_lb_order_match_record_push_batch_no` (`push_batch_no`),
  KEY `idx_lb_order_match_record_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单匹配记录表';

DROP TABLE IF EXISTS `lb_order_unit`;
CREATE TABLE IF NOT EXISTS `lb_order_unit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '主订单ID',
  `unit_no` VARCHAR(32) NOT NULL COMMENT '单元订单号',
  `unit_seq` INT NOT NULL COMMENT '单元序号',
  `unit_title` VARCHAR(255) DEFAULT NULL COMMENT '单元标题',
  `unit_type` VARCHAR(32) NOT NULL DEFAULT 'DIRECT' COMMENT '单元类型：DIRECT 直单、AUTO_SPLIT 自动拆分',
  `unit_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '单元金额',
  `split_mode` VARCHAR(32) DEFAULT NULL COMMENT '拆分模式',
  `unit_content` VARCHAR(255) DEFAULT NULL COMMENT '单元内容摘要',
  `unit_progress` VARCHAR(64) DEFAULT NULL COMMENT '单元进度描述，例如 1/3',
  `worker_count` INT NOT NULL DEFAULT 1 COMMENT '单元服务人数',
  `max_amount_limit` DECIMAL(18,2) DEFAULT NULL COMMENT '单元金额上限快照',
  `prev_unit_id` BIGINT DEFAULT NULL COMMENT '前置单元ID',
  `is_locked` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否锁定',
  `lock_reason` VARCHAR(255) DEFAULT NULL COMMENT '锁定原因',
  `merchant_id` BIGINT DEFAULT NULL COMMENT '服务商ID',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING_CREATE' COMMENT '单元状态',
  `accept_deadline_time` DATETIME DEFAULT NULL COMMENT '接单截止时间',
  `dispatch_status` VARCHAR(32) NOT NULL DEFAULT 'WAITING' COMMENT '派单状态',
  `current_batch_no` INT DEFAULT NULL COMMENT '当前推送批次号',
  `flow_time` DATETIME DEFAULT NULL COMMENT '流单时间',
  `flow_reason` VARCHAR(255) DEFAULT NULL COMMENT '流单原因',
  `auto_refund_status` VARCHAR(32) NOT NULL DEFAULT 'NONE' COMMENT '自动退款状态',
  `auto_refund_id` BIGINT DEFAULT NULL COMMENT '自动退款单ID',
  `finish_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `appeal_expire_time` DATETIME DEFAULT NULL COMMENT '申诉截止时间',
  `verify_code` VARCHAR(32) DEFAULT NULL COMMENT '核销码',
  `verify_status` VARCHAR(32) DEFAULT NULL COMMENT '核销状态',
  `verify_time` DATETIME DEFAULT NULL COMMENT '核销时间',
  `verify_by` BIGINT DEFAULT NULL COMMENT '核销人用户 ID',
  `verify_remark` VARCHAR(255) DEFAULT NULL COMMENT '核销备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_order_unit_unit_no` (`unit_no`),
  KEY `idx_lb_order_unit_order_id` (`order_id`),
  KEY `idx_lb_order_unit_status` (`status`),
  KEY `idx_lb_order_unit_merchant_id` (`merchant_id`),
  KEY `idx_lb_order_unit_prev_unit_id` (`prev_unit_id`),
  KEY `idx_lb_order_unit_dispatch_status` (`dispatch_status`),
  KEY `idx_lb_order_unit_flow_time` (`flow_time`),
  KEY `idx_lb_order_unit_tenant_id` (`tenant_id`),
  CONSTRAINT `chk_lb_order_unit_amount` CHECK (`unit_amount` <= 200.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拆分单元表';

DROP TABLE IF EXISTS `lb_match_strategy`;
CREATE TABLE IF NOT EXISTS `lb_match_strategy` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `strategy_code` VARCHAR(64) NOT NULL COMMENT '策略编码',
  `strategy_name` VARCHAR(100) NOT NULL COMMENT '策略名称',
  `stage_config_json` TEXT NOT NULL COMMENT '阶段配置JSON',
  `max_stage_count` INT NOT NULL DEFAULT 5 COMMENT '最大阶段数',
  `max_radius_km` DECIMAL(10,2) NOT NULL DEFAULT 5.00 COMMENT '最大半径公里',
  `flow_advice_template` VARCHAR(500) DEFAULT NULL COMMENT '流单建议模板',
  `auto_refund_enabled` BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否自动退款',
  `auto_refund_retry_times` INT NOT NULL DEFAULT 3 COMMENT '自动退款重试次数',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_match_strategy_code` (`strategy_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='匹配策略表';

DROP TABLE IF EXISTS `lb_match_push_batch`;
CREATE TABLE IF NOT EXISTS `lb_match_push_batch` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `unit_id` BIGINT NOT NULL COMMENT '单元ID',
  `stage_no` INT NOT NULL COMMENT '阶段号',
  `push_batch_no` INT NOT NULL COMMENT '批次号',
  `radius_start_km` DECIMAL(10,2) DEFAULT NULL COMMENT '起始半径',
  `radius_end_km` DECIMAL(10,2) DEFAULT NULL COMMENT '结束半径',
  `planned_at` DATETIME NOT NULL COMMENT '计划推送时间',
  `expired_at` DATETIME NOT NULL COMMENT '阶段过期时间',
  `status` VARCHAR(32) NOT NULL COMMENT '状态',
  `trigger_type` VARCHAR(32) NOT NULL COMMENT '触发类型',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_match_push_batch_unit_stage` (`unit_id`, `stage_no`),
  KEY `idx_lb_match_push_batch_status` (`status`),
  KEY `idx_lb_match_push_batch_expired_at` (`expired_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='派单推送批次表';

DROP TABLE IF EXISTS `lb_priority_pool_record`;
CREATE TABLE IF NOT EXISTS `lb_priority_pool_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `status` VARCHAR(32) NOT NULL COMMENT '状态',
  `reason_code` VARCHAR(64) DEFAULT NULL COMMENT '原因编码',
  `reason_remark` VARCHAR(255) DEFAULT NULL COMMENT '原因说明',
  `current_flag` BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否当前生效',
  `effective_time` DATETIME DEFAULT NULL COMMENT '生效时间',
  `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_priority_pool_record_merchant_id` (`merchant_id`),
  KEY `idx_lb_priority_pool_record_current_flag` (`current_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优先池记录表';

DROP TABLE IF EXISTS `lb_showcase_reward`;
CREATE TABLE IF NOT EXISTS `lb_showcase_reward` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `description` VARCHAR(1000) DEFAULT NULL COMMENT '说明',
  `evidence_file_ids_json` VARCHAR(2000) DEFAULT NULL COMMENT '凭证文件JSON',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `effective_start_time` DATETIME DEFAULT NULL COMMENT '生效开始时间',
  `effective_end_time` DATETIME DEFAULT NULL COMMENT '生效结束时间',
  `priority_enabled` BIT(1) NOT NULL DEFAULT b'0' COMMENT '优先是否生效',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_showcase_reward_merchant_id` (`merchant_id`),
  KEY `idx_lb_showcase_reward_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='晒单悬赏优先表';

DROP TABLE IF EXISTS `lb_reward_order_participation`;
CREATE TABLE IF NOT EXISTS `lb_reward_order_participation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `reward_order_id` BIGINT NOT NULL COMMENT '悬赏申请 ID，关联 lb_showcase_reward.id',
  `reward_user_id` BIGINT NOT NULL COMMENT '悬赏发布用户 ID',
  `participant_user_id` BIGINT NOT NULL COMMENT '参与用户 ID',
  `participant_mobile` VARCHAR(32) DEFAULT NULL COMMENT '参与人手机号快照',
  `participant_nickname` VARCHAR(64) DEFAULT NULL COMMENT '参与人昵称快照',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '参与状态 PENDING/ACCEPTED/REJECTED/CANCELLED',
  `participate_remark` VARCHAR(255) DEFAULT NULL COMMENT '参与说明',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_reward_order_participation_reward_order_id` (`reward_order_id`),
  KEY `idx_lb_reward_order_participation_participant_user_id` (`participant_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='悬赏参与记录表';

DROP TABLE IF EXISTS `lb_merchant_dispatch_setting`;
CREATE TABLE IF NOT EXISTS `lb_merchant_dispatch_setting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `dispatch_enabled` BIT(1) NOT NULL DEFAULT b'1' COMMENT '是否参与派单',
  `max_accept_radius_km` DECIMAL(10,2) NOT NULL DEFAULT 5.00 COMMENT '最大接单半径',
  `voice_remind_enabled` BIT(1) NOT NULL DEFAULT b'1' COMMENT '语音提醒',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_merchant_dispatch_setting_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务商派单设置表';

DROP TABLE IF EXISTS `lb_order_unit_proof`;
CREATE TABLE IF NOT EXISTS `lb_order_unit_proof` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `unit_id` BIGINT NOT NULL COMMENT '单元ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `file_id` BIGINT NOT NULL COMMENT '文件ID',
  `file_url` VARCHAR(512) DEFAULT NULL COMMENT '文件访问地址',
  `file_hash` VARCHAR(128) DEFAULT NULL COMMENT '文件哈希',
  `proof_type` VARCHAR(32) DEFAULT NULL COMMENT '凭证类型',
  `proof_desc` VARCHAR(255) DEFAULT NULL COMMENT '凭证描述',
  `proof_time` DATETIME NOT NULL COMMENT '凭证时间',
  `device_time` DATETIME DEFAULT NULL COMMENT '设备拍摄时间',
  `longitude` DECIMAL(12,6) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(12,6) DEFAULT NULL COMMENT '纬度',
  `address_text` VARCHAR(255) DEFAULT NULL COMMENT '取证地址文本',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_order_unit_proof_unit_id` (`unit_id`),
  KEY `idx_lb_order_unit_proof_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单元交付凭证表';

DROP TABLE IF EXISTS `lb_order_abnormal`;
CREATE TABLE IF NOT EXISTS `lb_order_abnormal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单元ID',
  `abnormal_no` VARCHAR(32) NOT NULL COMMENT '异常单号',
  `abnormal_type` VARCHAR(32) NOT NULL COMMENT '异常类型',
  `risk_level` VARCHAR(32) NOT NULL COMMENT '风险等级',
  `hit_rule_code` VARCHAR(64) DEFAULT NULL COMMENT '命中规则编码',
  `handle_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态',
  `handle_by` BIGINT DEFAULT NULL COMMENT '处理人',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_order_abnormal_no` (`abnormal_no`),
  KEY `idx_lb_order_abnormal_order_id` (`order_id`),
  KEY `idx_lb_order_abnormal_tenant_id` (`tenant_id`),
  KEY `idx_lb_order_abnormal_handle_status` (`handle_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常订单表';

DROP TABLE IF EXISTS `lb_order_operate_log`;
CREATE TABLE IF NOT EXISTS `lb_order_operate_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单元ID',
  `operate_type` VARCHAR(32) NOT NULL COMMENT '操作类型',
  `operate_role` VARCHAR(32) DEFAULT NULL COMMENT '操作角色',
  `operate_by` BIGINT DEFAULT NULL COMMENT '操作人',
  `before_status` VARCHAR(32) DEFAULT NULL COMMENT '操作前状态',
  `after_status` VARCHAR(32) DEFAULT NULL COMMENT '操作后状态',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `operate_time` DATETIME NOT NULL COMMENT '操作时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_order_operate_log_order_id` (`order_id`),
  KEY `idx_lb_order_operate_log_unit_id` (`unit_id`),
  KEY `idx_lb_order_operate_log_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';

DROP TABLE IF EXISTS `lb_wallet_account`;
CREATE TABLE IF NOT EXISTS `lb_commission_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `commission_no` VARCHAR(32) NOT NULL COMMENT '佣金单号',
  `promoter_id` BIGINT NOT NULL COMMENT '推广员ID',
  `user_id` BIGINT NOT NULL COMMENT '来源用户ID',
  `source_order_id` BIGINT DEFAULT NULL COMMENT '来源订单ID',
  `source_unit_id` BIGINT DEFAULT NULL COMMENT '来源单元ID',
  `commission_type` VARCHAR(32) NOT NULL COMMENT '佣金类型',
  `commission_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '佣金金额',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '佣金状态',
  `settle_time` DATETIME DEFAULT NULL COMMENT '结算时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_commission_order_no` (`commission_no`),
  KEY `idx_lb_commission_order_promoter_id` (`promoter_id`),
  KEY `idx_lb_commission_order_user_id` (`user_id`),
  KEY `idx_lb_commission_order_status` (`status`),
  KEY `idx_lb_commission_order_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广佣金单表';

DROP TABLE IF EXISTS `lb_wallet_account`;
CREATE TABLE IF NOT EXISTS `lb_wallet_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_code` VARCHAR(32) NOT NULL COMMENT '角色编码',
  `total_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '总资产',
  `available_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '可提现金额',
  `frozen_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
  `escrow_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '托管金额',
  `commission_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '佣金金额',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_wallet_account_tenant_id` (`tenant_id`),
  UNIQUE KEY `uk_lb_wallet_account_user_role` (`user_id`,`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包账户表';

DROP TABLE IF EXISTS `lb_wallet_flow`;
CREATE TABLE IF NOT EXISTS `lb_wallet_flow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `flow_no` VARCHAR(32) NOT NULL COMMENT '流水号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `wallet_account_id` BIGINT NOT NULL COMMENT '钱包账户ID',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型',
  `flow_type` VARCHAR(32) NOT NULL COMMENT '流水类型',
  `change_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '变动金额',
  `before_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '变动前金额',
  `after_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '变动后金额',
  `related_order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
  `related_unit_id` BIGINT DEFAULT NULL COMMENT '关联单元ID',
  `related_pay_order_id` BIGINT DEFAULT NULL COMMENT '关联支付订单ID',
  `related_refund_id` BIGINT DEFAULT NULL COMMENT '关联退款ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_wallet_flow_no` (`flow_no`),
  KEY `idx_lb_wallet_flow_user_id` (`user_id`),
  KEY `idx_lb_wallet_flow_biz_type` (`biz_type`),
  KEY `idx_lb_wallet_flow_tenant_id` (`tenant_id`),
  KEY `idx_lb_wallet_flow_related_order_id` (`related_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包流水表';

DROP TABLE IF EXISTS `lb_wallet_withdraw`;
CREATE TABLE IF NOT EXISTS `lb_wallet_withdraw` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `withdraw_no` VARCHAR(32) NOT NULL COMMENT '提现单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `wallet_account_id` BIGINT NOT NULL COMMENT '钱包账户ID',
  `bank_card_id` BIGINT NOT NULL COMMENT '银行卡ID',
  `apply_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '申请金额',
  `fee_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '手续费',
  `real_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '实际到账金额',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `pay_time` DATETIME DEFAULT NULL COMMENT '打款时间',
  `pay_transfer_id` BIGINT DEFAULT NULL COMMENT '转账单 ID',
  `pay_transfer_no` VARCHAR(64) DEFAULT NULL COMMENT '转账单号',
  `transfer_error_msg` VARCHAR(255) DEFAULT NULL COMMENT '转账失败原因',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_wallet_withdraw_no` (`withdraw_no`),
  KEY `idx_lb_wallet_withdraw_user_id` (`user_id`),
  KEY `idx_lb_wallet_withdraw_tenant_id` (`tenant_id`),
  KEY `idx_lb_wallet_withdraw_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提现申请表';

DROP TABLE IF EXISTS `lb_user_bank_card`;
CREATE TABLE IF NOT EXISTS `lb_user_bank_card` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `bank_name` VARCHAR(64) NOT NULL COMMENT '银行名称',
  `bank_code` VARCHAR(32) DEFAULT NULL COMMENT '银行编码',
  `card_no_encrypt` VARCHAR(255) NOT NULL COMMENT '加密卡号',
  `transfer_account` VARCHAR(128) DEFAULT NULL COMMENT '出款收款账号',
  `card_no_mask` VARCHAR(64) NOT NULL COMMENT '脱敏卡号',
  `account_name` VARCHAR(64) NOT NULL COMMENT '开户名',
  `reserved_mobile` VARCHAR(20) DEFAULT NULL COMMENT '预留手机号',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `is_default` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否默认',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_bank_card_user_id` (`user_id`),
  KEY `idx_lb_user_bank_card_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户银行卡表';

DROP TABLE IF EXISTS `lb_divide_rule`;
CREATE TABLE IF NOT EXISTS `lb_divide_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_name` VARCHAR(64) NOT NULL COMMENT '规则名称',
  `city_level` VARCHAR(32) DEFAULT NULL COMMENT '城市等级',
  `category_id` BIGINT DEFAULT NULL COMMENT '类目ID',
  `merchant_rate` DECIMAL(8,4) NOT NULL DEFAULT 0.0000 COMMENT '服务商比例',
  `platform_rate` DECIMAL(8,4) NOT NULL DEFAULT 0.0000 COMMENT '平台比例',
  `partner_rate` DECIMAL(8,4) NOT NULL DEFAULT 0.0000 COMMENT '合作商比例',
  `promoter_rate` DECIMAL(8,4) NOT NULL DEFAULT 0.0000 COMMENT '推广员比例',
  `tax_withhold_rate` DECIMAL(8,4) NOT NULL DEFAULT 0.0000 COMMENT '个税代扣比例',
  `min_withdraw_amount` DECIMAL(18,2) NOT NULL DEFAULT 10.00 COMMENT '最低提现金额',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `effective_time` DATETIME DEFAULT NULL COMMENT '生效时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_divide_rule_status` (`status`),
  KEY `idx_lb_divide_rule_tenant_id` (`tenant_id`),
  KEY `idx_lb_divide_rule_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分账规则表';

DROP TABLE IF EXISTS `lb_escrow_proof`;
CREATE TABLE IF NOT EXISTS `lb_escrow_proof` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `proof_no` VARCHAR(64) NOT NULL COMMENT '托管凭证编号',
  `order_id` BIGINT NOT NULL COMMENT '订单 ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单元 ID',
  `user_id` BIGINT NOT NULL COMMENT '下单用户 ID',
  `merchant_id` BIGINT DEFAULT NULL COMMENT '服务商 ID',
  `escrow_amount` DECIMAL(16,2) NOT NULL DEFAULT 0.00 COMMENT '托管金额',
  `proof_status` VARCHAR(32) NOT NULL COMMENT '凭证状态',
  `lock_reason` VARCHAR(255) DEFAULT NULL COMMENT '锁定原因',
  `unlock_reason` VARCHAR(255) DEFAULT NULL COMMENT '解锁/退款原因',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_escrow_proof_no` (`proof_no`),
  KEY `idx_lb_escrow_proof_order` (`order_id`),
  KEY `idx_lb_escrow_proof_unit` (`unit_id`),
  KEY `idx_lb_escrow_proof_user` (`user_id`),
  KEY `idx_lb_escrow_proof_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='托管凭证表';

DROP TABLE IF EXISTS `lb_order_divide_record`;
CREATE TABLE IF NOT EXISTS `lb_order_divide_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `divide_no` VARCHAR(64) NOT NULL COMMENT '分账明细编号',
  `order_id` BIGINT NOT NULL COMMENT '订单 ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单元 ID',
  `divide_rule_id` BIGINT DEFAULT NULL COMMENT '分账规则 ID',
  `target_type` VARCHAR(32) NOT NULL COMMENT '分账去向类型',
  `target_biz_id` BIGINT DEFAULT NULL COMMENT '分账目标业务 ID',
  `divide_rate` DECIMAL(8,2) NOT NULL DEFAULT 0.00 COMMENT '分账比例',
  `divide_amount` DECIMAL(16,2) NOT NULL DEFAULT 0.00 COMMENT '分账金额',
  `tax_amount` DECIMAL(16,2) NOT NULL DEFAULT 0.00 COMMENT '税务扣减金额',
  `settle_status` VARCHAR(32) NOT NULL COMMENT '结算状态',
  `city_level` VARCHAR(32) DEFAULT NULL COMMENT '城市等级',
  `category_id` BIGINT DEFAULT NULL COMMENT '类目 ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_order_divide_record_no` (`divide_no`),
  KEY `idx_lb_order_divide_record_order` (`order_id`),
  KEY `idx_lb_order_divide_record_unit` (`unit_id`),
  KEY `idx_lb_order_divide_record_target` (`target_type`, `target_biz_id`),
  KEY `idx_lb_order_divide_record_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单分账明细表';

DROP TABLE IF EXISTS `lb_risk_rule`;
CREATE TABLE IF NOT EXISTS `lb_risk_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_code` VARCHAR(64) NOT NULL COMMENT '规则编码',
  `rule_name` VARCHAR(128) NOT NULL COMMENT '规则名称',
  `rule_group` VARCHAR(64) NOT NULL COMMENT '规则分组',
  `rule_value` VARCHAR(255) NOT NULL COMMENT '规则值',
  `value_type` VARCHAR(32) NOT NULL COMMENT '值类型',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_risk_rule_tenant_id` (`tenant_id`),
  UNIQUE KEY `uk_lb_risk_rule_code` (`rule_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风控规则表';

DROP TABLE IF EXISTS `lb_sensitive_word`;
CREATE TABLE IF NOT EXISTS `lb_sensitive_word` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `word` VARCHAR(128) NOT NULL COMMENT '关键词',
  `word_type` VARCHAR(32) NOT NULL COMMENT '词库类型',
  `match_type` VARCHAR(32) NOT NULL COMMENT '匹配方式',
  `block_level` VARCHAR(32) NOT NULL COMMENT '拦截级别',
  `scene_type` VARCHAR(64) DEFAULT NULL COMMENT '生效场景',
  `replace_text` VARCHAR(64) DEFAULT NULL COMMENT '替换文本',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_sensitive_word_tenant_id` (`tenant_id`),
  UNIQUE KEY `uk_lb_sensitive_word_type_word` (`word_type`,`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

DROP TABLE IF EXISTS `lb_risk_event`;
CREATE TABLE IF NOT EXISTS `lb_risk_event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_type` VARCHAR(32) NOT NULL COMMENT '业务类型',
  `biz_id` BIGINT NOT NULL COMMENT '业务ID',
  `risk_type` VARCHAR(32) NOT NULL COMMENT '风险类型',
  `risk_level` VARCHAR(32) NOT NULL COMMENT '风险等级',
  `hit_rule_code` VARCHAR(64) DEFAULT NULL COMMENT '命中规则编码',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `dispose_status` VARCHAR(32) DEFAULT 'PENDING' COMMENT '处置状态',
  `dispose_action` VARCHAR(64) DEFAULT NULL COMMENT '处置动作',
  `handle_by` BIGINT DEFAULT NULL COMMENT '处理人',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `dispose_remark` VARCHAR(255) DEFAULT NULL COMMENT '处置备注',
  `related_user_ids` VARCHAR(255) DEFAULT NULL COMMENT '关联用户ID列表',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_risk_event_biz` (`biz_type`, `biz_id`),
  KEY `idx_lb_risk_event_risk_type` (`risk_type`),
  KEY `idx_lb_risk_event_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险事件表';

DROP TABLE IF EXISTS `lb_blacklist`;
CREATE TABLE IF NOT EXISTS `lb_blacklist` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `black_type` VARCHAR(32) NOT NULL COMMENT '黑名单类型',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT '原因',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_blacklist_user_id` (`user_id`),
  KEY `idx_lb_blacklist_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='黑名单表';

DROP TABLE IF EXISTS `lb_complaint`;
CREATE TABLE IF NOT EXISTS `lb_complaint` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `complaint_no` VARCHAR(32) NOT NULL COMMENT '投诉单号',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单元ID',
  `complainant_user_id` BIGINT NOT NULL COMMENT '投诉人用户ID',
  `respondent_user_id` BIGINT NOT NULL COMMENT '被投诉人用户ID',
  `complaint_type` VARCHAR(32) NOT NULL COMMENT '投诉类型',
  `content` TEXT COMMENT '投诉内容',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `handle_by` BIGINT DEFAULT NULL COMMENT '处理人',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  `result_desc` VARCHAR(255) DEFAULT NULL COMMENT '处理结果',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_complaint_no` (`complaint_no`),
  KEY `idx_lb_complaint_order_id` (`order_id`),
  KEY `idx_lb_complaint_tenant_id` (`tenant_id`),
  KEY `idx_lb_complaint_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉表';

DROP TABLE IF EXISTS `lb_complaint_file_rel`;
CREATE TABLE IF NOT EXISTS `lb_complaint_file_rel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `complaint_id` BIGINT NOT NULL COMMENT '投诉ID',
  `file_id` BIGINT NOT NULL COMMENT '文件ID',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_complaint_file_rel_complaint_id` (`complaint_id`),
  KEY `idx_lb_complaint_file_rel_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投诉附件关联表';

DROP TABLE IF EXISTS `lb_appeal`;
CREATE TABLE IF NOT EXISTS `lb_appeal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appeal_no` VARCHAR(32) NOT NULL COMMENT '申诉单号',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单元ID',
  `user_id` BIGINT NOT NULL COMMENT '申诉人用户ID',
  `appeal_type` VARCHAR(32) NOT NULL COMMENT '申诉类型',
  `content` TEXT COMMENT '申诉内容',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `appeal_expire_time` DATETIME DEFAULT NULL COMMENT '申诉时效截止时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_appeal_no` (`appeal_no`),
  KEY `idx_lb_appeal_order_id` (`order_id`),
  KEY `idx_lb_appeal_tenant_id` (`tenant_id`),
  KEY `idx_lb_appeal_status` (`status`),
  KEY `idx_lb_appeal_audit_status` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申诉表';

DROP TABLE IF EXISTS `lb_appeal_file_rel`;
CREATE TABLE IF NOT EXISTS `lb_appeal_file_rel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appeal_id` BIGINT NOT NULL COMMENT '申诉ID',
  `file_id` BIGINT NOT NULL COMMENT '文件ID',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_lb_appeal_file_rel_appeal_id` (`appeal_id`),
  KEY `idx_lb_appeal_file_rel_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申诉附件关联表';

DROP TABLE IF EXISTS `lb_review`;
CREATE TABLE IF NOT EXISTS `lb_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `unit_id` BIGINT DEFAULT NULL COMMENT '单元ID',
  `from_user_id` BIGINT NOT NULL COMMENT '评价发起人',
  `to_user_id` BIGINT NOT NULL COMMENT '评价目标用户',
  `star_level` TINYINT NOT NULL COMMENT '星级',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `is_auto_review` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否自动评价',
  `is_content_supplemented` BIT(1) NOT NULL DEFAULT b'1' COMMENT '自动评价文字是否已补充',
  `edit_deadline_time` DATETIME DEFAULT NULL COMMENT '评价可编辑截止时间',
  `last_edit_time` DATETIME DEFAULT NULL COMMENT '最后编辑时间',
  `edit_count` INT NOT NULL DEFAULT 0 COMMENT '编辑次数',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_lb_review_order_id` (`order_id`),
  KEY `idx_lb_review_unit_id` (`unit_id`),
  KEY `idx_lb_review_from_unit` (`from_user_id`, `unit_id`),
  KEY `idx_lb_review_tenant_id` (`tenant_id`),
  KEY `idx_lb_review_to_user_id` (`to_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

DROP TABLE IF EXISTS `lb_credit_rule`;
CREATE TABLE IF NOT EXISTS `lb_credit_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_code` VARCHAR(64) NOT NULL COMMENT '规则编码',
  `rule_name` VARCHAR(128) NOT NULL COMMENT '规则名称',
  `score_change` INT NOT NULL DEFAULT 0 COMMENT '分值变动',
  `trigger_type` VARCHAR(32) NOT NULL COMMENT '触发类型',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_credit_rule_tenant_id` (`tenant_id`),
  UNIQUE KEY `uk_lb_credit_rule_code` (`rule_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信用分规则表';

DROP TABLE IF EXISTS `lb_credit_level_benefit`;
CREATE TABLE IF NOT EXISTS `lb_credit_level_benefit` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `level_code` VARCHAR(32) NOT NULL COMMENT '等级编码',
  `level_name` VARCHAR(64) NOT NULL COMMENT '等级名称',
  `benefit_title` VARCHAR(128) NOT NULL COMMENT '权益标题',
  `benefit_desc` VARCHAR(500) NOT NULL COMMENT '权益说明',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_credit_level_benefit_level_code` (`level_code`),
  KEY `idx_lb_credit_level_benefit_status` (`status`),
  KEY `idx_lb_credit_level_benefit_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信用等级权益表';

DROP TABLE IF EXISTS `lb_credit_record`;
CREATE TABLE IF NOT EXISTS `lb_credit_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `merchant_id` BIGINT DEFAULT NULL COMMENT '服务商ID',
  `rule_id` BIGINT DEFAULT NULL COMMENT '信用规则ID',
  `rule_code` VARCHAR(64) DEFAULT NULL COMMENT '规则编码',
  `rule_name` VARCHAR(128) DEFAULT NULL COMMENT '规则名称',
  `score_change` INT NOT NULL DEFAULT 0 COMMENT '分值变动',
  `before_score` INT DEFAULT NULL COMMENT '变动前分值',
  `after_score` INT DEFAULT NULL COMMENT '变动后分值',
  `trigger_type` VARCHAR(32) DEFAULT NULL COMMENT '触发类型',
  `biz_type` VARCHAR(32) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_credit_record_user_id` (`user_id`),
  KEY `idx_lb_credit_record_merchant_id` (`merchant_id`),
  KEY `idx_lb_credit_record_rule_id` (`rule_id`),
  KEY `idx_lb_credit_record_rule_code` (`rule_code`),
  KEY `idx_lb_credit_record_trigger_type` (`trigger_type`),
  KEY `idx_lb_credit_record_biz_type_biz_id` (`biz_type`, `biz_id`),
  KEY `idx_lb_credit_record_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='信用记录表';

DROP TABLE IF EXISTS `lb_promoter`;
CREATE TABLE IF NOT EXISTS `lb_promoter` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `level_code` VARCHAR(32) DEFAULT NULL COMMENT '等级编码',
  `invite_code` VARCHAR(64) NOT NULL COMMENT '邀请码',
  `invite_url` VARCHAR(255) DEFAULT NULL COMMENT '邀请链接',
  `bind_user_count` INT NOT NULL DEFAULT 0 COMMENT '绑定用户数',
  `convert_count` INT NOT NULL DEFAULT 0 COMMENT '转化用户数',
  `total_commission_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '累计佣金金额',
  `available_commission_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '可提现佣金金额',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_promoter_user_id` (`user_id`),
  UNIQUE KEY `uk_lb_promoter_invite_code` (`invite_code`),
  KEY `idx_lb_promoter_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广员表';

DROP TABLE IF EXISTS `lb_partner_info`;
CREATE TABLE IF NOT EXISTS `lb_partner_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `partner_name` VARCHAR(128) NOT NULL COMMENT '合作商名称',
  `contact_name` VARCHAR(64) NOT NULL COMMENT '联系人',
  `contact_mobile` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_partner_info_user_id` (`user_id`),
  KEY `idx_lb_partner_info_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区域合作商表';

DROP TABLE IF EXISTS `lb_partner_region_rel`;
CREATE TABLE IF NOT EXISTS `lb_partner_region_rel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `partner_id` BIGINT NOT NULL COMMENT '合作商ID',
  `province` VARCHAR(64) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(64) DEFAULT NULL COMMENT '市',
  `district` VARCHAR(64) DEFAULT NULL COMMENT '区',
  `adcode` VARCHAR(16) DEFAULT NULL COMMENT '区域编码',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_partner_region_rel_partner_id` (`partner_id`),
  KEY `idx_lb_partner_region_rel_adcode` (`adcode`),
  KEY `idx_lb_partner_region_rel_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合作商辖区关系表';

DROP TABLE IF EXISTS `lb_user_role_apply`;
CREATE TABLE IF NOT EXISTS `lb_user_role_apply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `apply_role_code` VARCHAR(32) NOT NULL COMMENT '申请角色编码',
  `apply_reason` VARCHAR(255) DEFAULT NULL COMMENT '申请说明',
  `resource_desc` VARCHAR(255) DEFAULT NULL COMMENT '资源说明',
  `expected_conversion_desc` VARCHAR(255) DEFAULT NULL COMMENT '预期转化说明',
  `ability_desc` VARCHAR(255) DEFAULT NULL COMMENT '能力说明',
  `available_time_desc` VARCHAR(255) DEFAULT NULL COMMENT '可投入时间说明',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_role_apply_user_id` (`user_id`),
  KEY `idx_lb_user_role_apply_role_code` (`apply_role_code`),
  KEY `idx_lb_user_role_apply_audit_status` (`audit_status`),
  KEY `idx_lb_user_role_apply_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户身份申请表';

DROP TABLE IF EXISTS `lb_merchant_price_report`;
CREATE TABLE IF NOT EXISTS `lb_merchant_price_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `partner_id` BIGINT DEFAULT NULL COMMENT '合作商ID',
  `category_id` BIGINT NOT NULL COMMENT '服务类目ID',
  `region_code` VARCHAR(16) NOT NULL COMMENT '区域编码',
  `suggested_price` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '建议价格',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '申报状态',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_merchant_price_report_merchant_id` (`merchant_id`),
  KEY `idx_lb_merchant_price_report_partner_id` (`partner_id`),
  KEY `idx_lb_merchant_price_report_category_id` (`category_id`),
  KEY `idx_lb_merchant_price_report_region_code` (`region_code`),
  KEY `idx_lb_merchant_price_report_status` (`status`),
  KEY `idx_lb_merchant_price_report_audit_status` (`audit_status`),
  KEY `idx_lb_merchant_price_report_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务商价格申报表';

DROP TABLE IF EXISTS `lb_message_template`;
CREATE TABLE IF NOT EXISTS `lb_message_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_code` VARCHAR(64) NOT NULL COMMENT '模板编码',
  `template_name` VARCHAR(128) NOT NULL COMMENT '模板名称',
  `scene_code` VARCHAR(64) NOT NULL DEFAULT 'SYSTEM_NOTICE' COMMENT '场景编码',
  `message_category` VARCHAR(32) NOT NULL DEFAULT 'SYSTEM' COMMENT '消息分类',
  `template_type` VARCHAR(32) NOT NULL COMMENT '模板类型',
  `channel_type` VARCHAR(32) NOT NULL COMMENT '发送渠道',
  `title_template` VARCHAR(255) DEFAULT NULL COMMENT '标题模板',
  `content_template` TEXT NOT NULL COMMENT '正文模板',
  `route_type` VARCHAR(32) DEFAULT NULL COMMENT '路由类型',
  `route_value` VARCHAR(255) DEFAULT NULL COMMENT '路由值',
  `mp_template_id` VARCHAR(128) DEFAULT NULL COMMENT '公众号/小程序模板ID',
  `sms_template_code` VARCHAR(128) DEFAULT NULL COMMENT '短信模板编码',
  `voice_text_template` TEXT DEFAULT NULL COMMENT '语音朗读模板',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_message_template_code` (`template_code`),
  KEY `idx_lb_message_template_scene_channel` (`scene_code`, `channel_type`, `status`),
  KEY `idx_lb_message_template_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';

DROP TABLE IF EXISTS `lb_register_reminder_record`;
CREATE TABLE IF NOT EXISTS `lb_register_reminder_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `reminder_key` VARCHAR(128) NOT NULL COMMENT '提醒键',
  `reminder_scene` VARCHAR(64) NOT NULL COMMENT '提醒场景',
  `device_id` VARCHAR(128) DEFAULT NULL COMMENT '设备标识',
  `social_type` INT DEFAULT NULL COMMENT '社交平台类型',
  `social_openid` VARCHAR(128) DEFAULT NULL COMMENT '社交平台 openid',
  `trigger_count` INT NOT NULL DEFAULT 0 COMMENT '触发次数',
  `cooldown_minutes` INT NOT NULL DEFAULT 60 COMMENT '冷却分钟数',
  `last_trigger_time` DATETIME DEFAULT NULL COMMENT '最后触发时间',
  `last_ack_time` DATETIME DEFAULT NULL COMMENT '最后确认时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/ACKED/CLOSED',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_register_reminder_key` (`reminder_key`),
  KEY `idx_lb_register_reminder_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='注册提醒记录表';

DROP TABLE IF EXISTS `lb_identity_verify_log`;
CREATE TABLE IF NOT EXISTS `lb_identity_verify_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `real_name_id` BIGINT DEFAULT NULL COMMENT '实名认证记录ID',
  `verify_flow_no` VARCHAR(64) NOT NULL COMMENT '核验流水号',
  `verify_provider` VARCHAR(64) DEFAULT NULL COMMENT '核验供应商',
  `verify_stage` VARCHAR(32) NOT NULL COMMENT '核验阶段',
  `verify_status` VARCHAR(32) NOT NULL COMMENT '核验状态',
  `request_snapshot` TEXT DEFAULT NULL COMMENT '请求快照',
  `response_snapshot` TEXT DEFAULT NULL COMMENT '响应快照',
  `fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_identity_verify_log_user_id` (`user_id`),
  KEY `idx_lb_identity_verify_log_flow_no` (`verify_flow_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实名核验日志表';

DROP TABLE IF EXISTS `lb_cert_exemption_apply`;
CREATE TABLE IF NOT EXISTS `lb_cert_exemption_apply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `merchant_id` BIGINT DEFAULT NULL COMMENT '服务商ID',
  `qualification_id` BIGINT DEFAULT NULL COMMENT '关联资质ID',
  `exemption_type` VARCHAR(32) NOT NULL COMMENT '豁免类型',
  `reason` VARCHAR(500) NOT NULL COMMENT '申请原因',
  `attachment_file_ids_json` TEXT DEFAULT NULL COMMENT '附件文件ID JSON',
  `effective_start_time` DATETIME DEFAULT NULL COMMENT '生效开始时间',
  `effective_end_time` DATETIME DEFAULT NULL COMMENT '生效结束时间',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '驳回原因',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_cert_exemption_apply_user_id` (`user_id`),
  KEY `idx_lb_cert_exemption_apply_qualification_id` (`qualification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证件豁免申请表';

DROP TABLE IF EXISTS `lb_merchant_sub_account`;
CREATE TABLE IF NOT EXISTS `lb_merchant_sub_account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `merchant_id` BIGINT NOT NULL COMMENT '服务商ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `mobile` VARCHAR(20) NOT NULL COMMENT '手机号',
  `permission_codes_json` TEXT NOT NULL COMMENT '权限编码 JSON',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_merchant_sub_account_user_id` (`user_id`),
  KEY `idx_lb_merchant_sub_account_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务商子账号表';

DROP TABLE IF EXISTS `lb_merchant_sub_account_service_point_rel`;
CREATE TABLE IF NOT EXISTS `lb_merchant_sub_account_service_point_rel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `sub_account_id` BIGINT NOT NULL COMMENT '子账号ID',
  `service_point_id` BIGINT NOT NULL COMMENT '服务点ID',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_sub_account_service_point` (`sub_account_id`, `service_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='子账号服务点关联表';

DROP TABLE IF EXISTS `lb_message_scene`;
CREATE TABLE IF NOT EXISTS `lb_message_scene` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scene_code` VARCHAR(64) NOT NULL COMMENT '场景编码',
  `scene_name` VARCHAR(128) NOT NULL COMMENT '场景名称',
  `message_category` VARCHAR(32) NOT NULL COMMENT '消息分类',
  `default_channels` VARCHAR(255) NOT NULL COMMENT '默认渠道，逗号分隔',
  `mandatory_sms` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否强制短信',
  `voice_enabled` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否支持语音朗读',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_message_scene_code` (`scene_code`),
  KEY `idx_lb_message_scene_category_status` (`message_category`, `status`),
  KEY `idx_lb_message_scene_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息场景配置表';

DROP TABLE IF EXISTS `lb_message_campaign`;
CREATE TABLE IF NOT EXISTS `lb_message_campaign` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `campaign_name` VARCHAR(128) NOT NULL COMMENT '投放名称',
  `source_type` VARCHAR(32) NOT NULL COMMENT '来源类型',
  `audit_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态',
  `execute_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '执行状态',
  `target_mode` VARCHAR(32) NOT NULL COMMENT '目标模式',
  `target_region_codes` TEXT DEFAULT NULL COMMENT '目标区域编码 JSON',
  `target_category_ids` TEXT DEFAULT NULL COMMENT '目标类目ID JSON',
  `target_role_codes` VARCHAR(255) DEFAULT NULL COMMENT '目标角色编码，逗号分隔',
  `delivery_time_windows` VARCHAR(255) DEFAULT NULL COMMENT '投放时段，逗号分隔',
  `schedule_time` DATETIME DEFAULT NULL COMMENT '计划投放时间',
  `execute_time` DATETIME DEFAULT NULL COMMENT '执行时间',
  `scene_code` VARCHAR(64) NOT NULL DEFAULT 'SYSTEM_NOTICE' COMMENT '关联场景编码',
  `message_category` VARCHAR(32) NOT NULL DEFAULT 'SYSTEM' COMMENT '消息分类',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务ID',
  `applicant_user_id` BIGINT DEFAULT NULL COMMENT '申请用户ID',
  `audit_user_id` BIGINT DEFAULT NULL COMMENT '审核人ID',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `planned_audience_count` INT NOT NULL DEFAULT 0 COMMENT '计划触达人数',
  `reached_count` INT NOT NULL DEFAULT 0 COMMENT '触达人数',
  `clicked_count` INT NOT NULL DEFAULT 0 COMMENT '点击人数',
  `read_count` INT NOT NULL DEFAULT 0 COMMENT '已读人数',
  `voice_played_count` INT NOT NULL DEFAULT 0 COMMENT '语音播放人数',
  `content_snapshot` TEXT DEFAULT NULL COMMENT '内容快照',
  `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
  `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '驳回原因',
  `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_message_campaign_source_audit` (`source_type`, `audit_status`),
  KEY `idx_lb_message_campaign_scene_status` (`scene_code`, `execute_status`),
  KEY `idx_lb_message_campaign_applicant` (`applicant_user_id`),
  KEY `idx_lb_message_campaign_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息投放活动表';

DROP TABLE IF EXISTS `lb_message_push_task`;
CREATE TABLE IF NOT EXISTS `lb_message_push_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_name` VARCHAR(128) NOT NULL COMMENT '任务名称',
  `campaign_id` BIGINT DEFAULT NULL COMMENT '投放活动ID',
  `scene_code` VARCHAR(64) NOT NULL DEFAULT 'SYSTEM_NOTICE' COMMENT '场景编码',
  `message_category` VARCHAR(32) NOT NULL DEFAULT 'SYSTEM' COMMENT '消息分类',
  `target_scope` VARCHAR(64) DEFAULT NULL COMMENT '目标范围',
  `channel_type` VARCHAR(32) NOT NULL COMMENT '发送渠道',
  `template_id` BIGINT DEFAULT NULL COMMENT '关联模板ID',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务ID',
  `planned_send_time` DATETIME DEFAULT NULL COMMENT '计划发送时间',
  `execute_time` DATETIME DEFAULT NULL COMMENT '执行时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态',
  `execute_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '执行状态',
  `planned_audience_count` INT NOT NULL DEFAULT 0 COMMENT '计划触达人数',
  `reached_count` INT NOT NULL DEFAULT 0 COMMENT '触达人数',
  `clicked_count` INT NOT NULL DEFAULT 0 COMMENT '点击人数',
  `read_count` INT NOT NULL DEFAULT 0 COMMENT '已读人数',
  `voice_played_count` INT NOT NULL DEFAULT 0 COMMENT '语音播放人数',
  `success_count` INT NOT NULL DEFAULT 0 COMMENT '成功数量',
  `fail_count` INT NOT NULL DEFAULT 0 COMMENT '失败数量',
  `creator_remark` VARCHAR(255) DEFAULT NULL COMMENT '任务备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_message_push_task_status` (`status`),
  KEY `idx_lb_message_push_task_campaign_id` (`campaign_id`),
  KEY `idx_lb_message_push_task_template_id` (`template_id`),
  KEY `idx_lb_message_push_task_scene_channel` (`scene_code`, `channel_type`),
  KEY `idx_lb_message_push_task_biz_type_biz_id` (`biz_type`, `biz_id`),
  KEY `idx_lb_message_push_task_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息推送任务表';

DROP TABLE IF EXISTS `lb_message_record`;
CREATE TABLE IF NOT EXISTS `lb_message_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_id` BIGINT DEFAULT NULL COMMENT '模板ID',
  `campaign_id` BIGINT DEFAULT NULL COMMENT '投放活动ID',
  `push_task_id` BIGINT DEFAULT NULL COMMENT '推送任务ID',
  `receiver_user_id` BIGINT NOT NULL COMMENT '接收用户ID',
  `scene_code` VARCHAR(64) NOT NULL DEFAULT 'SYSTEM_NOTICE' COMMENT '场景编码',
  `message_category` VARCHAR(32) NOT NULL DEFAULT 'SYSTEM' COMMENT '消息分类',
  `channel_type` VARCHAR(32) NOT NULL COMMENT '发送渠道',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务ID',
  `dedupe_key` VARCHAR(128) DEFAULT NULL COMMENT '幂等键',
  `send_status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '发送状态',
  `send_time` DATETIME DEFAULT NULL COMMENT '发送时间',
  `fail_reason` VARCHAR(255) DEFAULT NULL COMMENT '失败原因',
  `title` VARCHAR(255) DEFAULT NULL COMMENT '消息标题',
  `content_snapshot` TEXT DEFAULT NULL COMMENT '消息内容快照',
  `route_type` VARCHAR(32) DEFAULT NULL COMMENT '路由类型',
  `route_value` VARCHAR(255) DEFAULT NULL COMMENT '路由值',
  `read_status` VARCHAR(32) NOT NULL DEFAULT 'UNREAD' COMMENT '已读状态',
  `read_time` DATETIME DEFAULT NULL COMMENT '已读时间',
  `exposed_time` DATETIME DEFAULT NULL COMMENT '曝光时间',
  `click_time` DATETIME DEFAULT NULL COMMENT '点击时间',
  `voice_played_time` DATETIME DEFAULT NULL COMMENT '语音播放时间',
  `voice_text` TEXT DEFAULT NULL COMMENT '语音朗读文本',
  `provider_message_id` VARCHAR(128) DEFAULT NULL COMMENT '渠道消息ID',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_message_record_receiver_user_id` (`receiver_user_id`),
  KEY `idx_lb_message_record_campaign_id` (`campaign_id`),
  KEY `idx_lb_message_record_push_task_id` (`push_task_id`),
  KEY `idx_lb_message_record_scene_category` (`scene_code`, `message_category`),
  KEY `idx_lb_message_record_biz_type_biz_id` (`biz_type`, `biz_id`),
  KEY `idx_lb_message_record_read_status` (`read_status`),
  KEY `idx_lb_message_record_send_status` (`send_status`),
  UNIQUE KEY `uk_lb_message_record_dedupe_key` (`dedupe_key`),
  KEY `idx_lb_message_record_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息发送记录表';

DROP TABLE IF EXISTS `lb_user_device`;
CREATE TABLE IF NOT EXISTS `lb_user_device` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `device_fingerprint` VARCHAR(128) NOT NULL COMMENT '设备指纹摘要',
  `device_name` VARCHAR(128) DEFAULT NULL COMMENT '设备名称',
  `last_ip` VARCHAR(64) DEFAULT NULL COMMENT '最近IP',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最近登录时间',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_device_user_id` (`user_id`),
  KEY `idx_lb_user_device_fingerprint` (`device_fingerprint`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备表';

DROP TABLE IF EXISTS `lb_user_risk_relation`;
CREATE TABLE IF NOT EXISTS `lb_user_risk_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `related_user_id` BIGINT NOT NULL COMMENT '关联用户ID',
  `relation_type` VARCHAR(32) NOT NULL COMMENT '关联类型',
  `relation_value` VARCHAR(255) DEFAULT NULL COMMENT '关联值',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_risk_relation_user_id` (`user_id`),
  KEY `idx_lb_user_risk_relation_related_user_id` (`related_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关联风险表';

DROP TABLE IF EXISTS `lb_user_restrict_record`;
CREATE TABLE IF NOT EXISTS `lb_user_restrict_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `restrict_type` VARCHAR(32) NOT NULL COMMENT '限制类型',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `source_rule_code` VARCHAR(64) DEFAULT NULL COMMENT '来源规则编码',
  `source_biz_type` VARCHAR(32) DEFAULT NULL COMMENT '来源业务类型',
  `source_biz_id` BIGINT DEFAULT NULL COMMENT '来源业务ID',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT '原因',
  `released_by` BIGINT DEFAULT NULL COMMENT '解除人',
  `released_time` DATETIME DEFAULT NULL COMMENT '解除时间',
  `release_remark` VARCHAR(255) DEFAULT NULL COMMENT '解除备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_restrict_record_user_id` (`user_id`),
  KEY `idx_lb_user_restrict_record_type_status` (`restrict_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户限制记录表';

DROP TABLE IF EXISTS `lb_user_frozen_fund_record`;
CREATE TABLE IF NOT EXISTS `lb_user_frozen_fund_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `wallet_account_id` BIGINT NOT NULL COMMENT '钱包账户ID',
  `frozen_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
  `released_amount` DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '已解冻金额',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `source_biz_type` VARCHAR(32) DEFAULT NULL COMMENT '来源业务类型',
  `source_biz_id` BIGINT DEFAULT NULL COMMENT '来源业务ID',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT '原因',
  `released_by` BIGINT DEFAULT NULL COMMENT '解除人',
  `released_time` DATETIME DEFAULT NULL COMMENT '解除时间',
  `release_remark` VARCHAR(255) DEFAULT NULL COMMENT '解除备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_frozen_fund_record_user_id` (`user_id`),
  KEY `idx_lb_user_frozen_fund_record_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户冻结资金记录表';

DROP TABLE IF EXISTS `lb_sensitive_hit_log`;
CREATE TABLE IF NOT EXISTS `lb_sensitive_hit_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scene_type` VARCHAR(64) NOT NULL COMMENT '命中场景',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务ID',
  `word_id` BIGINT DEFAULT NULL COMMENT '敏感词ID',
  `hit_word` VARCHAR(128) DEFAULT NULL COMMENT '命中词',
  `block_level` VARCHAR(32) DEFAULT NULL COMMENT '拦截级别',
  `strategy` VARCHAR(32) DEFAULT NULL COMMENT '处理策略',
  `content_type` VARCHAR(32) DEFAULT NULL COMMENT '内容类型',
  `file_id` BIGINT DEFAULT NULL COMMENT '文件ID',
  `ocr_text_snapshot` TEXT COMMENT 'OCR 文本快照',
  `qr_content_snapshot` TEXT COMMENT '二维码内容快照',
  `manual_audit_result` VARCHAR(32) DEFAULT NULL COMMENT '人工审核结果',
  `content_snapshot` TEXT COMMENT '命中内容快照',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_sensitive_hit_log_scene_type` (`scene_type`),
  KEY `idx_lb_sensitive_hit_log_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词命中日志表';

DROP TABLE IF EXISTS `lb_user_sensitive_custom_word`;
CREATE TABLE IF NOT EXISTS `lb_user_sensitive_custom_word` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `word` VARCHAR(128) NOT NULL COMMENT '自定义脱敏词',
  `scene_type` VARCHAR(64) DEFAULT NULL COMMENT '适用场景，为空表示全场景',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ENABLE' COMMENT '状态：ENABLE/DISABLE',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_user_sensitive_custom_word_user_id` (`user_id`),
  KEY `idx_lb_user_sensitive_custom_word_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户自定义脱敏词表';

DROP TABLE IF EXISTS `lb_sensitive_image_scan_result`;
CREATE TABLE IF NOT EXISTS `lb_sensitive_image_scan_result` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scene_type` VARCHAR(64) NOT NULL COMMENT '检测场景',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户 ID',
  `biz_type` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
  `biz_id` BIGINT DEFAULT NULL COMMENT '业务 ID',
  `file_id` BIGINT NOT NULL COMMENT '文件 ID',
  `source_file_url` VARCHAR(512) DEFAULT NULL COMMENT '原图 URL',
  `masked_file_url` VARCHAR(512) DEFAULT NULL COMMENT '脱敏展示图 URL',
  `ocr_text` LONGTEXT DEFAULT NULL COMMENT 'OCR 文本',
  `qr_content` LONGTEXT DEFAULT NULL COMMENT '二维码内容',
  `hit_words` LONGTEXT DEFAULT NULL COMMENT '命中规则列表 JSON',
  `scan_status` VARCHAR(32) NOT NULL DEFAULT 'PASSED' COMMENT '扫描状态：PASSED/BLOCKED/REVIEW/FAILED',
  `failure_reason` VARCHAR(255) DEFAULT NULL COMMENT '失败原因',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_sensitive_image_scan_result_scene_type` (`scene_type`),
  KEY `idx_lb_sensitive_image_scan_result_user_id` (`user_id`),
  KEY `idx_lb_sensitive_image_scan_result_biz` (`biz_type`, `biz_id`),
  KEY `idx_lb_sensitive_image_scan_result_scan_status` (`scan_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片 OCR 与二维码检测结果表';

DROP TABLE IF EXISTS `lb_punish_log`;
CREATE TABLE IF NOT EXISTS `lb_punish_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `punish_type` VARCHAR(64) NOT NULL COMMENT '处罚类型',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '处罚状态',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT '处罚原因',
  `source_biz_type` VARCHAR(64) DEFAULT NULL COMMENT '来源业务类型',
  `source_biz_id` BIGINT DEFAULT NULL COMMENT '来源业务ID',
  `source_record_type` VARCHAR(64) DEFAULT NULL COMMENT '来源记录类型',
  `source_record_id` BIGINT DEFAULT NULL COMMENT '来源记录ID',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人',
  `operate_time` DATETIME DEFAULT NULL COMMENT '处罚执行时间',
  `start_time` DATETIME DEFAULT NULL COMMENT '生效时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `release_operator_id` BIGINT DEFAULT NULL COMMENT '解除人',
  `release_time` DATETIME DEFAULT NULL COMMENT '解除时间',
  `release_remark` VARCHAR(255) DEFAULT NULL COMMENT '解除备注',
  `ext_json` TEXT COMMENT '扩展信息',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_punish_log_user_id` (`user_id`),
  KEY `idx_lb_punish_log_type_status` (`punish_type`, `status`),
  KEY `idx_lb_punish_log_source_record` (`source_record_type`, `source_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处罚执行日志表';

DROP TABLE IF EXISTS `lb_promote_content`;
CREATE TABLE IF NOT EXISTS `lb_promote_content` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `promoter_id` BIGINT NOT NULL COMMENT '推广员ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(255) NOT NULL COMMENT '标题',
  `content` TEXT COMMENT '内容',
  `image_urls` TEXT COMMENT '图片URL列表',
  `status` VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  `system_audit_result` VARCHAR(32) DEFAULT NULL COMMENT '系统审核结果',
  `system_audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '系统审核备注',
  `system_audit_time` DATETIME DEFAULT NULL COMMENT '系统审核时间',
  `manual_audit_result` VARCHAR(32) DEFAULT NULL COMMENT '人工审核结果',
  `manual_audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '人工审核备注',
  `manual_audit_by` BIGINT DEFAULT NULL COMMENT '人工审核人',
  `manual_audit_time` DATETIME DEFAULT NULL COMMENT '人工审核时间',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `offline_reason` VARCHAR(255) DEFAULT NULL COMMENT '下架原因',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_promote_content_promoter_id` (`promoter_id`),
  KEY `idx_lb_promote_content_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广内容表';

DROP TABLE IF EXISTS `lb_promote_content_audit_log`;
CREATE TABLE IF NOT EXISTS `lb_promote_content_audit_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content_id` BIGINT NOT NULL COMMENT '推广内容ID',
  `audit_type` VARCHAR(32) NOT NULL COMMENT '审核类型',
  `audit_result` VARCHAR(32) DEFAULT NULL COMMENT '审核结果',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_promote_content_audit_log_content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广内容审核日志表';

DROP TABLE IF EXISTS `lb_promoter_penalty_record`;
CREATE TABLE IF NOT EXISTS `lb_promoter_penalty_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `promoter_id` BIGINT NOT NULL COMMENT '推广员ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `content_id` BIGINT DEFAULT NULL COMMENT '推广内容ID',
  `penalty_action` VARCHAR(32) NOT NULL COMMENT '处罚动作',
  `score_change` INT DEFAULT NULL COMMENT '分值变化',
  `reason` VARCHAR(255) DEFAULT NULL COMMENT '原因',
  `status` VARCHAR(32) NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_promoter_penalty_record_promoter_id` (`promoter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广员处罚记录表';

DROP TABLE IF EXISTS `lb_promote_appeal`;
CREATE TABLE IF NOT EXISTS `lb_promote_appeal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content_id` BIGINT NOT NULL COMMENT '推广内容ID',
  `promoter_id` BIGINT NOT NULL COMMENT '推广员ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `appeal_reason` TEXT COMMENT '申诉理由',
  `status` VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
  `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
  `audit_by` BIGINT DEFAULT NULL COMMENT '审核人',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_promote_appeal_content_id` (`content_id`),
  KEY `idx_lb_promote_appeal_promoter_id` (`promoter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推广内容申诉表';

DROP TABLE IF EXISTS `lb_app_message_setting`;
CREATE TABLE IF NOT EXISTS `lb_app_message_setting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `voice_read_enabled` BIT(1) NOT NULL DEFAULT b'1' COMMENT '语音朗读开关',
  `popup_enabled` BIT(1) NOT NULL DEFAULT b'1' COMMENT '弹窗开关',
  `marketing_enabled` BIT(1) NOT NULL DEFAULT b'1' COMMENT '营销消息开关',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lb_app_message_setting_user_id` (`user_id`),
  KEY `idx_lb_app_message_setting_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息偏好设置表';

DROP TABLE IF EXISTS `lb_message_feedback_stat`;
CREATE TABLE IF NOT EXISTS `lb_message_feedback_stat` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `scene_code` VARCHAR(64) NOT NULL COMMENT '场景编码',
  `message_category` VARCHAR(32) NOT NULL COMMENT '消息分类',
  `template_id` BIGINT DEFAULT NULL COMMENT '模板ID',
  `campaign_id` BIGINT DEFAULT NULL COMMENT '投放活动ID',
  `push_task_id` BIGINT DEFAULT NULL COMMENT '推送任务ID',
  `channel_type` VARCHAR(32) NOT NULL COMMENT '渠道类型',
  `planned_audience_count` INT NOT NULL DEFAULT 0 COMMENT '计划触达人数',
  `reached_count` INT NOT NULL DEFAULT 0 COMMENT '触达人数',
  `clicked_count` INT NOT NULL DEFAULT 0 COMMENT '点击人数',
  `read_count` INT NOT NULL DEFAULT 0 COMMENT '已读人数',
  `voice_played_count` INT NOT NULL DEFAULT 0 COMMENT '语音播放人数',
  `reach_rate` DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT '触达率',
  `click_rate` DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT '点击率',
  `read_rate` DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT '已读率',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_message_feedback_scene_channel` (`scene_code`, `channel_type`),
  KEY `idx_lb_message_feedback_template` (`template_id`),
  KEY `idx_lb_message_feedback_campaign` (`campaign_id`),
  KEY `idx_lb_message_feedback_task` (`push_task_id`),
  KEY `idx_lb_message_feedback_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息反馈统计表';

DROP TABLE IF EXISTS `lb_message_optimization`;
CREATE TABLE IF NOT EXISTS `lb_message_optimization` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ref_type` VARCHAR(32) NOT NULL COMMENT '关联类型 TEMPLATE/CAMPAIGN',
  `template_id` BIGINT DEFAULT NULL COMMENT '模板ID',
  `campaign_id` BIGINT DEFAULT NULL COMMENT '投放活动ID',
  `scene_code` VARCHAR(64) DEFAULT NULL COMMENT '场景编码',
  `message_category` VARCHAR(32) DEFAULT NULL COMMENT '消息分类',
  `channel_type` VARCHAR(32) DEFAULT NULL COMMENT '渠道类型',
  `stat_start_date` DATE DEFAULT NULL COMMENT '统计开始日期',
  `stat_end_date` DATE DEFAULT NULL COMMENT '统计结束日期',
  `reach_rate` DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT '触达率',
  `click_rate` DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT '点击率',
  `optimization_note` TEXT DEFAULT NULL COMMENT '优化备注',
  `next_action` VARCHAR(500) DEFAULT NULL COMMENT '下一步动作',
  `owner` VARCHAR(64) DEFAULT NULL COMMENT '负责人',
  `deadline` DATETIME DEFAULT NULL COMMENT '截止时间',
  `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT '租户编号',
  `creator` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_lb_message_optimization_ref` (`ref_type`, `template_id`, `campaign_id`),
  KEY `idx_lb_message_optimization_scene` (`scene_code`, `channel_type`),
  KEY `idx_lb_message_optimization_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息优化工作台表';
