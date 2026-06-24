package cn.iocoder.yudao.module.linbang.dal.mysql.promoterrelation;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterrelation.PromoterRelationDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PromoterRelationMapper extends BaseMapperX<PromoterRelationDO> {

    default PromoterRelationDO selectByPromoterIdAndUserId(Long promoterId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<PromoterRelationDO>()
                .eq(PromoterRelationDO::getPromoterId, promoterId)
                .eq(PromoterRelationDO::getUserId, userId)
                .last("LIMIT 1"));
    }
}
