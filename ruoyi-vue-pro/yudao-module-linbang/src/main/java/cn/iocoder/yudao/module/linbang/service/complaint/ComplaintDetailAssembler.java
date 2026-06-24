package cn.iocoder.yudao.module.linbang.service.complaint;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.ComplaintDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaintfilerel.ComplaintFileRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class ComplaintDetailAssembler {

    private ComplaintDetailAssembler() {
    }

    static ComplaintDetailRespVO build(ComplaintDO complaint, OrderInfoDO order, OrderUnitDO unit,
                                       MemberUserDO complainantUser, MemberUserDO respondentUser, MemberUserDO orderUser,
                                       MerchantInfoDO respondentMerchant, List<ComplaintFileRelDO> fileRels,
                                       List<ComplaintDO> relatedComplaints, List<OrderOperateLogDO> operateLogs) {
        ComplaintDetailRespVO respVO = BeanUtils.toBean(complaint, ComplaintDetailRespVO.class);
        if (order != null) {
            ComplaintDetailRespVO.OrderRespVO orderRespVO = BeanUtils.toBean(order, ComplaintDetailRespVO.OrderRespVO.class);
            if (orderUser != null) {
                orderRespVO.setUserNo(orderUser.getUserNo());
                orderRespVO.setUserNickname(orderUser.getNickname());
                orderRespVO.setUserMobile(orderUser.getMobile());
            }
            respVO.setOrder(orderRespVO);
        }
        if (unit != null) {
            respVO.setUnit(BeanUtils.toBean(unit, ComplaintDetailRespVO.UnitRespVO.class));
        }
        if (complainantUser != null) {
            respVO.setComplainantUser(BeanUtils.toBean(complainantUser, ComplaintDetailRespVO.UserRespVO.class));
        }
        if (respondentUser != null) {
            respVO.setRespondentUser(BeanUtils.toBean(respondentUser, ComplaintDetailRespVO.UserRespVO.class));
        }
        if (respondentMerchant != null) {
            respVO.setRespondentMerchant(BeanUtils.toBean(respondentMerchant, ComplaintDetailRespVO.MerchantRespVO.class));
        }
        respVO.setSummary(buildSummary(fileRels, relatedComplaints));
        respVO.setFiles(buildFiles(fileRels));
        respVO.setRelatedComplaints(buildRelatedComplaints(relatedComplaints, complaint.getId()));
        respVO.setOperateLogs(buildOperateLogs(operateLogs));
        return respVO;
    }

    private static ComplaintDetailRespVO.SummaryRespVO buildSummary(List<ComplaintFileRelDO> fileRels,
                                                                    List<ComplaintDO> relatedComplaints) {
        List<ComplaintDO> source = relatedComplaints == null ? Collections.emptyList() : relatedComplaints;
        ComplaintDetailRespVO.SummaryRespVO summary = new ComplaintDetailRespVO.SummaryRespVO();
        summary.setAttachmentCount(fileRels == null ? 0 : fileRels.size());
        summary.setSameOrderComplaintCount(source.size());
        summary.setSameRespondentComplaintCount((int) source.stream()
                .map(ComplaintDO::getRespondentUserId)
                .filter(item -> item != null)
                .distinct()
                .count());
        summary.setPendingCount((int) source.stream().filter(item -> "PENDING".equalsIgnoreCase(item.getStatus())).count());
        summary.setProcessingCount((int) source.stream().filter(item -> "PROCESSING".equalsIgnoreCase(item.getStatus())).count());
        summary.setFinishedCount((int) source.stream().filter(item -> "FINISHED".equalsIgnoreCase(item.getStatus())).count());
        summary.setRejectedCount((int) source.stream().filter(item -> "REJECTED".equalsIgnoreCase(item.getStatus())).count());
        return summary;
    }

    private static List<ComplaintDetailRespVO.FileRespVO> buildFiles(List<ComplaintFileRelDO> fileRels) {
        if (fileRels == null || fileRels.isEmpty()) {
            return Collections.emptyList();
        }
        return fileRels.stream().map(item -> {
            ComplaintDetailRespVO.FileRespVO respVO = new ComplaintDetailRespVO.FileRespVO();
            respVO.setFileId(item.getFileId());
            return respVO;
        }).collect(Collectors.toList());
    }

    private static List<ComplaintDetailRespVO.RelatedComplaintRespVO> buildRelatedComplaints(List<ComplaintDO> relatedComplaints,
                                                                                               Long currentId) {
        if (relatedComplaints == null || relatedComplaints.isEmpty()) {
            return Collections.emptyList();
        }
        return relatedComplaints.stream()
                .filter(item -> currentId == null || !currentId.equals(item.getId()))
                .limit(10)
                .map(item -> BeanUtils.toBean(item, ComplaintDetailRespVO.RelatedComplaintRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<ComplaintDetailRespVO.OperateLogRespVO> buildOperateLogs(List<OrderOperateLogDO> operateLogs) {
        if (operateLogs == null || operateLogs.isEmpty()) {
            return Collections.emptyList();
        }
        return operateLogs.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, ComplaintDetailRespVO.OperateLogRespVO.class))
                .collect(Collectors.toList());
    }
}
