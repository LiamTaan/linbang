package cn.iocoder.yudao.module.linbang.dal.mysql.complaint;

import cn.hutool.core.util.StrUtil;
import java.util.List;
import java.util.Collections;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.*;

/**
 * 投诉 Mapper
 *
 * @author dawn
 */
@Mapper
public interface ComplaintMapper extends BaseMapperX<ComplaintDO> {

    default List<ComplaintDO> selectListByComplaintNo(String complaintNo) {
        if (StrUtil.isBlank(complaintNo)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<ComplaintDO>()
                .like(ComplaintDO::getComplaintNo, complaintNo)
                .orderByDesc(ComplaintDO::getId));
    }

    default PageResult<ComplaintDO> selectPage(ComplaintPageReqVO reqVO, List<Long> matchedOrderIds,
                                               List<Long> matchedUnitIds, List<Long> complainantUserIds,
                                               List<Long> respondentUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ComplaintDO>()
                .eqIfPresent(ComplaintDO::getComplaintNo, reqVO.getComplaintNo())
                .inIfPresent(ComplaintDO::getOrderId, matchedOrderIds)
                .inIfPresent(ComplaintDO::getUnitId, matchedUnitIds)
                .inIfPresent(ComplaintDO::getComplainantUserId, complainantUserIds)
                .inIfPresent(ComplaintDO::getRespondentUserId, respondentUserIds)
                .eqIfPresent(ComplaintDO::getComplaintType, reqVO.getComplaintType())
                .eqIfPresent(ComplaintDO::getContent, reqVO.getContent())
                .eqIfPresent(ComplaintDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ComplaintDO::getHandleBy, reqVO.getHandleBy())
                .betweenIfPresent(ComplaintDO::getHandleTime, reqVO.getHandleTime())
                .eqIfPresent(ComplaintDO::getResultDesc, reqVO.getResultDesc())
                .betweenIfPresent(ComplaintDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ComplaintDO::getId));
    }

}
