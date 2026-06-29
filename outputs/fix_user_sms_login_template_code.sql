UPDATE system_sms_template
SET code = 'user-sms-login'
WHERE code = 'sms_login_code';

SELECT id, code, api_template_id, channel_id
FROM system_sms_template
WHERE code = 'user-sms-login';
