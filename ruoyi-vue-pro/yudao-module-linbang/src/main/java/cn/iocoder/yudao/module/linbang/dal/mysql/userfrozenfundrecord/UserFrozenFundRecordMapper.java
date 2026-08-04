package cn.iocoder.yudao.module.linbang.dal.mysql.userfrozenfundrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userfrozenfundrecord.UserFrozenFundRecordDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

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

    default List<UserFrozenFundRecordDO> selectBatchByMinId(Long minId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 1000));
        return selectPage(new Page<UserFrozenFundRecordDO>(1, safeLimit, false),
                new LambdaQueryWrapperX<UserFrozenFundRecordDO>()
                .gtIfPresent(UserFrozenFundRecordDO::getId, minId)
                .orderByAsc(UserFrozenFundRecordDO::getId)).getRecords();
    }
}
