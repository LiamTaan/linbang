package cn.iocoder.yudao.module.linbang.service.memberqualification;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.certexemption.CertExemptionApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrecord.CreditRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.certexemption.CertExemptionApplyMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.service.merchantinfo.MerchantAccessStateService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_QUALIFICATION_AUDIT_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_QUALIFICATION_NOT_EXISTS;

@Service
@Validated
public class MemberQualificationServiceImpl implements MemberQualificationService {

    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private CreditRecordMapper creditRecordMapper;
    @Resource
    private CertExemptionApplyMapper certExemptionApplyMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private MerchantAccessStateService merchantAccessStateService;

    @Override
    public PageResult<MemberQualificationRespVO> getQualificationPage(MemberQualificationPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<MemberUserQualificationDO> pageResult = memberUserQualificationMapper.selectPage(pageReqVO, matchedUserIds);
        List<MemberQualificationRespVO> list = BeanUtils.toBean(pageResult.getList(), MemberQualificationRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public MemberQualificationDetailRespVO getQualificationDetail(Long id) {
        MemberUserQualificationDO qualification = memberUserQualificationMapper.selectById(id);
        if (qualification == null) {
            throw exception(MEMBER_USER_QUALIFICATION_NOT_EXISTS);
        }
        MemberUserDO user = qualification.getUserId() == null ? null : memberUserMapper.selectById(qualification.getUserId());
        MemberUserRealNameDO realName = qualification.getUserId() == null ? null : memberUserRealNameMapper.selectByUserId(qualification.getUserId());
        MerchantInfoDO merchant = qualification.getUserId() == null ? null : merchantInfoMapper.selectOne(
                new LambdaQueryWrapperX<MerchantInfoDO>()
                        .eq(MerchantInfoDO::getUserId, qualification.getUserId())
                        .last("LIMIT 1"));
        MerchantEntryDO latestEntry = qualification.getUserId() == null ? null : merchantEntryMapper.selectOne(
                new LambdaQueryWrapperX<MerchantEntryDO>()
                        .eq(MerchantEntryDO::getUserId, qualification.getUserId())
                        .orderByDesc(MerchantEntryDO::getCreateTime, MerchantEntryDO::getId)
                        .last("LIMIT 1"));
        List<MemberUserQualificationDO> relatedQualifications = qualification.getUserId() == null ? Collections.emptyList()
                : memberUserQualificationMapper.selectListByUserId(qualification.getUserId());
        List<CertExemptionApplyDO> certExemptions = qualification.getUserId() == null ? Collections.emptyList()
                : certExemptionApplyMapper.selectListByUserId(qualification.getUserId()).stream()
                .filter(item -> Objects.equals(item.getQualificationId(), qualification.getId()))
                .collect(java.util.stream.Collectors.toList());
        List<CreditRecordDO> creditRecords = qualification.getUserId() == null ? Collections.emptyList()
                : creditRecordMapper.selectList(new LambdaQueryWrapperX<CreditRecordDO>()
                .eq(CreditRecordDO::getUserId, qualification.getUserId())
                .orderByDesc(CreditRecordDO::getCreateTime, CreditRecordDO::getId));
        return MemberQualificationDetailAssembler.build(qualification, user, realName, merchant, latestEntry,
                relatedQualifications, certExemptions, creditRecords);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditQualification(MemberQualificationAuditReqVO reqVO) {
        MemberUserQualificationDO qualification = memberUserQualificationMapper.selectByIdForUpdate(reqVO.getId());
        if (qualification == null) {
            throw exception(MEMBER_USER_QUALIFICATION_NOT_EXISTS);
        }
        String auditStatus = StrUtil.trimToEmpty(reqVO.getAuditStatus()).toUpperCase(java.util.Locale.ROOT);
        if (!"PENDING".equals(qualification.getAuditStatus())
                || (!"APPROVED".equals(auditStatus) && !"REJECTED".equals(auditStatus))) {
            throw exception(MEMBER_USER_QUALIFICATION_AUDIT_STATUS_INVALID);
        }
        if ("REJECTED".equals(auditStatus) && StrUtil.isBlank(reqVO.getRejectReason())) {
            throw exception(MEMBER_USER_QUALIFICATION_AUDIT_STATUS_INVALID);
        }
        boolean approved = Objects.equals(auditStatus, "APPROVED");
        MemberUserQualificationDO updateObj = new MemberUserQualificationDO();
        updateObj.setId(reqVO.getId());
        updateObj.setAuditStatus(auditStatus);
        updateObj.setAuditRemark(reqVO.getAuditRemark());
        updateObj.setRejectReason(approved ? null : reqVO.getRejectReason());
        updateObj.setAuditBy(SecurityFrameworkUtils.getLoginUserId());
        updateObj.setAuditTime(LocalDateTime.now());
        updateObj.setPriorityEnabled(approved
                && Arrays.asList("PLATFORM_CLOTHING", "TOOLBOX").contains(qualification.getQualificationType()));
        memberUserQualificationMapper.updateById(updateObj);
        merchantAccessStateService.refreshMerchantAcceptStatus(qualification.getUserId());
        messagePushDispatchService.dispatchSingleIdempotent("lb_special_cert_audited", "专项资质审核结果通知",
                "MEMBER_QUALIFICATION", qualification.getId(), qualification.getUserId(), "专项资质审核通知",
                "lb_special_cert_audited:" + qualification.getId() + ":" + auditStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recomputePriorityEnabled(Long id) {
        MemberUserQualificationDO qualification = memberUserQualificationMapper.selectById(id);
        if (qualification == null) {
            throw exception(MEMBER_USER_QUALIFICATION_NOT_EXISTS);
        }
        boolean enabled = Objects.equals(qualification.getAuditStatus(), "APPROVED")
                && Arrays.asList("PLATFORM_CLOTHING", "TOOLBOX").contains(qualification.getQualificationType());
        memberUserQualificationMapper.updateById(MemberUserQualificationDO.builder()
                .id(id)
                .priorityEnabled(enabled)
                .build());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<MemberQualificationRespVO> list) {
        java.util.Set<Long> userIds = convertSet(list, MemberQualificationRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
                return;
            }
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
        });
    }

}
