package cn.iocoder.yudao.module.linbang.dal.mysql.blacklist;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.blacklist.BlacklistDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlacklistMapper extends BaseMapperX<BlacklistDO> {

    default PageResult<BlacklistDO> selectPage(BlacklistPageReqVO reqVO, List<Long> matchedUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BlacklistDO>()
                .inIfPresent(BlacklistDO::getUserId, matchedUserIds)
                .eqIfPresent(BlacklistDO::getBlackType, reqVO.getBlackType())
                .eqIfPresent(BlacklistDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(BlacklistDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BlacklistDO::getId));
    }

    default java.util.List<BlacklistDO> selectBatchByMinId(Long minId, int limit) {
        return selectList(new LambdaQueryWrapperX<BlacklistDO>()
                .gtIfPresent(BlacklistDO::getId, minId)
                .orderByAsc(BlacklistDO::getId)
                .last("LIMIT " + limit));
    }
}
