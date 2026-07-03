package cn.iocoder.yudao.module.linbang.dal.mysql.merchantreferenceprice;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantreferenceprice.MerchantReferencePriceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MerchantReferencePriceMapper extends BaseMapperX<MerchantReferencePriceDO> {

    default List<MerchantReferencePriceDO> selectListByMerchantId(Long merchantId) {
        return selectList(new LambdaQueryWrapperX<MerchantReferencePriceDO>()
                .eq(MerchantReferencePriceDO::getMerchantId, merchantId)
                .orderByDesc(MerchantReferencePriceDO::getId));
    }

    default MerchantReferencePriceDO selectByIdAndMerchantId(Long id, Long merchantId) {
        return selectOne(new LambdaQueryWrapperX<MerchantReferencePriceDO>()
                .eq(MerchantReferencePriceDO::getId, id)
                .eq(MerchantReferencePriceDO::getMerchantId, merchantId)
                .last("LIMIT 1"));
    }
}

