package cn.iocoder.yudao.module.linbang.dal.mysql.sensitivehitlog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitivehitlog.SensitiveHitLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface SensitiveHitLogMapper extends BaseMapperX<SensitiveHitLogDO> {

    default PageResult<SensitiveHitLogDO> selectPage(SensitiveHitLogPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SensitiveHitLogDO>()
                .eqIfPresent(SensitiveHitLogDO::getSceneType, reqVO.getSceneType())
                .eqIfPresent(SensitiveHitLogDO::getUserId, reqVO.getUserId())
                .inIfPresent(SensitiveHitLogDO::getUserId, userIds)
                .eqIfPresent(SensitiveHitLogDO::getBizType, reqVO.getBizType())
                .eqIfPresent(SensitiveHitLogDO::getBizId, reqVO.getBizId())
                .eqIfPresent(SensitiveHitLogDO::getStrategy, reqVO.getStrategy())
                .eqIfPresent(SensitiveHitLogDO::getContentType, reqVO.getContentType())
                .eqIfPresent(SensitiveHitLogDO::getFileId, reqVO.getFileId())
                .eqIfPresent(SensitiveHitLogDO::getManualAuditResult, reqVO.getManualAuditResult())
                .betweenIfPresent(SensitiveHitLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SensitiveHitLogDO::getId));
    }
}
