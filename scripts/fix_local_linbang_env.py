import pymysql


DB_CONFIG = {
    "host": "127.0.0.1",
    "port": 3306,
    "user": "root",
    "password": "TanLiMing123!",
    "database": "linbang",
    "charset": "utf8mb4",
    "autocommit": True,
}


COMMISSION_ORDER_DDL = """
CREATE TABLE IF NOT EXISTS `lb_commission_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `commission_no` VARCHAR(32) NOT NULL COMMENT '佣金单号',
  `promoter_id` BIGINT NOT NULL COMMENT '推广员ID',
  `user_id` BIGINT NOT NULL COMMENT '被推广用户ID',
  `source_order_id` BIGINT DEFAULT NULL COMMENT '来源订单ID',
  `source_unit_id` BIGINT DEFAULT NULL COMMENT '来源订单单元ID',
  `commission_type` VARCHAR(32) NOT NULL COMMENT '佣金类型',
  `commission_amount` DECIMAL(18, 2) NOT NULL DEFAULT 0 COMMENT '佣金金额',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='佣金单表';
"""


PROMOTE_MENUS = [
    (110680, "推广中心", "", 1, 68, 110000, "promote", "ep:promotion", None, None, 0, 1, 1, 1),
    (110681, "推广中心概览", "", 2, 1, 110680, "index", "", "linbang/promote/index", "LinbangPromote", 0, 1, 1, 1),
    (110682, "推广员管理", "", 2, 2, 110680, "promoter", "", "linbang/promoter/index", "LinbangPromoter", 0, 1, 1, 1),
    (110683, "推广员详情", "linbang:promote:user:get", 3, 1, 110682, "", "", "", "", 0, 1, 1, 1),
    (110684, "佣金记录", "", 2, 3, 110680, "commission", "", "linbang/commissionorder/index", "LinbangCommissionOrder", 0, 1, 1, 1),
    (110685, "佣金记录详情", "linbang:promote:commission:get", 3, 1, 110684, "", "", "", "", 0, 1, 1, 1),
]

ROLE_MENU_RELATIONS = {
    1: [110680, 110681, 110682, 110683, 110684, 110685],
    20001: [110680, 110681, 110682, 110683, 110684, 110685],
    20004: [110680, 110681, 110682, 110683, 110684, 110685],
    20005: [110680, 110681, 110682, 110683, 110684, 110685],
}

SMS_CHANNEL = {
    "id": 900001,
    "signature": "邻里互助",
    "code": "ALIYUN",
    "status": 1,
    "remark": "本地联调短信渠道",
    "api_key": "local-debug-key",
    "api_secret": "local-debug-secret",
    "callback_url": "",
}

SMS_TEMPLATE = {
    "id": 900001,
    "type": 1,
    "status": 0,
    "code": "user-sms-login",
    "name": "App 登录注册验证码",
    "content": "您的验证码为{code}，5分钟内有效，请勿泄露给他人。",
    "params": '["code"]',
    "remark": "本地联调用模板，禁用真实发送，仅用于验证码链路校验",
    "api_template_id": "LOCAL_USER_SMS_LOGIN",
    "channel_id": 900001,
    "channel_code": "ALIYUN",
}


def upsert_menu(cursor, menu):
    cursor.execute(
        """
        INSERT INTO system_menu
        (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
         status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, 'admin', NOW(), 'admin', NOW(), b'0')
        ON DUPLICATE KEY UPDATE
          name = VALUES(name),
          permission = VALUES(permission),
          type = VALUES(type),
          sort = VALUES(sort),
          parent_id = VALUES(parent_id),
          path = VALUES(path),
          icon = VALUES(icon),
          component = VALUES(component),
          component_name = VALUES(component_name),
          status = VALUES(status),
          visible = VALUES(visible),
          keep_alive = VALUES(keep_alive),
          always_show = VALUES(always_show),
          updater = 'admin',
          update_time = NOW(),
          deleted = b'0'
        """,
        menu,
    )


def ensure_role_menu(cursor, role_id, menu_id):
    cursor.execute(
        "SELECT id FROM system_role_menu WHERE role_id = %s AND menu_id = %s AND deleted = b'0' LIMIT 1",
        (role_id, menu_id),
    )
    if cursor.fetchone():
        return
    cursor.execute(
        """
        INSERT INTO system_role_menu
        (role_id, menu_id, creator, create_time, updater, update_time, deleted, tenant_id)
        VALUES (%s, %s, 'admin', NOW(), 'admin', NOW(), b'0', 1)
        """,
        (role_id, menu_id),
    )


def upsert_sms_channel(cursor):
    cursor.execute(
        """
        INSERT INTO system_sms_channel
        (id, signature, code, status, remark, api_key, api_secret, callback_url,
         creator, create_time, updater, update_time, deleted)
        VALUES (%(id)s, %(signature)s, %(code)s, %(status)s, %(remark)s, %(api_key)s, %(api_secret)s, %(callback_url)s,
                'admin', NOW(), 'admin', NOW(), b'0')
        ON DUPLICATE KEY UPDATE
          signature = VALUES(signature),
          code = VALUES(code),
          status = VALUES(status),
          remark = VALUES(remark),
          api_key = VALUES(api_key),
          api_secret = VALUES(api_secret),
          callback_url = VALUES(callback_url),
          updater = 'admin',
          update_time = NOW(),
          deleted = b'0'
        """,
        SMS_CHANNEL,
    )




