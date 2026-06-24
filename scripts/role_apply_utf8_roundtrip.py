import json
import urllib.request

import pymysql


API_BASE = "http://127.0.0.1:48080"
HEADERS = {
    "Authorization": "Bearer test1",
    "tenant-id": "1",
    "Content-Type": "application/json; charset=utf-8",
}
DB_CONFIG = {
    "host": "127.0.0.1",
    "port": 3306,
    "user": "root",
    "password": "TanLiMing123!",
    "database": "linbang",
    "charset": "utf8mb4",
    "autocommit": True,
}


def post_json(path, payload):
    req = urllib.request.Request(
        API_BASE + path,
        data=json.dumps(payload, ensure_ascii=False).encode("utf-8"),
        headers=HEADERS,
        method="POST",
    )
    with urllib.request.urlopen(req, timeout=20) as resp:
        return json.loads(resp.read().decode("utf-8"))


def get_json(path):
    req = urllib.request.Request(API_BASE + path, headers=HEADERS)
    with urllib.request.urlopen(req, timeout=20) as resp:
        return json.loads(resp.read().decode("utf-8"))


def repair_bad_row():
    conn = pymysql.connect(**DB_CONFIG)
    try:
        with conn.cursor() as cursor:
            cursor.execute(
                """
                UPDATE lb_user_role_apply
                SET apply_reason = %s,
                    resource_desc = %s
                WHERE id = %s
                """,
                ("本地社群推广", "3个微信群和线下推广点", 1),
            )
            cursor.execute(
                """
                SELECT id, apply_reason, resource_desc
                FROM lb_user_role_apply
                WHERE id = %s
                """,
                (1,),
            )
            return cursor.fetchone()
    finally:
        conn.close()


def main():
    repaired = repair_bad_row()
    create_resp = post_json(
        "/app-api/member/role-apply/create",
        {
            "applyRoleCode": "PARTNER",
            "applyReason": "负责本地社区协同",
            "resourceDesc": "覆盖2个街道和物业协作资源",
        },
    )
    page_resp = get_json("/app-api/member/role-apply/page?pageNo=1&pageSize=10")
    detail_id = create_resp.get("data")
    detail_resp = get_json(f"/app-api/member/role-apply/get?id={detail_id}") if detail_id else None
    print(
        json.dumps(
            {
                "repaired_row": repaired,
                "create_resp": create_resp,
                "page_resp": page_resp,
                "detail_resp": detail_resp,
            },
            ensure_ascii=False,
        )
    )


if __name__ == "__main__":
    main()
