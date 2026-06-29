package cn.iocoder.yudao.module.linbang.service.memberqualification;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.certexemption.CertExemptionApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class MemberQualificationDetailAssembler {

    private MemberQualificationDetailAssembler() {
    }

    static MemberQualificationDetailRespVO build(MemberUserQualificationDO qualification, MemberUserDO user,
                                                 MemberUserRealNameDO realName, MerchantInfoDO merchant,
                                                 MerchantEntryDO latestEntry, List<MemberUserQualificationDO> relatedQualifications,
                                                 List<CertExemptionApplyDO> certExemptions,
                                                 List<CreditRecordDO> creditRecords) {
        MemberQualificationDetailRespVO respVO = BeanUtils.toBean(qualification, MemberQualificationDetailRespVO.class);
        if (user != null) {
            respVO.setUser(BeanUtils.toBean(user, MemberQualificationDetailRespVO.UserRespVO.class));
        }
        if (realName != null) {
            respVO.setRealName(BeanUtils.toBean(realName, MemberQualificationDetailRespVO.RealNameRespVO.class));
        }
        if (merchant != null) {
            respVO.setMerchant(BeanUtils.toBean(merchant, MemberQualificationDetailRespVO.MerchantRespVO.class));
        }
        if (latestEntry != null) {
            respVO.setLatestEntry(BeanUtils.toBean(latestEntry, MemberQualificationDetailRespVO.LatestEntryRespVO.class));
        }
        respVO.setSummary(buildSummary(realName, merchant, relatedQualifications, certExemptions, creditRecords));
        respVO.setRelatedQualifications(buildRelatedQualifications(relatedQualifications, qualification.getId()));
        respVO.setCertExemptions(buildCertExemptions(certExemptions));
        respVO.setCreditRecords(buildCreditRecords(creditRecords));
        return respVO;
    }

    private static MemberQualificationDetailRespVO.SummaryRespVO buildSummary(MemberUserRealNameDO realName, MerchantInfoDO merchant,
                                                                              List<MemberUserQualificationDO> relatedQualifications,
                                                                              List<CertExemptionApplyDO> certExemptions,
                                                                              List<CreditRecordDO> creditRecords) {
        List<MemberUserQualificationDO> qualificationSource = relatedQualifications == null ? Collections.emptyList() : relatedQualifications;
        List<CreditRecordDO> creditSource = creditRecords == null ? Collections.emptyList() : creditRecords;
        CreditRecordDO latestCredit = creditSource.isEmpty() ? null : creditSource.get(0);
        MemberQualificationDetailRespVO.SummaryRespVO summary = new MemberQualificationDetailRespVO.SummaryRespVO();
        summary.setSameUserQualificationCount(qualificationSource.size());
        summary.setApprovedQualificationCount((int) qualificationSource.stream()
                .filter(item -> "APPROVED".equalsIgnoreCase(item.getAuditStatus()))
                .count());
        summary.setRejectedQualificationCount((int) qualificationSource.stream()
                .filter(item -> "REJECTED".equalsIgnoreCase(item.getAuditStatus()))
                .count());
        summary.setExpiringSoonCount((int) qualificationSource.stream()
                .filter(item -> item.getValidEndDate() != null && !item.getValidEndDate().isBefore(LocalDate.now())
                        && !item.getValidEndDate().isAfter(LocalDate.now().plusDays(30)))
                .count());
        summary.setCreditRecordCount(creditSource.size());
        summary.setLatestCreditScore(latestCredit == null ? null : latestCredit.getAfterScore());
        summary.setLatestCreditLevel(merchant == null ? null : merchant.getCreditLevel());
        summary.setRealNameApproved(realName != null && "APPROVED".equalsIgnoreCase(realName.getAuditStatus()));
        summary.setMerchantBound(merchant != null);
        summary.setApprovedExemptionCount(certExemptions == null ? 0 : (int) certExemptions.stream()
                .filter(item -> "APPROVED".equalsIgnoreCase(item.getAuditStatus()))
                .count());
        return summary;
    }

    private static List<MemberQualificationDetailRespVO.RelatedQualificationRespVO> buildRelatedQualifications(
            List<MemberUserQualificationDO> relatedQualifications, Long currentId) {
        if (relatedQualifications == null || relatedQualifications.isEmpty()) {
            return Collections.emptyList();
        }
        return relatedQualifications.stream()
                .filter(item -> currentId == null || !currentId.equals(item.getId()))
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MemberQualificationDetailRespVO.RelatedQualificationRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<MemberQualificationDetailRespVO.CreditRecordRespVO> buildCreditRecords(List<CreditRecordDO> creditRecords) {
        if (creditRecords == null || creditRecords.isEmpty()) {
            return Collections.emptyList();
        }
        return creditRecords.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MemberQualificationDetailRespVO.CreditRecordRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<MemberQualificationDetailRespVO.CertExemptionRespVO> buildCertExemptions(List<CertExemptionApplyDO> certExemptions) {
        if (certExemptions == null || certExemptions.isEmpty()) {
            return Collections.emptyList();
        }
        return certExemptions.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MemberQualificationDetailRespVO.CertExemptionRespVO.class))
                .collect(Collectors.toList());
    }
}
