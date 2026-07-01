package cn.iocoder.yudao.module.linbang.service.memberuser;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo.MemberUserDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberaddress.MemberUserAddressDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class MemberUserDetailAssembler {

    private MemberUserDetailAssembler() {
    }

    static MemberUserDetailRespVO build(MemberUserDO user, MemberUserRealNameDO realName, MerchantInfoDO merchant,
                                        MerchantEntryDO latestEntry, List<MemberUserQualificationDO> qualifications,
                                        List<MemberUserAddressDO> addresses, List<CreditRecordDO> creditRecords,
                                        List<String> enabledRoleCodes) {
        MemberUserDetailRespVO respVO = BeanUtils.toBean(user, MemberUserDetailRespVO.class);
        respVO.setEnabledRoleCodes(enabledRoleCodes);
        if (realName != null) {
            respVO.setRealName(BeanUtils.toBean(realName, MemberUserDetailRespVO.RealNameRespVO.class));
        }
        if (merchant != null) {
            respVO.setMerchant(BeanUtils.toBean(merchant, MemberUserDetailRespVO.MerchantRespVO.class));
        }
        if (latestEntry != null) {
            respVO.setLatestEntry(BeanUtils.toBean(latestEntry, MemberUserDetailRespVO.LatestEntryRespVO.class));
        }
        respVO.setSummary(buildSummary(realName, merchant, latestEntry, qualifications, addresses, creditRecords));
        respVO.setQualifications(buildQualifications(qualifications));
        respVO.setAddresses(buildAddresses(addresses));
        respVO.setCreditRecords(buildCreditRecords(creditRecords));
        return respVO;
    }

    private static MemberUserDetailRespVO.SummaryRespVO buildSummary(MemberUserRealNameDO realName, MerchantInfoDO merchant,
                                                                     MerchantEntryDO latestEntry,
                                                                     List<MemberUserQualificationDO> qualifications,
                                                                     List<MemberUserAddressDO> addresses,
                                                                     List<CreditRecordDO> creditRecords) {
        List<MemberUserQualificationDO> qualificationSource = qualifications == null ? Collections.emptyList() : qualifications;
        List<MemberUserAddressDO> addressSource = addresses == null ? Collections.emptyList() : addresses;
        List<CreditRecordDO> creditSource = creditRecords == null ? Collections.emptyList() : creditRecords;
        CreditRecordDO latestCredit = creditSource.isEmpty() ? null : creditSource.get(0);
        MemberUserDetailRespVO.SummaryRespVO summary = new MemberUserDetailRespVO.SummaryRespVO();
        summary.setQualificationCount(qualificationSource.size());
        summary.setApprovedQualificationCount((int) qualificationSource.stream()
                .filter(item -> "APPROVED".equalsIgnoreCase(item.getAuditStatus()))
                .count());
        summary.setRejectedQualificationCount((int) qualificationSource.stream()
                .filter(item -> "REJECTED".equalsIgnoreCase(item.getAuditStatus()))
                .count());
        summary.setAddressCount(addressSource.size());
        summary.setDefaultAddressCount((int) addressSource.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsDefault()))
                .count());
        summary.setCreditRecordCount(creditSource.size());
        summary.setLatestCreditScore(latestCredit == null ? null : latestCredit.getAfterScore());
        summary.setLatestCreditLevel(merchant == null ? null : merchant.getCreditLevel());
        summary.setRealNameApproved(realName != null && "APPROVED".equalsIgnoreCase(realName.getAuditStatus()));
        summary.setMerchantBound(merchant != null);
        summary.setLatestEntryApproved(latestEntry != null && "APPROVED".equalsIgnoreCase(latestEntry.getStatus()));
        return summary;
    }

    private static List<MemberUserDetailRespVO.QualificationRespVO> buildQualifications(List<MemberUserQualificationDO> qualifications) {
        if (qualifications == null || qualifications.isEmpty()) {
            return Collections.emptyList();
        }
        return qualifications.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MemberUserDetailRespVO.QualificationRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<MemberUserDetailRespVO.AddressRespVO> buildAddresses(List<MemberUserAddressDO> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return Collections.emptyList();
        }
        return addresses.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MemberUserDetailRespVO.AddressRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<MemberUserDetailRespVO.CreditRecordRespVO> buildCreditRecords(List<CreditRecordDO> creditRecords) {
        if (creditRecords == null || creditRecords.isEmpty()) {
            return Collections.emptyList();
        }
        return creditRecords.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MemberUserDetailRespVO.CreditRecordRespVO.class))
                .collect(Collectors.toList());
    }
}
