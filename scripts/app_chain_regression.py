import json
import os
from decimal import Decimal
from urllib import error, parse, request

import pymysql


BASE = os.environ.get("LINBANG_BASE", "http://127.0.0.1:48080")
TENANT_ID = "1"
ADMIN_USERNAME = "admin"
ADMIN_PASSWORD = "admin123"
PROMOTER_TOKEN = "test1"
CONSUMER_TOKEN = "test3"
MERCHANT_TOKEN = "test2"
ADMIN_DYNAMIC_KEY_PASSWORD = "linbang@2026"
ADMIN_DYNAMIC_KEY_HEADER = "X-Linbang-Dynamic-Key-Token"

DB_CONFIG = {
    "host": "127.0.0.1",
    "port": 3306,
    "user": "root",
    "password": "TanLiMing123!",
    "database": "linbang",
    "charset": "utf8mb4",
    "autocommit": True,
}

PROMOTER_USER_ID = 1
CONSUMER_USER_ID = 3
MERCHANT_USER_ID = 2
MERCHANT_INFO_ID = 900102
PARTNER_REL_ID = 900301
FILE_ID = 2260
CONSUMER_WALLET_ID = 3
MERCHANT_WALLET_ID = 2


def http_request(method, path, token, payload=None, params=None, allow_error=False, extra_headers=None):
    url = BASE + path
    if params:
        url += "?" + parse.urlencode(params, doseq=True)
    body = None
    headers = {
        "Authorization": f"Bearer {token}",
        "tenant-id": TENANT_ID,
    }
    if extra_headers:
        headers.update(extra_headers)
    if payload is not None:
        body = json.dumps(payload, ensure_ascii=False, default=str).encode("utf-8")
        headers["Content-Type"] = "application/json; charset=utf-8"
        headers["Content-Length"] = str(len(body))
    req = request.Request(url, data=body, headers=headers, method=method)
    try:
        with request.urlopen(req, timeout=30) as resp:
            text = resp.read().decode("utf-8")
    except error.HTTPError as exc:
        text = exc.read().decode("utf-8")
        if not allow_error:
            raise RuntimeError(f"{method} {path} http {exc.code}: {text}") from exc
    data = json.loads(text)
    if not allow_error and data.get("code") != 0:
        raise RuntimeError(f"{method} {path} failed: {data}")
    return data


def jget(path, token, params=None, allow_error=False):
    return http_request("GET", path, token, params=params, allow_error=allow_error)


def jpost(path, token, payload, allow_error=False, extra_headers=None):
    return http_request(
        "POST",
        path,
        token,
        payload=payload,
        allow_error=allow_error,
        extra_headers=extra_headers,
    )


def admin_login():
    req = request.Request(
        BASE + "/admin-api/system/auth/login",
        data=json.dumps({"username": ADMIN_USERNAME, "password": ADMIN_PASSWORD}).encode("utf-8"),
        headers={"Content-Type": "application/json; charset=utf-8"},
        method="POST",
    )
    with request.urlopen(req, timeout=30) as resp:
        data = json.loads(resp.read().decode("utf-8"))
    if data.get("code") != 0:
        raise RuntimeError(f"admin login failed: {data}")
    return data["data"]["accessToken"]


def admin_get(path, token, params=None, allow_error=False):
    return http_request("GET", path, token, params=params, allow_error=allow_error)


def admin_post(path, token, payload, allow_error=False, extra_headers=None):
    return http_request(
        "POST",
        path,
        token,
        payload=payload,
        allow_error=allow_error,
        extra_headers=extra_headers,
    )


def admin_verify_dynamic_key(token):
    resp = admin_post(
        "/admin-api/security/dynamic-key/verify",
        token,
        {"password": ADMIN_DYNAMIC_KEY_PASSWORD},
    )["data"]
    return resp["verifyToken"]


