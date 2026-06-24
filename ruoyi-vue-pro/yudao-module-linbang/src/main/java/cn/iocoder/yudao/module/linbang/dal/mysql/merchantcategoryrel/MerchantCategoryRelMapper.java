package cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MerchantCategoryRelMapper extends BaseMapperX<MerchantCategoryRelDO> {

    default List<MerchantCategoryRelDO> selectListByMerchantId(Long merchantId) {
        return selectList(new LambdaQueryWrapperX<MerchantCategoryRelDO>()
                .eq(MerchantCategoryRelDO::getMerchantId, merchantId)
                .orderByAsc(MerchantCategoryRelDO::getId));
    }

}
