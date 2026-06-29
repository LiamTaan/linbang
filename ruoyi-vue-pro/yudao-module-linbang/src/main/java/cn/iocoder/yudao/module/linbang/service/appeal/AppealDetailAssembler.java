package cn.iocoder.yudao.module.linbang.service.appeal;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.AppealDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appealfilerel.AppealFileRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.PartnerCoordinationDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class AppealDetailAssembler {

    private AppealDetailAssembler() {
    }

    static AppealDetailRespVO build(AppealDO appeal, OrderInfoDO order, OrderUnitDO unit,
                                    MemberUserDO user, MemberUserDO orderUser, MerchantInfoDO merchant,
                                    List<AppealFileRelDO> fileRels, List<AppealDO> relatedAppeals,
                                    List<PartnerCoordinationDO> coordinationRecords, List<OrderOperateLogDO> operateLogs) {
        AppealDetailRespVO respVO = BeanUtils.toBean(appeal, AppealDetailRespVO.class);
        if (order != null) {
            AppealDetailRespVO.OrderRespVO orderRespVO = BeanUtils.toBean(order, AppealDetailRespVO.OrderRespVO.class);
            if (orderUser != null) {
                orderRespVO.setUserNo(orderUser.getUserNo());
                orderRespVO.setUserNickname(orderUser.getNickname());
                orderRespVO.setUserMobile(orderUser.getMobile());
            }
            respVO.setOrder(orderRespVO);
        }
        if (unit != null) {
            respVO.setUnit(BeanUtils.toBean(unit, AppealDetailRespVO.UnitRespVO.class));
        }
        if (user != null) {
            respVO.setUser(BeanUtils.toBean(user, AppealDetailRespVO.UserRespVO.class));
        }
        if (merchant != null) {
            respVO.setMerchant(BeanUtils.toBean(merchant, AppealDetailRespVO.MerchantRespVO.class));
        }
        respVO.setSummary(buildSummary(fileRels, relatedAppeals));
        respVO.setFiles(buildFiles(fileRels));
        respVO.setRelatedAppeals(buildRelatedAppeals(relatedAppeals, appeal.getId()));
        respVO.setCoordinationRecords(buildCoordinationRecords(coordinationRecords));
        respVO.setOperateLogs(buildOperateLogs(operateLogs));
        return respVO;
    }

    private static AppealDetailRespVO.SummaryRespVO buildSummary(List<AppealFileRelDO> fileRels,
                                                                 List<AppealDO> relatedAppeals) {
        List<AppealDO> source = relatedAppeals == null ? Collections.emptyList() : relatedAppeals;
        AppealDetailRespVO.SummaryRespVO summary = new AppealDetailRespVO.SummaryRespVO();
        summary.setAttachmentCount(fileRels == null ? 0 : fileRels.size());
        summary.setSameOrderAppealCount(source.size());
        summary.setSameUserAppealCount((int) source.stream()
                .map(AppealDO::getUserId)
                .filter(item -> item != null)
                .distinct()
                .count());
        summary.setPendingAuditCount((int) source.stream().filter(item -> "PENDING".equalsIgnoreCase(item.getAuditStatus())).count());
        summary.setProcessingCount((int) source.stream().filter(item -> "PROCESSING".equalsIgnoreCase(item.getStatus())).count());
        summary.setApprovedCount((int) source.stream().filter(item -> "APPROVED".equalsIgnoreCase(item.getAuditStatus())).count());
        summary.setRejectedCount((int) source.stream().filter(item -> "REJECTED".equalsIgnoreCase(item.getAuditStatus())).count());
        summary.setFinishedCount((int) source.stream().filter(item -> "FINISHED".equalsIgnoreCase(item.getStatus())).count());
        return summary;
    }

    private static List<AppealDetailRespVO.FileRespVO> buildFiles(List<AppealFileRelDO> fileRels) {
        if (fileRels == null || fileRels.isEmpty()) {
            return Collections.emptyList();
        }
        return fileRels.stream().map(item -> {
            AppealDetailRespVO.FileRespVO respVO = new AppealDetailRespVO.FileRespVO();
            respVO.setFileId(item.getFileId());
            return respVO;
        }).collect(Collectors.toList());
    }

    private static List<AppealDetailRespVO.RelatedAppealRespVO> buildRelatedAppeals(List<AppealDO> relatedAppeals,
                                                                                      Long currentId) {
        if (relatedAppeals == null || relatedAppeals.isEmpty()) {
            return Collections.emptyList();
        }
        return relatedAppeals.stream()
                .filter(item -> currentId == null || !currentId.equals(item.getId()))
                .limit(10)
                .map(item -> BeanUtils.toBean(item, AppealDetailRespVO.RelatedAppealRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<AppealDetailRespVO.OperateLogRespVO> buildOperateLogs(List<OrderOperateLogDO> operateLogs) {
        if (operateLogs == null || operateLogs.isEmpty()) {
            return Collections.emptyList();
        }
        return operateLogs.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, AppealDetailRespVO.OperateLogRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<AppealDetailRespVO.CoordinationRespVO> buildCoordinationRecords(
            List<PartnerCoordinationDO> coordinationRecords) {
        if (coordinationRecords == null || coordinationRecords.isEmpty()) {
            return Collections.emptyList();
        }
        return coordinationRecords.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, AppealDetailRespVO.CoordinationRespVO.class))
                .collect(Collectors.toList());
    }
}
