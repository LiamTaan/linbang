package cn.iocoder.yudao.module.linbang.dal.mysql.rewardorderparticipation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipationPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.rewardorderparticipation.RewardOrderParticipationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RewardOrderParticipationMapper extends BaseMapperX<RewardOrderParticipationDO> {

    default PageResult<RewardOrderParticipationDO> selectPageByParticipant(Long participantUserId,
                                                                           AppRewardOrderParticipationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RewardOrderParticipationDO>()
                .eq(RewardOrderParticipationDO::getParticipantUserId, participantUserId)
                .eqIfPresent(RewardOrderParticipationDO::getStatus, reqVO.getStatus())
                .orderByDesc(RewardOrderParticipationDO::getId));
    }

    default List<RewardOrderParticipationDO> selectListByRewardOrderId(Long rewardOrderId) {
        return selectList(new LambdaQueryWrapperX<RewardOrderParticipationDO>()
                .eq(RewardOrderParticipationDO::getRewardOrderId, rewardOrderId)
                .orderByDesc(RewardOrderParticipationDO::getId));
    }

    default boolean existsByRewardOrderIdAndParticipantUserId(Long rewardOrderId, Long participantUserId) {
        return selectCount(new LambdaQueryWrapperX<RewardOrderParticipationDO>()
                .eq(RewardOrderParticipationDO::getRewardOrderId, rewardOrderId)
                .eq(RewardOrderParticipationDO::getParticipantUserId, participantUserId)) > 0;
    }
}
