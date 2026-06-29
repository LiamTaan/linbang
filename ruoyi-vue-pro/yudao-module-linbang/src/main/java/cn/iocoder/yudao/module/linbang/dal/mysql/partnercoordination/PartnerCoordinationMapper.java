package cn.iocoder.yudao.module.linbang.dal.mysql.partnercoordination;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.PartnerCoordinationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerCoordinationMapper extends BaseMapperX<PartnerCoordinationDO> {

    default List<PartnerCoordinationDO> selectListByDispute(String disputeType, Long disputeId) {
        return selectList(new LambdaQueryWrapperX<PartnerCoordinationDO>()
                .eq(PartnerCoordinationDO::getDisputeType, disputeType)
                .eq(PartnerCoordinationDO::getDisputeId, disputeId)
                .orderByDesc(PartnerCoordinationDO::getId));
    }

    default List<PartnerCoordinationDO> selectListByPartnerId(Long partnerId) {
        return selectList(new LambdaQueryWrapperX<PartnerCoordinationDO>()
                .eq(PartnerCoordinationDO::getPartnerId, partnerId)
                .orderByDesc(PartnerCoordinationDO::getId));
    }
}
