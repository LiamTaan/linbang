package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantdispatchsetting.MerchantDispatchSettingDO;

public interface MerchantDispatchSettingService {

    MerchantDispatchSettingDO getOrCreate(Long merchantId);

    void updateSetting(MerchantDispatchSettingDO settingDO);
}
