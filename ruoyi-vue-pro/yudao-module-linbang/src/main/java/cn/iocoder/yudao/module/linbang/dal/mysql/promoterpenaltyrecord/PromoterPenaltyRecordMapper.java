package cn.iocoder.yudao.module.linbang.dal.mysql.promoterpenaltyrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo.PromoterPenaltyRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterpenaltyrecord.PromoterPenaltyRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface PromoterPenaltyRecordMapper extends BaseMapperX<PromoterPenaltyRecordDO> {

    default PageResult<PromoterPenaltyRecordDO> selectPageByPromoterIds(PromoterPenaltyRecordPageReqVO reqVO, Collection<Long> promoterIds) {
        return BaseMapperX.super.selectPage(reqVO, new LambdaQueryWrapperX<PromoterPenaltyRecordDO>()
                .eqIfPresent(PromoterPenaltyRecordDO::getPromoterId, reqVO.getPromoterId())
                .inIfPresent(PromoterPenaltyRecordDO::getPromoterId, promoterIds)
                .eqIfPresent(PromoterPenaltyRecordDO::getPenaltyAction, reqVO.getPenaltyAction())
                .eqIfPresent(PromoterPenaltyRecordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PromoterPenaltyRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PromoterPenaltyRecordDO::getId));
    }

    default java.util.List<PromoterPenaltyRecordDO> selectBatchByMinId(Long minId, int limit) {
        return selectList(new LambdaQueryWrapperX<PromoterPenaltyRecordDO>()
                .gtIfPresent(PromoterPenaltyRecordDO::getId, minId)
                .orderByAsc(PromoterPenaltyRecordDO::getId)
                .last("LIMIT " + limit));
    }
}
