package cn.iocoder.yudao.module.linbang.dal.mysql.partnerregionrel;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerregionrel.PartnerRegionRelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerRegionRelMapper extends BaseMapperX<PartnerRegionRelDO> {

    default List<PartnerRegionRelDO> selectListByPartnerId(Long partnerId) {
        return selectList(new LambdaQueryWrapperX<PartnerRegionRelDO>()
                .eq(PartnerRegionRelDO::getPartnerId, partnerId)
                .orderByAsc(PartnerRegionRelDO::getId));
    }
}
