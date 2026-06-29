package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;

import java.util.List;

public interface AppMerchantOperatorContextService {

    String PERMISSION_ORDER_ACCEPT = "ORDER_ACCEPT";
    String PERMISSION_MERCHANT_MANAGE = "MERCHANT_MANAGE";
    String PERMISSION_SERVICE_POINT_MANAGE = "SERVICE_POINT_MANAGE";

    AppMerchantOperatorContext getRequiredContext(Long authUserId);

    AppMerchantOperatorContext getRequiredMerchantManageContext(Long authUserId);

    AppMerchantOperatorContext getRequiredServicePointManageContext(Long authUserId);

    AppMerchantOperatorContext getRequiredOrderAcceptContext(Long authUserId);

    List<MerchantServicePointDO> filterVisibleServicePoints(AppMerchantOperatorContext context, List<MerchantServicePointDO> servicePoints);

    void validateVisibleServicePoint(AppMerchantOperatorContext context, Long servicePointId);
}
