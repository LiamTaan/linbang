package cn.iocoder.yudao.module.linbang.dal.mysql.matchpushbatch;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.matchpushbatch.MatchPushBatchDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MatchPushBatchMapper extends BaseMapperX<MatchPushBatchDO> {

    default MatchPushBatchDO selectByUnitIdAndStageNo(Long unitId, Integer stageNo) {
        return selectOne(new LambdaQueryWrapperX<MatchPushBatchDO>()
                .eq(MatchPushBatchDO::getUnitId, unitId)
                .eq(MatchPushBatchDO::getStageNo, stageNo)
                .last("LIMIT 1"));
    }

    default List<MatchPushBatchDO> selectExpiredActiveBatches(LocalDateTime now) {
        return selectList(new LambdaQueryWrapperX<MatchPushBatchDO>()
                .eq(MatchPushBatchDO::getStatus, "PUSHING")
                .le(MatchPushBatchDO::getExpiredAt, now)
                .orderByAsc(MatchPushBatchDO::getExpiredAt, MatchPushBatchDO::getId));
    }

    default List<MatchPushBatchDO> selectListByUnitId(Long unitId) {
        return selectList(new LambdaQueryWrapperX<MatchPushBatchDO>()
                .eq(MatchPushBatchDO::getUnitId, unitId)
                .orderByAsc(MatchPushBatchDO::getStageNo, MatchPushBatchDO::getId));
    }

    default PageResult<MatchPushBatchDO> selectPage(cn.iocoder.yudao.framework.common.pojo.PageParam pageParam,
                                                    Long unitId, String status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<MatchPushBatchDO>()
                .eqIfPresent(MatchPushBatchDO::getUnitId, unitId)
                .eqIfPresent(MatchPushBatchDO::getStatus, status)
                .orderByDesc(MatchPushBatchDO::getId));
    }
}
