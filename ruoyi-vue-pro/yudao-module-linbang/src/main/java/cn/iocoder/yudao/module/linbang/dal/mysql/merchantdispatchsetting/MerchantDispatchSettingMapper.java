package cn.iocoder.yudao.module.linbang.dal.mysql.merchantdispatchsetting;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantdispatchsetting.MerchantDispatchSettingDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantDispatchSettingMapper extends BaseMapperX<MerchantDispatchSettingDO> {

    default MerchantDispatchSettingDO selectByMerchantId(Long merchantId) {
        return selectOne(MerchantDispatchSettingDO::getMerchantId, merchantId);
    }
}
