package cn.iocoder.yudao.module.pay.service.notify;

import cn.hutool.core.util.StrUtil;

import java.net.URI;

public final class PayNotifyRouteHelper {

    private PayNotifyRouteHelper() {
    }

    public static boolean matches(String notifyUrl, String pathSuffix) {
        if (StrUtil.isBlank(notifyUrl) || StrUtil.isBlank(pathSuffix)) {
            return false;
        }
        try {
            URI uri = URI.create(notifyUrl.trim());
            if (uri.isOpaque() || StrUtil.isBlank(uri.getHost()) || uri.getUserInfo() != null
                    || uri.getQuery() != null || uri.getFragment() != null
                    || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
                return false;
            }
            String normalizedPath = StrUtil.removeSuffix(uri.getRawPath(), "/");
            String normalizedSuffix = StrUtil.addPrefixIfNot(StrUtil.removeSuffix(pathSuffix.trim(), "/"), "/");
            return normalizedPath.endsWith(normalizedSuffix);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

}
