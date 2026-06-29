package cn.iocoder.yudao.module.linbang.dal.mysql.punishlog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.punishlog.PunishLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PunishLogMapper extends BaseMapperX<PunishLogDO> {

    default PageResult<PunishLogDO> selectPage(PunishLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PunishLogDO>()
                .eqIfPresent(PunishLogDO::getUserId, reqVO.getUserId())
                .likeIfPresent(PunishLogDO::getPunishType, reqVO.getPunishType())
                .eqIfPresent(PunishLogDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PunishLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PunishLogDO::getCreateTime, PunishLogDO::getId));
    }

    default PunishLogDO selectBySourceRecord(String sourceRecordType, Long sourceRecordId) {
        return selectOne(new LambdaQueryWrapperX<PunishLogDO>()
                .eq(PunishLogDO::getSourceRecordType, sourceRecordType)
                .eq(PunishLogDO::getSourceRecordId, sourceRecordId)
                .last("LIMIT 1"));
    }
}
