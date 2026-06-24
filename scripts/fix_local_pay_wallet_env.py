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


PAY_WALLET_DDL = """
CREATE TABLE IF NOT EXISTS `pay_wallet` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `user_type` TINYINT NOT NULL,
  `balance` INT NOT NULL DEFAULT 0,
  `freeze_price` INT NOT NULL DEFAULT 0,
  `total_expense` INT NOT NULL DEFAULT 0,
  `total_recharge` INT NOT NULL DEFAULT 0,
  `creator` VARCHAR(64) DEFAULT '',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) DEFAULT '',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_wallet_user` (`user_id`, `user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包';
"""

PAY_WALLET_TX_DDL = """
CREATE TABLE IF NOT EXISTS `pay_wallet_transaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `no` VARCHAR(64) NOT NULL,
  `wallet_id` BIGINT NOT NULL,
  `biz_type` INT NOT NULL,
  `biz_id` VARCHAR(64) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `price` INT NOT NULL,
  `balance` INT NOT NULL,
  `creator` VARCHAR(64) DEFAULT '',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` VARCHAR(64) DEFAULT '',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_wallet_tx_no` (`no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包流水';
"""


def main():
    conn = pymysql.connect(**DB_CONFIG)
    try:
        with conn.cursor() as cursor:
            cursor.execute(PAY_WALLET_DDL)
            cursor.execute(PAY_WALLET_TX_DDL)
            cursor.execute(
                """
                INSERT INTO pay_wallet
                (id, user_id, user_type, balance, freeze_price, total_expense, total_recharge,
                 creator, create_time, updater, update_time, deleted)
                VALUES (1, 1, 1, 500000, 0, 0, 500000, 'admin', NOW(), 'admin', NOW(), b'0')
                ON DUPLICATE KEY UPDATE
                  balance = VALUES(balance),
                  freeze_price = VALUES(freeze_price),
                  total_recharge = VALUES(total_recharge),
                  updater = 'admin',
                  update_time = NOW(),
                  deleted = b'0'
                """
            )
            cursor.execute("SELECT id, user_id, user_type, balance, total_recharge FROM pay_wallet WHERE id = 1")
            wallet = cursor.fetchone()
        print({"wallet_ready": True, "wallet": wallet})
    finally:
        conn.close()


if __name__ == "__main__":
    main()
