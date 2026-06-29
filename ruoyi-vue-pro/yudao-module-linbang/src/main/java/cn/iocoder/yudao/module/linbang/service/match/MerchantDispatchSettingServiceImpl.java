package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantdispatchsetting.MerchantDispatchSettingDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantdispatchsetting.MerchantDispatchSettingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class MerchantDispatchSettingServiceImpl implements MerchantDispatchSettingService {

    @Resource
    private MerchantDispatchSettingMapper merchantDispatchSettingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantDispatchSettingDO getOrCreate(Long merchantId) {
        MerchantDispatchSettingDO setting = merchantDispatchSettingMapper.selectByMerchantId(merchantId);
        if (setting != null) {
            return setting;
        }
        setting = MerchantDispatchSettingDO.builder()
                .merchantId(merchantId)
                .dispatchEnabled(true)
                .maxAcceptRadiusKm(new BigDecimal("5.00"))
                .voiceRemindEnabled(true)
                .build();
        merchantDispatchSettingMapper.insert(setting);
        return setting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSetting(MerchantDispatchSettingDO settingDO) {
        MerchantDispatchSettingDO current = getOrCreate(settingDO.getMerchantId());
        settingDO.setId(current.getId());
        merchantDispatchSettingMapper.updateById(settingDO);
    }
}