def ensure_local_data():
    conn = pymysql.connect(**DB_CONFIG)
    try:
        with conn.cursor() as cur:
            cur.execute(
                """
                CREATE TABLE IF NOT EXISTS lb_promoter_relation (
                  id BIGINT NOT NULL AUTO_INCREMENT,
                  promoter_id BIGINT NOT NULL,
                  user_id BIGINT NOT NULL,
                  bind_time DATETIME DEFAULT NULL,
                  first_order_id BIGINT DEFAULT NULL,
                  convert_status VARCHAR(32) DEFAULT NULL,
                  tenant_id BIGINT NOT NULL DEFAULT 1,
                  creator VARCHAR(64) DEFAULT '',
                  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  updater VARCHAR(64) DEFAULT '',
                  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                  deleted BIT(1) NOT NULL DEFAULT b'0',
                  PRIMARY KEY (id),
                  UNIQUE KEY uk_lb_promoter_relation_promoter_user (promoter_id, user_id),
                  KEY idx_lb_promoter_relation_user_id (user_id),
                  KEY idx_lb_promoter_relation_tenant_id (tenant_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """
            )
            cur.execute(
                """
                INSERT INTO lb_user
                (id, user_no, mobile, nickname, register_source, current_role_code, status,
                 creator, create_time, updater, update_time, deleted, tenant_id)
                VALUES
                (%s, %s, %s, %s, 'LOCAL_TEST', 'USER', 'ENABLE',
                 'admin', NOW(), 'admin', NOW(), b'0', 1)
                ON DUPLICATE KEY UPDATE
                  mobile = VALUES(mobile),
                  nickname = VALUES(nickname),
                  current_role_code = VALUES(current_role_code),
                  status = VALUES(status),
                  updater = 'admin',
                  update_time = NOW(),
                  deleted = b'0'
                """,
                (CONSUMER_USER_ID, "LBU_LOCAL_TEST_3", "13800138002", "consumer3"),
            )
            cur.execute(
                """
                INSERT INTO lb_user
                (id, user_no, mobile, nickname, register_source, current_role_code, status,
                 creator, create_time, updater, update_time, deleted, tenant_id)
                VALUES
                (%s, %s, %s, %s, 'LOCAL_TEST', 'MERCHANT', 'ENABLE',
                 'admin', NOW(), 'admin', NOW(), b'0', 1)
                ON DUPLICATE KEY UPDATE
                  mobile = VALUES(mobile),
                  nickname = VALUES(nickname),
                  current_role_code = VALUES(current_role_code),
                  status = VALUES(status),
                  updater = 'admin',
                  update_time = NOW(),
                  deleted = b'0'
                """,
                (MERCHANT_USER_ID, "LBU_LOCAL_TEST_2", "13800138001", "merchant2"),
            )
            cur.execute(
                """
                INSERT INTO lb_merchant_info
                (id, user_id, merchant_name, contact_name, contact_mobile, service_scope_desc,
                 status, accept_status, credit_score, credit_level,
                 creator, create_time, updater, update_time, deleted, tenant_id)
                VALUES
                (%s, %s, %s, %s, %s, %s, 'ENABLE', 'ENABLE', 100, 'NORMAL',
                 'admin', NOW(), 'admin', NOW(), b'0', 1)
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
                (
                    MERCHANT_INFO_ID,
                    MERCHANT_USER_ID,
                    "local-test-merchant-2",
                    "merchant2",
                    "13800138001",
                    "local merchant two",
                ),
            )
            cur.execute(
                """
                INSERT INTO lb_merchant_entry
                (merchant_id, user_id, entry_no, region_code,
                 first_audit_status, first_audit_by, first_audit_time,
                 final_audit_status, final_audit_by, final_audit_time,
                 status, remark, tenant_id, creator, create_time, updater, update_time, deleted)
                SELECT %s, %s, 'LBE_LOCAL_TEST_2', adcode,
                       'APPROVED', 1, NOW(),
                       'APPROVED', 1, NOW(),
                       'APPROVED', 'local approved entry', 1,
                       'admin', NOW(), 'admin', NOW(), b'0'
                FROM lb_user_address
                WHERE user_id = %s
                  AND NOT EXISTS (
                    SELECT 1 FROM lb_merchant_entry
                    WHERE merchant_id = %s AND deleted = b'0'
                  )
                LIMIT 1
                """,
                (MERCHANT_INFO_ID, MERCHANT_USER_ID, PROMOTER_USER_ID, MERCHANT_INFO_ID),
            )
            cur.execute(
                """
                INSERT INTO lb_merchant_service_point
                (merchant_id, point_name, province, city, district, street, detail_address,
                 longitude, latitude, service_radius_km, status,
                 creator, create_time, updater, update_time, deleted)
                SELECT %s, 'point-2', province, city, district, street, detail_address,
                       longitude, latitude, 20.00, 'ENABLE',
                       'admin', NOW(), 'admin', NOW(), b'0'
                FROM lb_user_address
                WHERE user_id = %s
                  AND NOT EXISTS (
                    SELECT 1 FROM lb_merchant_service_point
                    WHERE merchant_id = %s AND deleted = b'0'
                  )
                LIMIT 1
                """,
                (MERCHANT_INFO_ID, PROMOTER_USER_ID, MERCHANT_INFO_ID),
            )
            cur.execute(
                """
                INSERT INTO lb_wallet_account
                (user_id, role_code, total_amount, available_amount, frozen_amount, escrow_amount,
                 commission_amount, status, tenant_id, creator, create_time, updater, update_time, deleted)
                VALUES
                (%s, 'USER', 300.00, 300.00, 0.00, 0.00, 0.00, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0')
                ON DUPLICATE KEY UPDATE
                  total_amount = GREATEST(total_amount, 300.00),
                  available_amount = GREATEST(available_amount, 300.00),
                  status = 'ENABLE',
                  updater = 'admin',
                  update_time = NOW(),
                  deleted = b'0'
                """,
                (CONSUMER_USER_ID,),
            )
            cur.execute(
                """
                INSERT INTO lb_wallet_account
                (user_id, role_code, total_amount, available_amount, frozen_amount, escrow_amount,
                 commission_amount, status, tenant_id, creator, create_time, updater, update_time, deleted)
                VALUES
                (%s, 'MERCHANT', 500.00, 500.00, 0.00, 0.00, 0.00, 'ENABLE', 1, 'admin', NOW(), 'admin', NOW(), b'0')
                ON DUPLICATE KEY UPDATE
                  total_amount = GREATEST(total_amount, 500.00),
                  available_amount = GREATEST(available_amount, 500.00),
                  status = 'ENABLE',
                  updater = 'admin',
                  update_time = NOW(),
                  deleted = b'0'
                """,
                (MERCHANT_USER_ID,),
            )
            cur.execute(
                """
                CREATE TABLE IF NOT EXISTS pay_wallet (
                  id BIGINT NOT NULL AUTO_INCREMENT,
                  user_id BIGINT NOT NULL,
                  user_type TINYINT NOT NULL,
                  balance INT NOT NULL DEFAULT 0,
                  freeze_price INT NOT NULL DEFAULT 0,
                  total_expense INT NOT NULL DEFAULT 0,
                  total_recharge INT NOT NULL DEFAULT 0,
                  creator VARCHAR(64) DEFAULT '',
                  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  updater VARCHAR(64) DEFAULT '',
                  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                  deleted BIT(1) NOT NULL DEFAULT b'0',
                  PRIMARY KEY (id),
                  UNIQUE KEY uk_pay_wallet_user (user_id, user_type)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """
            )
            cur.execute(
                """
                CREATE TABLE IF NOT EXISTS pay_wallet_transaction (
                  id BIGINT NOT NULL AUTO_INCREMENT,
                  no VARCHAR(64) NOT NULL,
                  wallet_id BIGINT NOT NULL,
                  biz_type INT NOT NULL,
                  biz_id VARCHAR(64) NOT NULL,
                  title VARCHAR(255) NOT NULL,
                  price INT NOT NULL,
                  balance INT NOT NULL,
                  creator VARCHAR(64) DEFAULT '',
                  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                  updater VARCHAR(64) DEFAULT '',
                  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                  deleted BIT(1) NOT NULL DEFAULT b'0',
                  PRIMARY KEY (id),
                  UNIQUE KEY uk_pay_wallet_tx_no (no)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """
            )
            cur.execute(
                """
                INSERT INTO pay_wallet
                (id, user_id, user_type, balance, freeze_price, total_expense, total_recharge,
                 creator, create_time, updater, update_time, deleted)
                VALUES
                (%s, %s, 1, 200000, 0, 0, 200000, 'admin', NOW(), 'admin', NOW(), b'0')
                ON DUPLICATE KEY UPDATE
                  balance = GREATEST(balance, 200000),
                  total_recharge = GREATEST(total_recharge, 200000),
                  updater = 'admin',
                  update_time = NOW(),
                  deleted = b'0'
                """,
                (MERCHANT_WALLET_ID, MERCHANT_USER_ID),
            )
            cur.execute(
                """
                INSERT INTO pay_wallet
                (id, user_id, user_type, balance, freeze_price, total_expense, total_recharge,
                 creator, create_time, updater, update_time, deleted)
                VALUES
                (%s, %s, 1, 500000, 0, 0, 500000, 'admin', NOW(), 'admin', NOW(), b'0')
                ON DUPLICATE KEY UPDATE
                  balance = GREATEST(balance, 500000),
                  total_recharge = GREATEST(total_recharge, 500000),
                  updater = 'admin',
                  update_time = NOW(),
                  deleted = b'0'
                """,
                (CONSUMER_WALLET_ID, CONSUMER_USER_ID),
            )
            cur.execute(
                """
                INSERT INTO infra_file
                (id, config_id, name, path, url, type, size, creator, create_time, updater, update_time, deleted)
                VALUES
                (%s, 1, 'proof.jpg', '/local/proof.jpg', 'http://127.0.0.1/static/proof.jpg',
                 'image/jpeg', 128, 'admin', NOW(), 'admin', NOW(), b'0')
                ON DUPLICATE KEY UPDATE
                  name = VALUES(name),
                  path = VALUES(path),
                  url = VALUES(url),
                  type = VALUES(type),
                  size = VALUES(size),
                  updater = 'admin',
                  update_time = NOW(),
                  deleted = b'0'
                """,
                (FILE_ID,),
            )
            cur.execute(
                """
                INSERT INTO lb_partner_region_rel
                (id, partner_id, province, city, district, adcode, status,
                 tenant_id, creator, create_time, updater, update_time, deleted)
                SELECT %s, 900201, province, city, district, adcode, 'ENABLE',
                       1, 'admin', NOW(), 'admin', NOW(), b'0'
                FROM lb_user_address
                WHERE user_id = %s
                ON DUPLICATE KEY UPDATE
                  province = VALUES(province),
                  city = VALUES(city),
                  district = VALUES(district),
                  adcode = VALUES(adcode),
                  status = VALUES(status),
                  updater = 'admin',
                  update_time = NOW(),
                  deleted = b'0'
                """,
                (PARTNER_REL_ID, PROMOTER_USER_ID),
            )
            cur.execute(
                """
                INSERT INTO lb_user_address
                (user_id, receiver_name, receiver_mobile, province, city, district, street, detail_address,
                 longitude, latitude, adcode, is_default, tenant_id, creator, create_time, updater, update_time, deleted)
                SELECT %s, 'consumer3', '13800138002', province, city, district, street, 'Lane 3',
                       longitude, latitude, adcode, b'1', 1, 'admin', NOW(), 'admin', NOW(), b'0'
                FROM lb_user_address
                WHERE user_id = %s
                  AND NOT EXISTS (
                    SELECT 1 FROM lb_user_address
                    WHERE user_id = %s AND deleted = b'0'
                  )
                LIMIT 1
                """,
                (CONSUMER_USER_ID, PROMOTER_USER_ID, CONSUMER_USER_ID),
            )
            cur.execute(
                """
                UPDATE pay_app
                SET status = 0,
                    order_notify_url = 'http://127.0.0.1:48080/app-api/linbang/pay/order/update-paid'
                WHERE id = 1
                """
            )
            cur.execute("UPDATE pay_channel SET status = 0 WHERE code IN ('mock', 'wallet')")
    finally:
        conn.close()


def ensure_address(token, receiver_name, receiver_mobile, detail_address):
    address_page = jget("/app-api/member/address/page", token)["data"]
    if address_page.get("list"):
        return address_page["list"][0]["id"]
    return jpost(
        "/app-api/member/address/create",
        token,
        {
            "name": receiver_name,
            "mobile": receiver_mobile,
            "provinceId": 310000,
            "cityId": 310100,
            "districtId": 310115,
            "detailAddress": detail_address,
            "defaultStatus": True,
            "longitude": Decimal("121.599960"),
            "latitude": Decimal("31.202120"),
        },
    )["data"]


def ensure_category():
    categories = jget("/app-api/merchant/service-category/list", PROMOTER_TOKEN)["data"]
    if not categories:
        raise RuntimeError("No service category available")
    preferred = [item for item in categories if item.get("id") == 340201]
    if preferred:
        return preferred[0]["id"]
    return categories[0]["id"]


def ensure_bank_card(token, bank_name, card_no, account_name, reserved_mobile):
    card_page = jget("/app-api/wallet/bank-card/page", token, {"pageNo": 1, "pageSize": 20})["data"]
    if card_page.get("list"):
        return card_page["list"][0]["id"]
    return jpost(
        "/app-api/wallet/bank-card/create",
        token,
        {
            "bankName": bank_name,
            "bankCode": "ICBC",
            "cardNo": card_no,
            "accountName": account_name,
            "reservedMobile": reserved_mobile,
            "isDefault": True,
        },
    )["data"]


def trigger_paid_callback(order_id, pay_order_id):
    return jpost(
        "/app-api/linbang/pay/order/update-paid",
        CONSUMER_TOKEN,
        {"merchantOrderId": str(order_id), "payOrderId": pay_order_id},
        allow_error=False,
    )


def main():
    ensure_local_data()

    out = {
        "base": BASE,
        "tokens": {
            "admin": None,
            "promoter": PROMOTER_TOKEN,
            "consumer": CONSUMER_TOKEN,
            "merchant": MERCHANT_TOKEN,
        },
    }
    admin_token = admin_login()
    out["tokens"]["admin"] = admin_token
    dynamic_key_token = admin_verify_dynamic_key(admin_token)
    out["admin_dynamic_key_token"] = dynamic_key_token

    out["promoter_profile"] = jget("/app-api/member/user/profile", PROMOTER_TOKEN)["data"]
    out["consumer_profile"] = jget("/app-api/member/user/profile", CONSUMER_TOKEN)["data"]
    out["merchant_profile"] = jget("/app-api/member/user/profile", MERCHANT_TOKEN)["data"]
    out["real_name_submit_id"] = jpost(
        "/app-api/member/real-name/create",
        CONSUMER_TOKEN,
        {
            "realName": "Consumer Three",
            "idCardNo": "310101199001011239",
            "idCardFrontFileId": FILE_ID,
            "idCardBackFileId": FILE_ID,
            "holdCardFileId": FILE_ID,
        },
    )["data"]
    out["real_name_app_get_before_audit"] = jget("/app-api/member/real-name/get", CONSUMER_TOKEN)["data"]
    out["qualification_submit_id"] = jpost(
        "/app-api/member/qualification/create",
        CONSUMER_TOKEN,
        {
            "qualificationType": "ELECTRICIAN",
            "qualificationName": "电工证",
            "qualificationNo": "CERT-LOCAL-2026-001",
            "fileId": FILE_ID,
            "validStartDate": "2026-01-01",
            "validEndDate": "2028-12-31",
        },
    )["data"]
    out["qualification_app_page_before_audit"] = jget("/app-api/member/qualification/page", CONSUMER_TOKEN)["data"]
    out["admin_real_name_page_before_audit"] = admin_get(
        "/admin-api/member/real-name/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10, "auditStatus": "PENDING"},
    )["data"]
    out["admin_real_name_detail_before_audit"] = admin_get(
        "/admin-api/member/real-name/get",
        admin_token,
        {"id": out["real_name_submit_id"]},
    )["data"]
    out["admin_qualification_page_before_audit"] = admin_get(
        "/admin-api/member/qualification/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10, "auditStatus": "PENDING"},
    )["data"]
    out["admin_qualification_detail_before_audit"] = admin_get(
        "/admin-api/member/qualification/get",
        admin_token,
        {"id": out["qualification_submit_id"]},
    )["data"]
    out["admin_real_name_audit"] = admin_post(
        "/admin-api/member/real-name/audit",
        admin_token,
        {
            "id": out["real_name_submit_id"],
            "auditStatus": "APPROVED",
            "auditRemark": "local regression approved",
        },
    )["data"]
    out["admin_qualification_audit"] = admin_post(
        "/admin-api/member/qualification/audit",
        admin_token,
        {
            "id": out["qualification_submit_id"],
            "auditStatus": "APPROVED",
            "auditRemark": "local regression approved",
        },
    )["data"]
    out["real_name_app_get_after_audit"] = jget("/app-api/member/real-name/get", CONSUMER_TOKEN)["data"]
    out["qualification_app_page_after_audit"] = jget("/app-api/member/qualification/page", CONSUMER_TOKEN)["data"]
    out["admin_real_name_page_after_audit"] = admin_get(
        "/admin-api/member/real-name/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_qualification_page_after_audit"] = admin_get(
        "/admin-api/member/qualification/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["invite_code"] = jget("/app-api/promote/invite-code/get", PROMOTER_TOKEN)["data"]
    out["bind_invite_code"] = jpost(
        "/app-api/promote/invite-code/bind",
        CONSUMER_TOKEN,
        {"inviteCode": out["invite_code"]["inviteCode"]},
    )["data"]

    address_id = ensure_address(CONSUMER_TOKEN, "consumer3", "13800138002", "Lane 3")
    category_id = ensure_category()
    out["address_id"] = address_id
    out["category_id"] = category_id

    order_id = jpost(
        "/app-api/order/info/create",
        CONSUMER_TOKEN,
        {
            "categoryId": category_id,
            "title": "local order three roles",
            "pricingMode": "FIXED",
            "budgetAmount": Decimal("88.80"),
            "quantity": Decimal("1"),
            "serviceDurationDesc": "same day",
            "requireDesc": "local integration test order with promoter consumer merchant roles",
            "addressId": address_id,
            "needInvoice": False,
            "needSplit": True,
            "agreementConfirmed": True,
            "attachmentFileIds": [],
            "priceItems": [
                {
                    "itemType": "BASE",
                    "itemName": "base service",
                    "itemAmount": Decimal("88.80"),
                    "sortNo": 1,
                }
            ],
        },
    )["data"]
    out["order_id"] = order_id

    order_before_pay = jget("/app-api/order/info/get", CONSUMER_TOKEN, {"id": order_id})["data"]
    unit_id = order_before_pay["units"][0]["id"]
    out["unit_id"] = unit_id
    out["order_before_pay"] = order_before_pay

    pay_order_id = jpost("/app-api/linbang/pay/order/create", CONSUMER_TOKEN, {"orderId": order_id})["data"]
    out["pay_order_id"] = pay_order_id
    out["pay_order_before_submit"] = jget("/app-api/linbang/pay/order/get", CONSUMER_TOKEN, {"id": pay_order_id})["data"]
    out["wallet_before"] = jget("/app-api/pay/wallet/get", CONSUMER_TOKEN)["data"]
    out["pay_submit"] = jpost(
        "/app-api/pay/order/submit",
        CONSUMER_TOKEN,
        {"id": pay_order_id, "channelCode": "wallet"},
    )["data"]
    out["pay_order_after_submit"] = jget(
        "/app-api/linbang/pay/order/get",
        CONSUMER_TOKEN,
        {"id": pay_order_id, "sync": True},
    )["data"]

    order_after_submit = jget("/app-api/order/info/get", CONSUMER_TOKEN, {"id": order_id})["data"]
    out["order_after_submit"] = order_after_submit
    if order_after_submit["status"] != "PENDING_ACCEPT":
        out["manual_callback"] = trigger_paid_callback(order_id, pay_order_id)
    order_after_paid = jget("/app-api/order/info/get", CONSUMER_TOKEN, {"id": order_id})["data"]
    out["order_after_paid"] = order_after_paid

    out["merchant_accept_status_before"] = jget("/app-api/merchant/info/accept-status/get", MERCHANT_TOKEN)["data"]
    out["accept_result"] = jpost(
        "/app-api/order/accept/create",
        MERCHANT_TOKEN,
        {"orderId": order_id, "unitId": unit_id},
    )["data"]
    out["order_after_accept"] = jget("/app-api/order/info/get", MERCHANT_TOKEN, {"id": order_id})["data"]

    out["start_service_result"] = jpost(
        "/app-api/order/unit/start-service",
        MERCHANT_TOKEN,
        {"unitId": unit_id, "startRemark": "merchant start service"},
    )["data"]
    out["order_after_start"] = jget("/app-api/order/info/get", MERCHANT_TOKEN, {"id": order_id})["data"]

    out["upload_proof_result"] = jpost(
        "/app-api/order/unit/upload-delivery-proof",
        MERCHANT_TOKEN,
        {
            "unitId": unit_id,
            "proofType": "IMAGE",
            "proofDesc": "proof upload",
            "fileIds": [FILE_ID],
            "longitude": Decimal("121.599960"),
            "latitude": Decimal("31.202120"),
        },
    )["data"]
    out["order_after_proof"] = jget("/app-api/order/info/get", CONSUMER_TOKEN, {"id": order_id})["data"]

    out["confirm_result"] = jpost(
        "/app-api/order/unit/confirm",
        CONSUMER_TOKEN,
        {"unitId": unit_id, "confirmRemark": "user confirm finish"},
    )["data"]
    out["order_after_finish"] = jget("/app-api/order/info/get", CONSUMER_TOKEN, {"id": order_id})["data"]

    out["promote_center"] = jget("/app-api/promote/center/get", PROMOTER_TOKEN)["data"]
    out["commission_page"] = jget(
        "/app-api/promote/commission/page",
        PROMOTER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["partner_workbench"] = jget("/app-api/partner/workbench/get", PROMOTER_TOKEN)["data"]

    out["app_wallet_account_before_withdraw"] = jget("/app-api/wallet/account/get", CONSUMER_TOKEN)["data"]
    bank_card_id = ensure_bank_card(
        CONSUMER_TOKEN,
        "中国工商银行",
        "6222022024000001234",
        "Consumer Three",
        "13800138002",
    )
    out["bank_card_id"] = bank_card_id
    out["app_bank_card_page"] = jget(
        "/app-api/wallet/bank-card/page",
        CONSUMER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    withdraw_id = jpost(
        "/app-api/wallet/withdraw/create",
        CONSUMER_TOKEN,
        {
            "bankCardId": bank_card_id,
            "applyAmount": Decimal("88.00"),
        },
    )["data"]
    out["withdraw_id"] = withdraw_id
    out["app_withdraw_page_before_audit"] = jget(
        "/app-api/wallet/withdraw/page",
        CONSUMER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["app_wallet_account_after_withdraw_apply"] = jget("/app-api/wallet/account/get", CONSUMER_TOKEN)["data"]
    out["app_wallet_flow_page"] = jget(
        "/app-api/wallet/flow/page",
        CONSUMER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_wallet_account_page"] = admin_get(
        "/admin-api/wallet/account/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_wallet_bank_card_page"] = admin_get(
        "/admin-api/wallet/bank-card/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_wallet_withdraw_page_before_audit"] = admin_get(
        "/admin-api/wallet/withdraw/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_wallet_withdraw_detail_before_audit"] = admin_get(
        "/admin-api/wallet/withdraw/get",
        admin_token,
        {"id": withdraw_id},
    )["data"]
    out["admin_wallet_withdraw_audit"] = admin_post(
        "/admin-api/wallet/withdraw/audit",
        admin_token,
        {
            "id": withdraw_id,
            "auditStatus": "APPROVED",
            "auditRemark": "local regression approved",
        },
        extra_headers={ADMIN_DYNAMIC_KEY_HEADER: dynamic_key_token},
    )["data"]
    out["app_withdraw_page_after_audit"] = jget(
        "/app-api/wallet/withdraw/page",
        CONSUMER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_wallet_withdraw_page_after_audit"] = admin_get(
        "/admin-api/wallet/withdraw/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]

    review_id = jpost(
        "/app-api/review/comment/create",
        CONSUMER_TOKEN,
        {
            "orderId": order_id,
            "unitId": unit_id,
            "toUserId": MERCHANT_USER_ID,
            "starLevel": 5,
            "content": "local regression positive review",
        },
    )["data"]
    out["review_id"] = review_id
    out["app_review_page"] = jget(
        "/app-api/review/comment/page",
        CONSUMER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_review_comment_page"] = admin_get(
        "/admin-api/review/comment/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_review_comment_detail"] = admin_get(
        "/admin-api/review/comment/get",
        admin_token,
        {"id": review_id},
    )["data"]

    complaint_id = jpost(
        "/app-api/review/complaint/create",
        CONSUMER_TOKEN,
        {
            "orderId": order_id,
            "unitId": unit_id,
            "respondentUserId": MERCHANT_USER_ID,
            "complaintType": "SERVICE_DISPUTE",
            "content": "local regression complaint",
            "fileIds": [FILE_ID],
        },
    )["data"]
    out["complaint_id"] = complaint_id
    out["app_complaint_page_before_process"] = jget(
        "/app-api/review/complaint/page",
        CONSUMER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_complaint_page_before_process"] = admin_get(
        "/admin-api/review/complaint/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_complaint_detail_before_process"] = admin_get(
        "/admin-api/review/complaint/get",
        admin_token,
        {"id": complaint_id},
    )["data"]
    out["admin_complaint_process"] = admin_post(
        "/admin-api/review/complaint/process",
        admin_token,
        {
            "id": complaint_id,
            "status": "FINISHED",
            "resultDesc": "local regression complaint processed",
        },
    )["data"]
    out["app_complaint_page_after_process"] = jget(
        "/app-api/review/complaint/page",
        CONSUMER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_complaint_page_after_process"] = admin_get(
        "/admin-api/review/complaint/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]

    appeal_id = jpost(
        "/app-api/review/appeal/create",
        CONSUMER_TOKEN,
        {
            "orderId": order_id,
            "unitId": unit_id,
            "appealType": "RESULT_DISPUTE",
            "content": "local regression appeal",
            "fileIds": [FILE_ID],
        },
    )["data"]
    out["appeal_id"] = appeal_id
    out["order_after_appeal_create"] = jget("/app-api/order/info/get", CONSUMER_TOKEN, {"id": order_id})["data"]
    out["app_appeal_page_before_audit"] = jget(
        "/app-api/review/appeal/page",
        CONSUMER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_appeal_page_before_audit"] = admin_get(
        "/admin-api/review/appeal/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_appeal_detail_before_audit"] = admin_get(
        "/admin-api/review/appeal/get",
        admin_token,
        {"id": appeal_id},
    )["data"]
    out["admin_appeal_audit"] = admin_post(
        "/admin-api/review/appeal/audit",
        admin_token,
        {
            "id": appeal_id,
            "auditStatus": "APPROVED",
            "auditRemark": "local regression appeal approved",
        },
    )["data"]
    out["app_appeal_page_after_audit"] = jget(
        "/app-api/review/appeal/page",
        CONSUMER_TOKEN,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["admin_appeal_page_after_audit"] = admin_get(
        "/admin-api/review/appeal/page",
        admin_token,
        {"pageNo": 1, "pageSize": 10},
    )["data"]
    out["order_after_appeal_audit"] = jget("/app-api/order/info/get", CONSUMER_TOKEN, {"id": order_id})["data"]

    print(json.dumps(out, ensure_ascii=False, indent=2, default=str))


if __name__ == "__main__":
    main()
