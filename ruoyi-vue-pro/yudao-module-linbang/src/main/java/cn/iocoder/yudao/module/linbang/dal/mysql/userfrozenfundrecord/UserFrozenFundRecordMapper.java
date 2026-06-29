package cn.iocoder.yudao.module.linbang.dal.mysql.userfrozenfundrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userfrozenfundrecord.UserFrozenFundRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface UserFrozenFundRecordMapper extends BaseMapperX<UserFrozenFundRecordDO> {

    default PageResult<UserFrozenFundRecordDO> selectPage(UserFrozenFundRecordPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserFrozenFundRecordDO>()
                .eqIfPresent(UserFrozenFundRecordDO::getUserId, reqVO.getUserId())
                .inIfPresent(UserFrozenFundRecordDO::getUserId, userIds)
                .eqIfPresent(UserFrozenFundRecordDO::getStatus, reqVO.getStatus())
                .eqIfPresent(UserFrozenFundRecordDO::getSourceBizType, reqVO.getSourceBizType())
                .betweenIfPresent(UserFrozenFundRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserFrozenFundRecordDO::getId));
    }

    default java.util.List<UserFrozenFundRecordDO> selectBatchByMinId(Long minId, int limit) {
        return selectList(new LambdaQueryWrapperX<UserFrozenFundRecordDO>()
                .gtIfPresent(UserFrozenFundRecordDO::getId, minId)
                .orderByAsc(UserFrozenFundRecordDO::getId)
                .last("LIMIT " + limit));
    }
}
