package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class AppMerchantOperatorContext {

    private Long authUserId;

    private MemberUserDO loginUser;

    private MerchantInfoDO merchant;

    private Boolean mainAccount;

    private Long subAccountId;

    private String subAccountStatus;

    private Set<String> permissionCodes;

    private List<Long> visibleServicePointIds;

    public boolean isMainAccount() {
        return Boolean.TRUE.equals(mainAccount);
    }

    public boolean isSubAccount() {
        return !isMainAccount();
    }

    public boolean hasPermission(String permissionCode) {
        return isMainAccount() || (permissionCodes != null && permissionCodes.contains(permissionCode));
    }

    public boolean canManageMerchant() {
        return hasPermission(AppMerchantOperatorContextService.PERMISSION_MERCHANT_MANAGE);
    }

    public boolean canManageServicePoint() {
        return hasPermission(AppMerchantOperatorContextService.PERMISSION_SERVICE_POINT_MANAGE);
    }

    public boolean canAcceptOrder() {
        return hasPermission(AppMerchantOperatorContextService.PERMISSION_ORDER_ACCEPT);
    }

    public List<Long> getSafeVisibleServicePointIds() {
        return visibleServicePointIds == null ? Collections.emptyList() : visibleServicePointIds;
    }
}