MERCHANT_INFO = {
    "id": 900101,
    "user_id": 1,
    "merchant_name": "local-test-merchant",
    "contact_name": "local-test-merchant",
    "contact_mobile": "13800138000",
    "service_scope_desc": "local integration test",
    "status": "ENABLE",
    "accept_status": "ENABLE",
    "credit_score": 100,
    "credit_level": "NORMAL",
}

PARTNER_INFO = {
    "id": 900201,
    "user_id": 1,
    "partner_name": "local-test-partner",
    "contact_name": "local-test-partner",
    "contact_mobile": "13800138000",
    "status": "ENABLE",
}


def upsert_merchant_info(cursor):
    cursor.execute(
        """
        INSERT INTO lb_merchant_info
        (id, user_id, merchant_name, contact_name, contact_mobile, service_scope_desc, status, accept_status,
         credit_score, credit_level, creator, create_time, updater, update_time, deleted, tenant_id)
        VALUES (%(id)s, %(user_id)s, %(merchant_name)s, %(contact_name)s, %(contact_mobile)s, %(service_scope_desc)s,
                %(status)s, %(accept_status)s, %(credit_score)s, %(credit_level)s, 'admin', NOW(), 'admin', NOW(), b'0', 1)
        ON DUPLICATE KEY UPDATE
          user_id = VALUES(user_id),
          merchant_name = VALUES(merchant_name),
          contact_name = VALUES(contact_name),
          contact_mobile = VALUES(contact_mobile),
          service_scope_desc = VALUES(service_scope_desc),
          status = VALUES(status),
          accept_status = VALUES(accept_status),
          credit_score = VALUES(credit_score),
          credit_level = VALUES(credit_level),
          updater = 'admin',
          update_time = NOW(),
          deleted = b'0'
        """,
        MERCHANT_INFO,
    )


def upsert_partner_info(cursor):
    cursor.execute(
        """
        INSERT INTO lb_partner_info
        (id, user_id, partner_name, contact_name, contact_mobile, status, creator, create_time, updater, update_time, deleted, tenant_id)
        VALUES (%(id)s, %(user_id)s, %(partner_name)s, %(contact_name)s, %(contact_mobile)s, %(status)s,
                'admin', NOW(), 'admin', NOW(), b'0', 1)
        ON DUPLICATE KEY UPDATE
          user_id = VALUES(user_id),
          partner_name = VALUES(partner_name),
          contact_name = VALUES(contact_name),
          contact_mobile = VALUES(contact_mobile),
          status = VALUES(status),
          updater = 'admin',
          update_time = NOW(),
          deleted = b'0'
        """,
        PARTNER_INFO,
    )


def upsert_sms_template(cursor):
    cursor.execute(
        """
        INSERT INTO system_sms_template
        (id, type, status, code, name, content, params, remark, api_template_id, channel_id, channel_code,
         creator, create_time, updater, update_time, deleted)
        VALUES (%(id)s, %(type)s, %(status)s, %(code)s, %(name)s, %(content)s, %(params)s, %(remark)s,
                %(api_template_id)s, %(channel_id)s, %(channel_code)s, 'admin', NOW(), 'admin', NOW(), b'0')
        ON DUPLICATE KEY UPDATE
          type = VALUES(type),
          status = VALUES(status),
          code = VALUES(code),
          name = VALUES(name),
          content = VALUES(content),
          params = VALUES(params),
          remark = VALUES(remark),
          api_template_id = VALUES(api_template_id),
          channel_id = VALUES(channel_id),
          channel_code = VALUES(channel_code),
          updater = 'admin',
          update_time = NOW(),
          deleted = b'0'
        """,
        SMS_TEMPLATE,
    )


def main():
    conn = pymysql.connect(**DB_CONFIG)
    try:
        with conn.cursor() as cursor:
            cursor.execute(COMMISSION_ORDER_DDL)
            for menu in PROMOTE_MENUS:
                upsert_menu(cursor, menu)
            for role_id, menu_ids in ROLE_MENU_RELATIONS.items():
                for menu_id in menu_ids:
                    ensure_role_menu(cursor, role_id, menu_id)
            upsert_sms_channel(cursor)
            upsert_merchant_info(cursor)
            upsert_partner_info(cursor)
            upsert_sms_template(cursor)
            cursor.execute("SELECT COUNT(*) FROM lb_commission_order")
            commission_count = cursor.fetchone()[0]
            cursor.execute("SELECT COUNT(*) FROM system_menu WHERE id BETWEEN 110680 AND 110685")
            menu_count = cursor.fetchone()[0]
            cursor.execute("SELECT id, status, channel_id FROM system_sms_template WHERE code = 'user-sms-login' LIMIT 1")
            sms_template = cursor.fetchone()
        print(
            {
                "lb_commission_order_ready": True,
                "commission_count": commission_count,
                "promote_menu_count": menu_count,
                "sms_template": sms_template,
            }
        )
    finally:
        conn.close()


if __name__ == "__main__":
    main()
