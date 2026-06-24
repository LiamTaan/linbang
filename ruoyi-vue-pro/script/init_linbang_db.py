#!/usr/bin/env python
from __future__ import annotations

import argparse
from pathlib import Path

import mysql.connector


WORKSPACE_ROOT = Path(__file__).resolve().parents[2]
DEFAULT_SQL_FILES = [
    WORKSPACE_ROOT / "ruoyi-vue-pro" / "sql" / "mysql" / "ruoyi-vue-pro.sql",
    WORKSPACE_ROOT / "ruoyi-vue-pro" / "sql" / "mysql" / "quartz.sql",
    WORKSPACE_ROOT / "ruoyi-vue-pro" / "sql" / "mysql" / "01_lb_business_schema.sql",
    WORKSPACE_ROOT / "ruoyi-vue-pro" / "sql" / "mysql" / "02_lb_pay_extend.sql",
    WORKSPACE_ROOT / "ruoyi-vue-pro" / "sql" / "mysql" / "03_lb_seed_data.sql",
    WORKSPACE_ROOT / "ruoyi-vue-pro" / "sql" / "mysql" / "04_lb_admin_seed.sql",
]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Initialize the local linbang database.")
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", default=3306, type=int)
    parser.add_argument("--user", default="root")
    parser.add_argument("--password", default="TanLiMing123!")
    parser.add_argument("--database", default="linbang")
    parser.add_argument(
        "--skip-recreate",
        action="store_true",
        help="Skip dropping and recreating the target database.",
    )
    return parser.parse_args()


def execute_sql_file(connection: mysql.connector.MySQLConnection, sql_path: Path) -> None:
    if not sql_path.exists():
        print(f"[SKIP] missing SQL file: {sql_path}")
        return
    print(f"[RUN ] {sql_path}")
    sql = sql_path.read_text(encoding="utf-8")
    for _result in connection.cmd_query_iter(sql):
        pass
    connection.commit()


def main() -> None:
    args = parse_args()
    admin_connection = mysql.connector.connect(
        host=args.host,
        port=args.port,
        user=args.user,
        password=args.password,
        use_pure=True,
        charset="utf8mb4",
        collation="utf8mb4_unicode_ci",
        autocommit=False,
    )
    with admin_connection.cursor() as cursor:
        if not args.skip_recreate:
            print(f"[DDL ] recreate database `{args.database}`")
            cursor.execute(f"DROP DATABASE IF EXISTS `{args.database}`")
            cursor.execute(
                f"CREATE DATABASE `{args.database}` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
            )
        else:
            print(f"[DDL ] ensure database `{args.database}` exists")
            cursor.execute(
                f"CREATE DATABASE IF NOT EXISTS `{args.database}` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
            )
    admin_connection.commit()
    admin_connection.close()

    app_connection = mysql.connector.connect(
        host=args.host,
        port=args.port,
        user=args.user,
        password=args.password,
        database=args.database,
        use_pure=True,
        charset="utf8mb4",
        collation="utf8mb4_unicode_ci",
        autocommit=False,
    )
    try:
        for sql_file in DEFAULT_SQL_FILES:
            execute_sql_file(app_connection, sql_file)
    finally:
        app_connection.close()
    print("[DONE] linbang database initialized")


if __name__ == "__main__":
    main()
