SET NAMES utf8mb4;
UPDATE `infra_config`
SET `value` = '8339941bfd632eac92ca1de6ef8de10b', `update_time` = NOW(), `updater` = 'admin'
WHERE `config_key` = 'linbang.app.amap-js-key';
UPDATE `infra_config`
SET `value` = '17fbedba495ba11a985b182162a5d83b', `update_time` = NOW(), `updater` = 'admin'
WHERE `config_key` = 'linbang.app.amap-security-js-code';
SELECT `config_key`, `value` FROM `infra_config`
WHERE `config_key` IN ('linbang.app.amap-js-key', 'linbang.app.amap-security-js-code');
