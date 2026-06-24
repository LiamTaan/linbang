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
    String ADMIN_DYNAMIC_KEY_PASSWORD = "linbang.admin.dynamic-key.password";
    String QUALIFICATION_EXPIRE_REMIND_DAYS = "linbang.qualification.expire-remind-days";

    String AGREEMENT_SERVICE = "linbang.agreement.service";
    String AGREEMENT_PRIVACY = "linbang.agreement.privacy";

    List<String> SUPPORTED_CATEGORIES = Arrays.asList(CATEGORY_PLATFORM, CATEGORY_AGREEMENT);
}
