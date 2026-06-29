package cn.iocoder.yudao.module.linbang.dal.mysql.merchantsubaccountservicepointrel;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantsubaccountservicepointrel.MerchantSubAccountServicePointRelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MerchantSubAccountServicePointRelMapper extends BaseMapperX<MerchantSubAccountServicePointRelDO> {

    default List<MerchantSubAccountServicePointRelDO> selectListBySubAccountId(Long subAccountId) {
        return selectList(new LambdaQueryWrapperX<MerchantSubAccountServicePointRelDO>()
                .eq(MerchantSubAccountServicePointRelDO::getSubAccountId, subAccountId)
                .orderByAsc(MerchantSubAccountServicePointRelDO::getId));
    }
}
