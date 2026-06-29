package cn.iocoder.yudao.module.linbang.service.security;

import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ADMIN_DYNAMIC_KEY_REQUIRED;

@Aspect
@Component
public class AdminDynamicKeyAspect {

    private static final Set<String> PROTECTED_URIS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "/admin-api/member/role-apply/audit",
            "/admin-api/merchant/entry/audit",
            "/admin-api/wallet/withdraw/audit",
            "/admin-api/pay/refund/audit",
            "/admin-api/partner/price-report/audit",
            "/admin-api/order/abnormal/final-audit",
            "/admin-api/review/complaint/process",
            "/admin-api/review/appeal/audit",
            "/admin-api/linbang/member-user/restrict",
            "/admin-api/linbang/member-user/restrict/release",
            "/admin-api/wallet/divide-rule/create",
            "/admin-api/wallet/divide-rule/update",
            "/admin-api/risk/rule/create",
            "/admin-api/risk/rule/update",
            "/admin-api/risk/rule/delete",
            "/admin-api/risk/rule/delete-list",
            "/admin-api/platform-config/setting/create",
            "/admin-api/platform-config/setting/update"
    )));

    @Resource
    private AdminDynamicKeyService adminDynamicKeyService;

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null || !PROTECTED_URIS.contains(request.getRequestURI())) {
            return joinPoint.proceed();
        }
        String token = request.getHeader(AdminDynamicKeyService.HEADER_NAME);
        if (!adminDynamicKeyService.validateCurrentAdminToken(token)) {
            throw exception(ADMIN_DYNAMIC_KEY_REQUIRED);
        }
        return joinPoint.proceed();
    }
}
