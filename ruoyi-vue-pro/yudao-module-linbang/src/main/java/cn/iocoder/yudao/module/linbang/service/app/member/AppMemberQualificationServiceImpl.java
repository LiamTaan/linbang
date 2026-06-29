package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppCertExemptionCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppCertExemptionRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationReminderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationReminderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationSummaryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationUpdateReqVO;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.certexemption.CertExemptionApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.certexemption.CertExemptionApplyMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Validated
public class AppMemberQualificationServiceImpl implements AppMemberQualificationService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private CertExemptionApplyMapper certExemptionApplyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQualification(Long authUserId, @Valid AppMemberQualificationCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserQualificationDO qualification = MemberUserQualificationDO.builder()
                .userId(loginUser.getId())
                .qualificationType(reqVO.getQualificationType())
                .qualificationName(reqVO.getQualificationName())
                .qualificationNo(reqVO.getQualificationNo())
                .fileId(reqVO.getFileId())
                .evidenceFileIdsJson(reqVO.getEvidenceFileIdsJson())
                .videoFileId(reqVO.getVideoFileId())
                .validStartDate(reqVO.getValidStartDate())
                .validEndDate(reqVO.getValidEndDate())
                .auditStatus("PENDING")
                .priorityEnabled(Boolean.FALSE)
                .build();
        memberUserQualificationMapper.insert(qualification);
        return qualification.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQualification(Long authUserId, @Valid AppMemberQualificationUpdateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserQualificationDO qualification = memberUserQualificationMapper.selectListByUserIdAndIds(loginUser.getId(),
                java.util.Collections.singletonList(reqVO.getId())).stream().findFirst().orElse(null);
        if (qualification == null) {
            return;
        }
        memberUserQualificationMapper.updateById(MemberUserQualificationDO.builder()
                .id(qualification.getId())
                .qualificationType(reqVO.getQualificationType())
                .qualificationName(reqVO.getQualificationName())
                .qualificationNo(reqVO.getQualificationNo())
                .fileId(reqVO.getFileId())
                .evidenceFileIdsJson(reqVO.getEvidenceFileIdsJson())
                .videoFileId(reqVO.getVideoFileId())
                .validStartDate(reqVO.getValidStartDate())
                .validEndDate(reqVO.getValidEndDate())
                .auditStatus("PENDING")
                .auditRemark(null)
                .auditBy(null)
                .auditTime(null)
                .rejectReason(null)
                .priorityEnabled(Boolean.TRUE.equals(reqVO.getPriorityEnabled()))
                .build());
    }

    @Override
    public PageResult<AppMemberQualificationRespVO> getQualificationPage(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        List<AppMemberQualificationRespVO> list = memberUserQualificationMapper.selectListByUserId(loginUser.getId())
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
        return new PageResult<>(list, (long) list.size());
    }

    @Override
    public AppMemberQualificationRespVO getQualification(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserQualificationDO qualification = memberUserQualificationMapper.selectListByUserIdAndIds(loginUser.getId(),
                java.util.Collections.singletonList(id)).stream().findFirst().orElse(null);
        return qualification == null ? null : convert(qualification);
    }

    @Override
    public AppMemberQualificationSummaryRespVO getQualificationSummary(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserRealNameDO realName = memberUserRealNameMapper.selectByUserId(loginUser.getId());
        List<MemberUserQualificationDO> qualifications = memberUserQualificationMapper.selectListByUserId(loginUser.getId());
        List<MessageRecordDO> reminders = selectQualificationReminders(loginUser.getId());
        List<CertExemptionApplyDO> exemptions = certExemptionApplyMapper.selectListByUserId(loginUser.getId());

        AppMemberQualificationSummaryRespVO respVO = new AppMemberQualificationSummaryRespVO();
        respVO.setRealNameAuditStatus(realName == null ? null : realName.getAuditStatus());
        respVO.setWechatRealNameStatus(realName == null ? "UNBOUND" : realName.getWechatRealNameStatus());
        respVO.setAlipayRealNameStatus(realName == null ? "UNBOUND" : realName.getAlipayRealNameStatus());
        MemberUserQualificationDO businessLicense = latestQualificationByType(qualifications, "BUSINESS_LICENSE");
        MemberUserQualificationDO insurance = latestQualificationByType(qualifications, "INSURANCE_POLICY");
        MemberUserQualificationDO industry = latestIndustryQualification(qualifications);
        respVO.setBusinessLicenseAuditStatus(businessLicense == null ? null : businessLicense.getAuditStatus());
        respVO.setIndustryQualificationAuditStatus(industry == null ? null : industry.getAuditStatus());
        respVO.setInsuranceAuditStatus(insurance == null ? null : insurance.getAuditStatus());
        respVO.setExpiringQualificationCount((int) qualifications.stream()
                .filter(this::isExpiringSoon)
                .count());
        respVO.setIdCardExpiringSoon(isIdCardExpiringSoon(realName));
        respVO.setBusinessLicenseExpiringSoon(isExpiringSoon(businessLicense));
        respVO.setIndustryQualificationExpiringSoon(isExpiringSoon(industry));
        respVO.setInsuranceExpiringSoon(isExpiringSoon(insurance));
        respVO.setRealNameRejectReason(realName == null ? null : firstNonBlank(realName.getRejectReason(), realName.getVerifyFailReason()));
        respVO.setBusinessLicenseRejectReason(businessLicense == null ? null : businessLicense.getRejectReason());
        respVO.setIndustryQualificationRejectReason(industry == null ? null : industry.getRejectReason());
        respVO.setInsuranceRejectReason(insurance == null ? null : insurance.getRejectReason());
        respVO.setRealNameCanResubmit(realName != null && ("REJECTED".equals(realName.getAuditStatus())
                || "FAIL".equals(realName.getLivenessResult()) || "FAIL".equals(realName.getFaceVerifyResult())));
        respVO.setBusinessLicenseCanResubmit(businessLicense != null && "REJECTED".equals(businessLicense.getAuditStatus()));
        respVO.setIndustryQualificationCanResubmit(industry != null && "REJECTED".equals(industry.getAuditStatus()));
        respVO.setInsuranceCanResubmit(insurance != null && "REJECTED".equals(insurance.getAuditStatus()));
        respVO.setExemptionActive(hasActiveExemption(exemptions));
        respVO.setReminders(reminders.stream().limit(5).map(item -> {
            AppMemberQualificationSummaryRespVO.QualificationReminderRespVO reminder = new AppMemberQualificationSummaryRespVO.QualificationReminderRespVO();
            reminder.setMessageRecordId(item.getId());
            reminder.setQualificationId(item.getBizId());
            reminder.setReminderTitle(item.getTitle());
            reminder.setReminderContent(item.getContentSnapshot());
            reminder.setReadStatus(item.getReadStatus());
            MemberUserQualificationDO qualification = qualifications.stream()
                    .filter(q -> Objects.equals(q.getId(), item.getBizId()))
                    .findFirst().orElse(null);
            reminder.setQualificationName(qualification == null ? null : qualification.getQualificationName());
            return reminder;
        }).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public PageResult<AppMemberQualificationReminderRespVO> getReminderPage(Long authUserId, AppMemberQualificationReminderPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        List<AppMemberQualificationReminderRespVO> list = selectQualificationReminders(loginUser.getId()).stream()
                .filter(item -> reqVO.getReadStatus() == null || Objects.equals(item.getReadStatus(), reqVO.getReadStatus()))
                .map(item -> {
                    AppMemberQualificationReminderRespVO respVO = new AppMemberQualificationReminderRespVO();
                    respVO.setId(item.getId());
                    respVO.setTitle(item.getTitle());
                    respVO.setContentSnapshot(item.getContentSnapshot());
                    respVO.setReadStatus(item.getReadStatus());
                    respVO.setSendTime(item.getSendTime());
                    respVO.setBizId(item.getBizId());
                    return respVO;
                }).collect(Collectors.toList());
        return manualPage(list, reqVO.getPageNo(), reqVO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCertExemption(Long authUserId, @Valid AppCertExemptionCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        CertExemptionApplyDO applyDO = CertExemptionApplyDO.builder()
                .userId(loginUser.getId())
                .qualificationId(reqVO.getQualificationId())
                .exemptionType(reqVO.getExemptionType())
                .reason(reqVO.getReason())
                .attachmentFileIdsJson(reqVO.getAttachmentFileIdsJson())
                .effectiveStartTime(reqVO.getEffectiveStartTime())
                .effectiveEndTime(reqVO.getEffectiveEndTime())
                .auditStatus("PENDING")
                .build();
        certExemptionApplyMapper.insert(applyDO);
        return applyDO.getId();
    }

    @Override
    public PageResult<AppCertExemptionRespVO> getCertExemptionPage(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(1);
        pageParam.setPageSize(100);
        PageResult<CertExemptionApplyDO> pageResult = certExemptionApplyMapper.selectPageByUserId(pageParam, loginUser.getId());
        List<AppCertExemptionRespVO> list = pageResult.getList().stream().map(item -> {
            AppCertExemptionRespVO respVO = new AppCertExemptionRespVO();
            respVO.setId(item.getId());
            respVO.setExemptionType(item.getExemptionType());
            respVO.setQualificationId(item.getQualificationId());
            respVO.setReason(item.getReason());
            respVO.setAttachmentFileIdsJson(item.getAttachmentFileIdsJson());
            respVO.setAuditStatus(item.getAuditStatus());
            respVO.setAuditRemark(item.getAuditRemark());
            respVO.setRejectReason(item.getRejectReason());
            respVO.setEffectiveStartTime(item.getEffectiveStartTime());
            respVO.setEffectiveEndTime(item.getEffectiveEndTime());
            respVO.setCreateTime(item.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    private AppMemberQualificationRespVO convert(MemberUserQualificationDO qualification) {
        AppMemberQualificationRespVO respVO = new AppMemberQualificationRespVO();
        respVO.setId(qualification.getId());
        respVO.setUserId(qualification.getUserId());
        respVO.setQualificationType(qualification.getQualificationType());
        respVO.setQualificationName(qualification.getQualificationName());
        respVO.setQualificationNo(qualification.getQualificationNo());
        respVO.setFileId(qualification.getFileId());
        respVO.setEvidenceFileIdsJson(qualification.getEvidenceFileIdsJson());
        respVO.setVideoFileId(qualification.getVideoFileId());
        respVO.setValidStartDate(qualification.getValidStartDate());
        respVO.setValidEndDate(qualification.getValidEndDate());
        respVO.setAuditStatus(qualification.getAuditStatus());
        respVO.setAuditRemark(qualification.getAuditRemark());
        respVO.setRejectReason(qualification.getRejectReason());
        respVO.setPriorityEnabled(qualification.getPriorityEnabled());
        respVO.setCreateTime(qualification.getCreateTime());
        return respVO;
    }

    private boolean isExpiringSoon(MemberUserQualificationDO qualification) {
        return qualification != null && qualification.getValidEndDate() != null
                && !qualification.getValidEndDate().isBefore(LocalDate.now())
                && qualification.getValidEndDate().isBefore(LocalDate.now().plusDays(30));
    }

    private boolean isIdCardExpiringSoon(MemberUserRealNameDO realName) {
        return realName != null && realName.getIdCardValidEnd() != null
                && !realName.getIdCardValidEnd().isBefore(LocalDate.now())
                && realName.getIdCardValidEnd().isBefore(LocalDate.now().plusDays(30));
    }

    private MemberUserQualificationDO latestQualificationByType(List<MemberUserQualificationDO> qualifications, String type) {
        return qualifications.stream()
                .filter(item -> Objects.equals(item.getQualificationType(), type))
                .findFirst().orElse(null);
    }

    private MemberUserQualificationDO latestIndustryQualification(List<MemberUserQualificationDO> qualifications) {
        return qualifications.stream()
                .filter(item -> !Objects.equals(item.getQualificationType(), "BUSINESS_LICENSE"))
                .filter(item -> !Objects.equals(item.getQualificationType(), "INSURANCE_POLICY"))
                .findFirst().orElse(null);
    }

    private boolean hasActiveExemption(List<CertExemptionApplyDO> exemptions) {
        LocalDateTime now = LocalDateTime.now();
        return exemptions.stream().anyMatch(item -> Objects.equals(item.getAuditStatus(), "APPROVED")
                && (item.getEffectiveStartTime() == null || !item.getEffectiveStartTime().isAfter(now))
                && (item.getEffectiveEndTime() == null || !item.getEffectiveEndTime().isBefore(now)));
    }

    private String firstNonBlank(String primary, String secondary) {
        return primary != null && !primary.trim().isEmpty() ? primary : secondary;
    }

    private List<MessageRecordDO> selectQualificationReminders(Long userId) {
        return messageRecordMapper.selectList(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getReceiverUserId, userId)
                .eq(MessageRecordDO::getBizType, MessageCenterConstants.BIZ_TYPE_QUALIFICATION_EXPIRY)
                .orderByDesc(MessageRecordDO::getId));
    }

    private PageResult<AppMemberQualificationReminderRespVO> manualPage(List<AppMemberQualificationReminderRespVO> list,
                                                                        Integer pageNo, Integer pageSize) {
        if (list.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int fromIndex = (safePageNo - 1) * safePageSize;
        if (fromIndex >= list.size()) {
            return new PageResult<>(new ArrayList<>(), (long) list.size());
        }
        int toIndex = Math.min(fromIndex + safePageSize, list.size());
        return new PageResult<>(list.subList(fromIndex, toIndex), (long) list.size());
    }

}
