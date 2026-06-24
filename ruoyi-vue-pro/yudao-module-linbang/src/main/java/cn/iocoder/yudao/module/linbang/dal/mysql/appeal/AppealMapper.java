package cn.iocoder.yudao.module.linbang.dal.mysql.appeal;

import cn.hutool.core.util.StrUtil;
import java.util.List;
import java.util.Collections;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.*;

/**
 * 申诉 Mapper
 *
 * @author dawn
 */
@Mapper
public interface AppealMapper extends BaseMapperX<AppealDO> {

    default List<AppealDO> selectListByAppealNo(String appealNo) {
        if (StrUtil.isBlank(appealNo)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<AppealDO>()
                .like(AppealDO::getAppealNo, appealNo)
                .orderByDesc(AppealDO::getId));
    }

    default PageResult<AppealDO> selectPage(AppealPageReqVO reqVO, List<Long> matchedOrderIds,
                                            List<Long> matchedUnitIds, List<Long> matchedUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AppealDO>()
                .eqIfPresent(AppealDO::getAppealNo, reqVO.getAppealNo())
                .inIfPresent(AppealDO::getOrderId, matchedOrderIds)
                .inIfPresent(AppealDO::getUnitId, matchedUnitIds)
                .inIfPresent(AppealDO::getUserId, matchedUserIds)
                .eqIfPresent(AppealDO::getAppealType, reqVO.getAppealType())
                .eqIfPresent(AppealDO::getContent, reqVO.getContent())
                .eqIfPresent(AppealDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AppealDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(AppealDO::getAuditBy, reqVO.getAuditBy())
                .betweenIfPresent(AppealDO::getAuditTime, reqVO.getAuditTime())
                .eqIfPresent(AppealDO::getAuditRemark, reqVO.getAuditRemark())
                .eqIfPresent(AppealDO::getRejectReason, reqVO.getRejectReason())
                .betweenIfPresent(AppealDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AppealDO::getId));
    }

}
