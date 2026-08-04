package cn.iocoder.yudao.module.linbang.dal.mysql.blacklist;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.blacklist.BlacklistDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BlacklistMapper extends BaseMapperX<BlacklistDO> {

    default BlacklistDO selectEffective(Long userId, String blackType, LocalDateTime now) {
        return selectOne(new LambdaQueryWrapperX<BlacklistDO>()
                .eq(BlacklistDO::getUserId, userId)
                .eq(BlacklistDO::getBlackType, blackType)
                .eq(BlacklistDO::getStatus, "ENABLE")
                .le(BlacklistDO::getStartTime, now)
                .and(wrapper -> wrapper.isNull(BlacklistDO::getEndTime)
                        .or().ge(BlacklistDO::getEndTime, now))
                .last("LIMIT 1"));
    }

    default BlacklistDO selectEnabledForUpdate(Long userId, String blackType) {
        return selectOne(new LambdaQueryWrapperX<BlacklistDO>()
                .eq(BlacklistDO::getUserId, userId)
                .eq(BlacklistDO::getBlackType, blackType)
                .eq(BlacklistDO::getStatus, "ENABLE")
                .orderByDesc(BlacklistDO::getId)
                .last("LIMIT 1 FOR UPDATE"));
    }

    default BlacklistDO selectAnyEffective(Long userId, LocalDateTime now) {
        return selectOne(new LambdaQueryWrapperX<BlacklistDO>()
                .eq(BlacklistDO::getUserId, userId)
                .eq(BlacklistDO::getStatus, "ENABLE")
                .le(BlacklistDO::getStartTime, now)
                .and(wrapper -> wrapper.isNull(BlacklistDO::getEndTime)
                        .or().ge(BlacklistDO::getEndTime, now))
                .last("LIMIT 1"));
    }

    default PageResult<BlacklistDO> selectPage(BlacklistPageReqVO reqVO, List<Long> matchedUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BlacklistDO>()
                .inIfPresent(BlacklistDO::getUserId, matchedUserIds)
                .eqIfPresent(BlacklistDO::getBlackType, reqVO.getBlackType())
                .eqIfPresent(BlacklistDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(BlacklistDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BlacklistDO::getId));
    }

    default List<BlacklistDO> selectBatchByMinId(Long minId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 1000));
        return selectPage(new Page<BlacklistDO>(1, safeLimit, false), new LambdaQueryWrapperX<BlacklistDO>()
                .gtIfPresent(BlacklistDO::getId, minId)
                .orderByAsc(BlacklistDO::getId)).getRecords();
    }
}
