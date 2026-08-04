package cn.iocoder.yudao.module.linbang.dal.mysql.partnercoordination;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerDisputePageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.PartnerCoordinationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerDisputePageRecordDTO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerPromoteAggregateDTO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerPromoteTradeAggregateDTO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerWorkbenchAggregateDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
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

    default List<PartnerCoordinationDO> selectListByDisputes(String disputeType, Collection<Long> disputeIds) {
        if (disputeIds == null || disputeIds.isEmpty()) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<PartnerCoordinationDO>()
                .eq(PartnerCoordinationDO::getDisputeType, disputeType)
                .in(PartnerCoordinationDO::getDisputeId, disputeIds)
                .orderByDesc(PartnerCoordinationDO::getId));
    }

    IPage<PartnerDisputePageRecordDTO> selectDisputePage(
            IPage<PartnerDisputePageRecordDTO> page,
            @Param("regionCodes") Collection<String> regionCodes,
            @Param("reqVO") AppPartnerDisputePageReqVO reqVO);

    PartnerWorkbenchAggregateDTO selectWorkbenchAggregate(@Param("regionCodes") Collection<String> regionCodes);

    PartnerPromoteAggregateDTO selectPromoteAggregate(
            @Param("regionCodes") Collection<String> regionCodes,
            @Param("todayStart") LocalDateTime todayStart,
            @Param("tomorrowStart") LocalDateTime tomorrowStart);

    PartnerPromoteTradeAggregateDTO selectPromoteTradeAggregate(
            @Param("regionCodes") Collection<String> regionCodes);

    Long selectOrderInRegionCount(@Param("orderId") Long orderId,
                                  @Param("regionCodes") Collection<String> regionCodes);
}
