package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantsubaccount.MerchantSubAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantsubaccountservicepointrel.MerchantSubAccountServicePointRelDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantsubaccount.MerchantSubAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantsubaccountservicepointrel.MerchantSubAccountServicePointRelMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_AUTH_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SUB_ACCOUNT_DISABLED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SUB_ACCOUNT_PERMISSION_DENIED;

@Service
@Validated
public class AppMerchantOperatorContextServiceImpl implements AppMerchantOperatorContextService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantSubAccountMapper merchantSubAccountMapper;
    @Resource
    private MerchantSubAccountServicePointRelMapper merchantSubAccountServicePointRelMapper;

    @Override
    public AppMerchantOperatorContext getRequiredContext(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId())
                .last("LIMIT 1"));
        if (merchant != null) {
            return AppMerchantOperatorContext.builder()
                    .authUserId(authUserId)
                    .loginUser(loginUser)
                    .merchant(merchant)
                    .mainAccount(Boolean.TRUE)
                    .permissionCodes(Collections.emptySet())
                    .visibleServicePointIds(Collections.emptyList())
                    .build();
        }
        MerchantSubAccountDO subAccount = merchantSubAccountMapper.selectByUserId(loginUser.getId());
        if (subAccount == null) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        if (!Objects.equals(subAccount.getStatus(), "ENABLE")) {
            throw exception(MERCHANT_SUB_ACCOUNT_DISABLED);
        }
        merchant = merchantInfoMapper.selectById(subAccount.getMerchantId());
        if (merchant == null) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        Set<String> permissions = new LinkedHashSet<>(JsonUtils.parseArray(subAccount.getPermissionCodesJson(), String.class));
        List<Long> visibleServicePointIds = merchantSubAccountServicePointRelMapper.selectListBySubAccountId(subAccount.getId()).stream()
                .map(MerchantSubAccountServicePointRelDO::getServicePointId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return AppMerchantOperatorContext.builder()
                .authUserId(authUserId)
                .loginUser(loginUser)
                .merchant(merchant)
                .mainAccount(Boolean.FALSE)
                .subAccountId(subAccount.getId())
                .subAccountStatus(subAccount.getStatus())
                .permissionCodes(permissions)
                .visibleServicePointIds(visibleServicePointIds)
                .build();
    }

    @Override
    public AppMerchantOperatorContext getRequiredMerchantManageContext(Long authUserId) {
        AppMerchantOperatorContext context = getRequiredContext(authUserId);
        if (!context.canManageMerchant()) {
            throw exception(MERCHANT_SUB_ACCOUNT_PERMISSION_DENIED);
        }
        return context;
    }

    @Override
    public AppMerchantOperatorContext getRequiredServicePointManageContext(Long authUserId) {
        AppMerchantOperatorContext context = getRequiredContext(authUserId);
        if (!context.canManageServicePoint()) {
            throw exception(MERCHANT_SUB_ACCOUNT_PERMISSION_DENIED);
        }
        return context;
    }

    @Override
    public AppMerchantOperatorContext getRequiredOrderAcceptContext(Long authUserId) {
        AppMerchantOperatorContext context = getRequiredContext(authUserId);
        if (!context.canAcceptOrder()) {
            throw exception(MERCHANT_SUB_ACCOUNT_PERMISSION_DENIED);
        }
        return context;
    }

    @Override
    public List<MerchantServicePointDO> filterVisibleServicePoints(AppMerchantOperatorContext context, List<MerchantServicePointDO> servicePoints) {
        if (servicePoints == null || servicePoints.isEmpty() || context == null || context.isMainAccount()) {
            return servicePoints;
        }
        Set<Long> visibleIds = new LinkedHashSet<>(context.getSafeVisibleServicePointIds());
        return servicePoints.stream()
                .filter(item -> visibleIds.contains(item.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void validateVisibleServicePoint(AppMerchantOperatorContext context, Long servicePointId) {
        if (context == null || context.isMainAccount() || servicePointId == null) {
            return;
        }
        if (!context.getSafeVisibleServicePointIds().contains(servicePointId)) {
            throw exception(MERCHANT_SUB_ACCOUNT_PERMISSION_DENIED);
        }
    }
}
