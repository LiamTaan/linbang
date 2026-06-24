package cn.iocoder.yudao.module.linbang.dal.mysql.creditrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CreditRecordMapper extends BaseMapperX<CreditRecordDO> {

    default PageResult<CreditRecordDO> selectPage(CreditRecordPageReqVO reqVO, List<Long> matchedUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CreditRecordDO>()
                .inIfPresent(CreditRecordDO::getUserId, matchedUserIds)
                .eqIfPresent(CreditRecordDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(CreditRecordDO::getRuleId, reqVO.getRuleId())
                .eqIfPresent(CreditRecordDO::getRuleCode, reqVO.getRuleCode())
                .likeIfPresent(CreditRecordDO::getRuleName, reqVO.getRuleName())
                .eqIfPresent(CreditRecordDO::getTriggerType, reqVO.getTriggerType())
                .eqIfPresent(CreditRecordDO::getBizType, reqVO.getBizType())
                .eqIfPresent(CreditRecordDO::getBizId, reqVO.getBizId())
                .eqIfPresent(CreditRecordDO::getScoreChange, reqVO.getScoreChange())
                .betweenIfPresent(CreditRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CreditRecordDO::getId));
    }
}
