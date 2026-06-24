package cn.iocoder.yudao.module.linbang.service.memberrealname;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo.MemberUserRealNameDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class MemberUserRealNameDetailAssembler {

    private MemberUserRealNameDetailAssembler() {
    }

    static MemberUserRealNameDetailRespVO build(MemberUserRealNameDO realName, MemberUserDO user, MerchantInfoDO merchant,
                                                MerchantEntryDO latestEntry, List<MemberUserQualificationDO> qualifications,
                                                List<CreditRecordDO> creditRecords) {
        MemberUserRealNameDetailRespVO respVO = BeanUtils.toBean(realName, MemberUserRealNameDetailRespVO.class);
        if (user != null) {
            respVO.setUser(BeanUtils.toBean(user, MemberUserRealNameDetailRespVO.UserRespVO.class));
        }
        if (merchant != null) {
            respVO.setMerchant(BeanUtils.toBean(merchant, MemberUserRealNameDetailRespVO.MerchantRespVO.class));
        }
        if (latestEntry != null) {
            respVO.setLatestEntry(BeanUtils.toBean(latestEntry, MemberUserRealNameDetailRespVO.LatestEntryRespVO.class));
        }
        respVO.setSummary(buildSummary(merchant, latestEntry, qualifications, creditRecords));
        respVO.setQualifications(buildQualifications(qualifications));
        respVO.setCreditRecords(buildCreditRecords(creditRecords));
        return respVO;
    }

    private static MemberUserRealNameDetailRespVO.SummaryRespVO buildSummary(MerchantInfoDO merchant, MerchantEntryDO latestEntry,
                                                                              List<MemberUserQualificationDO> qualifications,
                                                                              List<CreditRecordDO> creditRecords) {
        List<MemberUserQualificationDO> qualificationSource = qualifications == null ? Collections.emptyList() : qualifications;
        List<CreditRecordDO> creditSource = creditRecords == null ? Collections.emptyList() : creditRecords;
        CreditRecordDO latestCredit = creditSource.isEmpty() ? null : creditSource.get(0);
        MemberUserRealNameDetailRespVO.SummaryRespVO summary = new MemberUserRealNameDetailRespVO.SummaryRespVO();
        summary.setQualificationCount(qualificationSource.size());
        summary.setApprovedQualificationCount((int) qualificationSource.stream()
                .filter(item -> "APPROVED".equalsIgnoreCase(item.getAuditStatus()))
                .count());
        summary.setRejectedQualificationCount((int) qualificationSource.stream()
                .filter(item -> "REJECTED".equalsIgnoreCase(item.getAuditStatus()))
                .count());
        summary.setCreditRecordCount(creditSource.size());
        summary.setLatestCreditScore(latestCredit == null ? null : latestCredit.getAfterScore());
        summary.setLatestCreditLevel(merchant == null ? null : merchant.getCreditLevel());
        summary.setMerchantBound(merchant != null);
        summary.setLatestEntryApproved(latestEntry != null && "APPROVED".equalsIgnoreCase(latestEntry.getStatus()));
        return summary;
    }

    private static List<MemberUserRealNameDetailRespVO.QualificationRespVO> buildQualifications(List<MemberUserQualificationDO> qualifications) {
        if (qualifications == null || qualifications.isEmpty()) {
            return Collections.emptyList();
        }
        return qualifications.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MemberUserRealNameDetailRespVO.QualificationRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<MemberUserRealNameDetailRespVO.CreditRecordRespVO> buildCreditRecords(List<CreditRecordDO> creditRecords) {
        if (creditRecords == null || creditRecords.isEmpty()) {
            return Collections.emptyList();
        }
        return creditRecords.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MemberUserRealNameDetailRespVO.CreditRecordRespVO.class))
                .collect(Collectors.toList());
    }
}
