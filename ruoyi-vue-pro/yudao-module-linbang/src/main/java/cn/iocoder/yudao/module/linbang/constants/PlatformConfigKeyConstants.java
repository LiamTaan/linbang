package cn.iocoder.yudao.module.linbang.constants;

import java.util.Arrays;
import java.util.List;

public interface PlatformConfigKeyConstants {

    String CATEGORY_PLATFORM = "linbang_platform";
    String CATEGORY_AGREEMENT = "linbang_agreement";

    String APP_SERVICE_HOTLINE = "linbang.app.service-hotline";
    String APP_SERVICE_WECHAT = "linbang.app.service-wechat";
    String APP_ABOUT_US = "linbang.app.about-us";
    String APP_IOS_DOWNLOAD_URL = "linbang.app.ios-download-url";
    String APP_ANDROID_DOWNLOAD_URL = "linbang.app.android-download-url";
    String APP_MESSAGE_NOTICE = "linbang.app.message-notice";
    String APP_FEEDBACK_TYPES = "linbang.app.feedback-types";
    String APP_TAX_REMINDER = "linbang.app.tax-reminder";
    String APP_LICENSE_AGENT_ENTRY_URL = "linbang.app.license-agent-entry-url";
    String APP_LICENSE_AGENT_ENTRY_TITLE = "linbang.app.license-agent-entry-title";
    String APP_ORDER_PRICE_DETAIL_ENABLED = "linbang.app.order-price-detail-enabled";
    String APP_MALL_ENTRY_ENABLED = "linbang.app.mall-entry-enabled";
    String APP_MALL_ENTRY_TITLE = "linbang.app.mall-entry-title";
    String APP_MALL_ENTRY_URL = "linbang.app.mall-entry-url";
    String APP_WITHDRAW_NOTICE = "linbang.app.withdraw-notice";
    String ADMIN_DYNAMIC_KEY_PASSWORD = "linbang.admin.dynamic-key.password";
    String QUALIFICATION_EXPIRE_REMIND_DAYS = "linbang.qualification.expire-remind-days";
    String ORDER_CANCEL_LIMIT_COUNT = "linbang.order.cancel-limit-count";
    String ORDER_CANCEL_RESTRICT_HOURS = "linbang.order.cancel-restrict-hours";
    String LARGE_ORDER_DEPOSIT_THRESHOLD_FEN = "linbang.order.large-order-deposit-threshold-fen";
    String LARGE_ORDER_DEPOSIT_AMOUNT_FEN = "linbang.order.large-order-deposit-amount-fen";
    String ORDER_RECHECK_REALNAME_ENABLE = "linbang.order.recheck-realname-enable";
    String SELF_DEAL_PENISH_SCORE = "linbang.risk.self-deal-penish-score";
    String SWIPE_ORDER_FREEZE_ENABLE = "linbang.risk.swipe-order-freeze-enable";
    String COMMENT_SENSITIVE_STRATEGY = "linbang.sensitive.comment-strategy";
    String MESSAGE_SENSITIVE_STRATEGY = "linbang.sensitive.message-strategy";
    String PROMOTE_SENSITIVE_STRATEGY = "linbang.sensitive.promote-strategy";
    String COMPLAINT_SENSITIVE_STRATEGY = "linbang.sensitive.complaint-strategy";
    String APPEAL_SENSITIVE_STRATEGY = "linbang.sensitive.appeal-strategy";
    String ORDER_PUBLISH_SENSITIVE_STRATEGY = "linbang.sensitive.order-publish-strategy";
    String OCR_PROVIDER = "linbang.ocr.provider";
    String OCR_FALLBACK_MODE = "linbang.ocr.fallback-mode";
    String OCR_GENERIC_ENDPOINT = "linbang.ocr.generic.endpoint";
    String OCR_GENERIC_API_KEY = "linbang.ocr.generic.api-key";

    String AGREEMENT_SERVICE = "linbang.agreement.service";
    String AGREEMENT_PRIVACY = "linbang.agreement.privacy";
    String AGREEMENT_REGISTER_VERSION = "linbang.agreement.register-version";
    String AGREEMENT_REGISTER_TITLE = "linbang.agreement.register-title";
    String AGREEMENT_REGISTER_CONTENT = "linbang.agreement.register-content";
    String AGREEMENT_BENEFICIARY_NOTICE = "linbang.agreement.beneficiary-notice";
    String AGREEMENT_TRADE_VERSION = "linbang.agreement.trade-version";
    String AGREEMENT_TRADE_TITLE = "linbang.agreement.trade-title";
    String AGREEMENT_PROJECT_ESCROW = "linbang.agreement.project-escrow";
    String AGREEMENT_PROJECT_ESCROW_TITLE = "linbang.agreement.project-escrow-title";
    String ORDER_ANTI_ESCAPE_NOTICE = "linbang.order.anti-escape-notice";

    List<String> SUPPORTED_CATEGORIES = Arrays.asList(CATEGORY_PLATFORM, CATEGORY_AGREEMENT);
}
