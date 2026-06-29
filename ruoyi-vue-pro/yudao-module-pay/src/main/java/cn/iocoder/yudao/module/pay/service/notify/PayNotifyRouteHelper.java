package cn.iocoder.yudao.module.pay.service.notify;

import cn.hutool.core.util.StrUtil;

public final class PayNotifyRouteHelper {

    private PayNotifyRouteHelper() {
    }

    public static boolean matches(String notifyUrl, String pathSuffix) {
        if (StrUtil.isBlank(notifyUrl) || StrUtil.isBlank(pathSuffix)) {
            return false;
        }
        String normalizedUrl = StrUtil.removeSuffix(notifyUrl.trim(), "/");
        String normalizedSuffix = StrUtil.addPrefixIfNot(pathSuffix.trim(), "/");
        return normalizedUrl.endsWith(normalizedSuffix);
    }

}
