package cn.iocoder.yudao.module.linbang.service.memberuser;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.sms.SmsCodeApi;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.blacklist.BlacklistDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberaddress.MemberUserAddressDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberroleapply.MemberRoleApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userrestrictrecord.UserRestrictRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.blacklist.BlacklistMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrecord.CreditRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberaddress.MemberUserAddressMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberroleapply.MemberRoleApplyMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerinfo.PartnerInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userrestrictrecord.UserRestrictRecordMapper;
import cn.iocoder.yudao.module.linbang.service.punishlog.PunishLogWriteService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_MOBILE_DUPLICATED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_RESTRICT_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_USERNAME_DUPLICATED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.USER_RESTRICT_RECORD_NOT_EXISTS;

/**
 * 用户主表 Service 实现类
 *
 * @author dawn
 */
@Service("linbangMemberUserService")
@Validated
public class MemberUserServiceImpl implements MemberUserService {

    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MemberUserAddressMapper memberUserAddressMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private PartnerInfoMapper partnerInfoMapper;
    @Resource
    private MemberRoleApplyMapper memberRoleApplyMapper;
    @Resource
    private CreditRecordMapper creditRecordMapper;
    @Resource
    private UserRestrictRecordMapper userRestrictRecordMapper;
    @Resource
    private PunishLogWriteService punishLogWriteService;
    @Resource
    private BlacklistMapper blacklistMapper;
    @Resource
    private SmsCodeApi smsCodeApi;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Long createMemberUser(MemberUserSaveReqVO createReqVO) {
        MemberUserDO memberUser = MemberUserDO.builder()
                .userNo("LBU" + IdUtil.getSnowflakeNextIdStr())
                .username(createReqVO.getMobile())
                .mobile(createReqVO.getMobile())
                .nickname(createReqVO.getNickname())
                .avatar(createReqVO.getAvatar())
                .gender(createReqVO.getGender())
                .birthday(createReqVO.getBirthday())
                .accountType("PERSONAL")
                .registerSource(StrUtil.blankToDefault(createReqVO.getRegisterSource(), "ADMIN"))
                .currentRoleCode(StrUtil.blankToDefault(createReqVO.getCurrentRoleCode(), "USER"))
                .status(StrUtil.blankToDefault(createReqVO.getStatus(), "ENABLE"))
                .remark(createReqVO.getRemark())
                .build();
        memberUserMapper.insert(memberUser);
        return memberUser.getId();
    }

    @Override
    public void updateMemberUser(MemberUserSaveReqVO updateReqVO) {
        validateMemberUserExists(updateReqVO.getId());
        MemberUserDO updateObj = MemberUserDO.builder()
                .id(updateReqVO.getId())
                .username(updateReqVO.getMobile())
                .mobile(updateReqVO.getMobile())
                .nickname(updateReqVO.getNickname())
                .avatar(updateReqVO.getAvatar())
                .gender(updateReqVO.getGender())
                .birthday(updateReqVO.getBirthday())
                .registerSource(updateReqVO.getRegisterSource())
                .currentRoleCode(updateReqVO.getCurrentRoleCode())
                .status(updateReqVO.getStatus())
                .remark(updateReqVO.getRemark())
                .build();
        memberUserMapper.updateById(updateObj);
    }

    @Override
    public void deleteMemberUser(Long id) {
        // 校验存在
        validateMemberUserExists(id);
        // 删除
        memberUserMapper.deleteById(id);
    }

    @Override
    public void deleteMemberUserListByIds(List<Long> ids) {
        // 删除
        memberUserMapper.deleteByIds(ids);
    }

    private MemberUserDO validateMemberUserExists(Long id) {
        MemberUserDO memberUser = memberUserMapper.selectById(id);
        if (memberUser == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        return memberUser;
    }

    @Override
    public MemberUserDO getMemberUser(Long id) {
        return memberUserMapper.selectById(id);
    }

    @Override
    public MemberUserDetailRespVO getMemberUserDetail(Long id) {
        MemberUserDO memberUser = memberUserMapper.selectById(id);
        if (memberUser == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        MemberUserRealNameDO realName = memberUserRealNameMapper.selectByUserId(id);
        List<MemberUserQualificationDO> qualifications = memberUserQualificationMapper.selectListByUserId(id);
        List<MemberUserAddressDO> addresses = memberUserAddressMapper.selectListByUserId(id);
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, id)
                .last("LIMIT 1"));
        MerchantEntryDO latestEntry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getUserId, id)
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        List<CreditRecordDO> creditRecords = creditRecordMapper.selectList(new LambdaQueryWrapperX<CreditRecordDO>()
                .eq(CreditRecordDO::getUserId, id)
                .orderByDesc(CreditRecordDO::getCreateTime, CreditRecordDO::getId));
        return MemberUserDetailAssembler.build(memberUser, realName, merchant, latestEntry, qualifications, addresses,
                creditRecords, getEnabledRoleCodes(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberUserDO getOrCreateMemberUser(Long authUserId) {
        MemberUserDO memberUser = memberUserMapper.selectById(authUserId);
        if (memberUser != null) {
            return memberUser;
        }
        throw exception(MEMBER_USER_NOT_EXISTS);
    }

    @Override
    public MemberUserDO getMemberUserByMobile(String mobile) {
        return memberUserMapper.selectByMobile(mobile);
    }

    @Override
    public MemberUserDO getMemberUserByUsername(String username) {
        return memberUserMapper.selectByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberUserDO createMemberUserIfAbsent(String mobile, String registerSource) {
        MemberUserDO memberUser = memberUserMapper.selectByMobile(mobile);
        if (memberUser != null) {
            return memberUser;
        }
        try {
            memberUser = MemberUserDO.builder()
                    .userNo("LBU" + IdUtil.getSnowflakeNextIdStr())
                    .mobile(mobile)
                    .nickname("邻里用户" + StrUtil.subSuf(mobile, Math.max(mobile.length() - 4, 0)))
                    .accountType("PERSONAL")
                    .registerSource(StrUtil.blankToDefault(registerSource, "APP_SMS"))
                    .registerSourceDetail(registerSource)
                    .currentRoleCode("USER")
                    .status("ENABLE")
                    .build();
            memberUserMapper.insert(memberUser);
            return memberUser;
        } catch (DuplicateKeyException ex) {
            MemberUserDO concurrent = memberUserMapper.selectByMobileForUpdate(mobile);
            if (concurrent == null) {
                throw ex;
            }
            return concurrent;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberUserDO registerMemberUser(String username, String mobile, String encodedPassword, String accountType,
                                           String registerSource, String registerSourceDetail,
                                           String agreementVersion, LocalDateTime agreementConfirmedTime) {
        MemberUserDO existedByMobile = memberUserMapper.selectByMobile(mobile);
        if (existedByMobile != null) {
            MemberUserDO locked = memberUserMapper.selectByMobileForUpdate(mobile);
            if (locked == null) {
                throw exception(MEMBER_USER_NOT_EXISTS);
            }
            return completeAccountRegistration(locked, username, encodedPassword, accountType, registerSource,
                    registerSourceDetail, agreementVersion, agreementConfirmedTime);
        }
        MemberUserDO memberUser = MemberUserDO.builder()
                .userNo("LBU" + IdUtil.getSnowflakeNextIdStr())
                .username(username)
                .password(encodedPassword)
                .accountType(StrUtil.blankToDefault(accountType, "PERSONAL"))
                .mobile(mobile)
                .nickname("邻里用户" + StrUtil.subSuf(mobile, Math.max(mobile.length() - 4, 0)))
                .registerSource(StrUtil.blankToDefault(registerSource, "APP_ACCOUNT"))
                .registerSourceDetail(registerSourceDetail)
                .registerAgreementVersion(agreementVersion)
                .registerAgreementConfirmedTime(agreementConfirmedTime)
                .currentRoleCode("USER")
                .status("ENABLE")
                .build();
        try {
            memberUserMapper.insert(memberUser);
            return memberUser;
        } catch (DuplicateKeyException ex) {
            MemberUserDO concurrent = memberUserMapper.selectByMobileForUpdate(mobile);
            if (concurrent != null) {
                return completeAccountRegistration(concurrent, username, encodedPassword, accountType, registerSource,
                        registerSourceDetail, agreementVersion, agreementConfirmedTime);
            }
            MemberUserDO usernameOwner = memberUserMapper.selectByUsernameForUpdate(username);
            if (usernameOwner != null) {
                throw exception(MEMBER_USER_USERNAME_DUPLICATED);
            }
            throw ex;
        }
    }

    private MemberUserDO completeAccountRegistration(MemberUserDO memberUser, String username, String encodedPassword,
                                                       String accountType, String registerSource,
                                                       String registerSourceDetail, String agreementVersion,
                                                       LocalDateTime agreementConfirmedTime) {
        if (memberUser.getPassword() != null) {
            throw exception(MEMBER_USER_MOBILE_DUPLICATED);
        }
        MemberUserDO update = MemberUserDO.builder()
                .id(memberUser.getId())
                .username(username)
                .password(encodedPassword)
                .accountType(StrUtil.blankToDefault(accountType, memberUser.getAccountType()))
                .registerSource(StrUtil.blankToDefault(registerSource, memberUser.getRegisterSource()))
                .registerSourceDetail(registerSourceDetail)
                .registerAgreementVersion(agreementVersion)
                .registerAgreementConfirmedTime(agreementConfirmedTime)
                .build();
        try {
            memberUserMapper.updateById(update);
        } catch (DuplicateKeyException ex) {
            MemberUserDO usernameOwner = memberUserMapper.selectByUsernameForUpdate(username);
            if (usernameOwner != null && !usernameOwner.getId().equals(memberUser.getId())) {
                throw exception(MEMBER_USER_USERNAME_DUPLICATED);
            }
            throw ex;
        }
        return memberUserMapper.selectById(memberUser.getId());
    }

    @Override
    public void updateRegisterAgreement(Long userId, String agreementVersion, LocalDateTime confirmedTime) {
        validateMemberUserExists(userId);
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(userId)
                .registerAgreementVersion(agreementVersion)
                .registerAgreementConfirmedTime(confirmedTime)
                .build());
    }

    @Override
    public void updateMemberUserLogin(Long userId, String loginIp) {
        validateMemberUserExists(userId);
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(userId)
                .lastLoginIp(loginIp)
                .lastLoginTime(java.time.LocalDateTime.now())
                .build());
    }

    @Override
    public void updateMemberUserProfile(Long userId, String nickname, String avatar, Integer gender, LocalDate birthday) {
        validateMemberUserExists(userId);
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(userId)
                .nickname(nickname)
                .avatar(avatar)
                .gender(gender)
                .birthday(birthday)
                .build());
    }

    @Override
    public void updateMemberUserNickname(Long userId, String nickname) {
        validateMemberUserExists(userId);
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(userId)
                .nickname(nickname)
                .build());
    }

    @Override
    public void updateMemberUserPassword(Long userId, String password, String code) {
        MemberUserDO user = validateMemberUserExists(userId);
        smsCodeApi.useSmsCode(new SmsCodeUseReqDTO().setMobile(user.getMobile()).setCode(code)
                .setScene(SmsSceneEnum.MEMBER_UPDATE_PASSWORD.getScene()).setUsedIp(getClientIP()));
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(userId)
                .password(passwordEncoder.encode(password))
                .build());
    }

    @Override
    public void updateMemberUserRole(Long userId, String currentRoleCode) {
        validateMemberUserExists(userId);
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(userId)
                .currentRoleCode(currentRoleCode)
                .build());
    }

    @Override
    public void updateMemberUserStatus(Long userId, String status, String remark) {
        validateMemberUserExists(userId);
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(userId)
                .status(status)
                .remark(remark)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restrictMemberUser(MemberUserRestrictReqVO reqVO) {
        MemberUserDO memberUser = memberUserMapper.selectByIdForUpdate(reqVO.getUserId());
        if (memberUser == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        LocalDateTime now = LocalDateTime.now();
        String actionType = reqVO.getActionType().trim().toUpperCase(Locale.ROOT);
        String restrictType = reqVO.getRestrictType().trim().toUpperCase(Locale.ROOT);
        if (!"RESTRICT".equals(actionType) && !"BAN".equals(actionType) && !"BLACKLIST".equals(actionType)) {
            throw exception(MEMBER_USER_RESTRICT_STATUS_INVALID);
        }
        if (reqVO.getEndTime() != null && !reqVO.getEndTime().isAfter(now)) {
            throw exception(MEMBER_USER_RESTRICT_STATUS_INVALID);
        }
        if (userRestrictRecordMapper.selectActive(reqVO.getUserId(), restrictType, now) != null) {
            return;
        }
        UserRestrictRecordDO record = UserRestrictRecordDO.builder()
                .userId(reqVO.getUserId())
                .restrictType(restrictType)
                .status(LinbangRiskConstants.RESTRICT_STATUS_ACTIVE)
                .startTime(now)
                .endTime(reqVO.getEndTime())
                .sourceBizType(actionType)
                .reason(reqVO.getReason())
                .build();
        userRestrictRecordMapper.insert(record);
        punishLogWriteService.createPunishLog(reqVO.getUserId(), actionType + "_" + restrictType,
                record.getStatus(), record.getReason(), actionType, null,
                "USER_RESTRICT_RECORD", record.getId(), loginUserId, now, now, record.getEndTime(), null);
        if ("BAN".equals(actionType)) {
            updateMemberUserStatus(reqVO.getUserId(), "DISABLE", reqVO.getReason());
        }
        if ("BLACKLIST".equals(actionType)) {
            BlacklistDO blacklist = BlacklistDO.builder()
                    .userId(reqVO.getUserId())
                    .blackType(restrictType)
                    .reason(reqVO.getReason())
                    .startTime(now)
                    .endTime(reqVO.getEndTime())
                    .status(LinbangRiskConstants.STATUS_ENABLE)
                    .build();
            blacklistMapper.insert(blacklist);
            punishLogWriteService.createPunishLog(reqVO.getUserId(), "BLACKLIST_" + restrictType,
                    blacklist.getStatus(), blacklist.getReason(), "USER", reqVO.getUserId(),
                    "BLACKLIST_RECORD", blacklist.getId(), loginUserId, now, now, blacklist.getEndTime(), null);
            updateMemberUserStatus(reqVO.getUserId(), "DISABLE", reqVO.getReason());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseMemberUserRestrict(MemberUserReleaseRestrictReqVO reqVO) {
        UserRestrictRecordDO snapshot = userRestrictRecordMapper.selectById(reqVO.getRestrictRecordId());
        if (snapshot == null) {
            throw exception(USER_RESTRICT_RECORD_NOT_EXISTS);
        }
        if (memberUserMapper.selectByIdForUpdate(snapshot.getUserId()) == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        UserRestrictRecordDO record = userRestrictRecordMapper.selectOneForUpdate(
                UserRestrictRecordDO::getId, reqVO.getRestrictRecordId());
        if (record == null) {
            throw exception(USER_RESTRICT_RECORD_NOT_EXISTS);
        }
        if (!"ACTIVE".equalsIgnoreCase(record.getStatus())) {
            throw exception(MEMBER_USER_RESTRICT_STATUS_INVALID);
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        LocalDateTime now = LocalDateTime.now();
        userRestrictRecordMapper.updateById(UserRestrictRecordDO.builder()
                .id(record.getId())
                .status("RELEASED")
                .releasedBy(loginUserId)
                .releasedTime(now)
                .releaseRemark(reqVO.getReleaseRemark())
                .build());
        punishLogWriteService.releasePunishLog("USER_RESTRICT_RECORD", record.getId(),
                LinbangRiskConstants.RESTRICT_STATUS_RELEASED, loginUserId, now, reqVO.getReleaseRemark());
        if ("BLACKLIST".equalsIgnoreCase(record.getSourceBizType())) {
            BlacklistDO blacklist = blacklistMapper.selectEnabledForUpdate(record.getUserId(), record.getRestrictType());
            if (blacklist != null) {
                blacklistMapper.updateById(BlacklistDO.builder()
                        .id(blacklist.getId())
                        .status(LinbangRiskConstants.STATUS_DISABLE)
                        .build());
                punishLogWriteService.releasePunishLog("BLACKLIST_RECORD", blacklist.getId(),
                        LinbangRiskConstants.STATUS_DISABLE, loginUserId, now, reqVO.getReleaseRemark());
            }
        }
        boolean blockingAction = "BAN".equalsIgnoreCase(record.getSourceBizType())
                || "BLACKLIST".equalsIgnoreCase(record.getSourceBizType());
        if (blockingAction
                && userRestrictRecordMapper.selectActiveBlocking(record.getUserId(), now) == null
                && blacklistMapper.selectAnyEffective(record.getUserId(), now) == null) {
            memberUserMapper.updateById(MemberUserDO.builder()
                    .id(record.getUserId())
                    .status("ENABLE")
                    .remark(reqVO.getReleaseRemark())
                    .build());
        }
    }

    @Override
    public PageResult<MemberUserDO> getMemberUserPage(MemberUserPageReqVO pageReqVO) {
        return memberUserMapper.selectPage(pageReqVO);
    }

    @Override
    public List<String> getEnabledRoleCodes(Long userId) {
        Set<String> enabledRoleCodes = new LinkedHashSet<>();
        enabledRoleCodes.add("USER");
        MerchantEntryDO merchantEntry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getUserId, userId)
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        if (merchantEntry != null && "APPROVED".equalsIgnoreCase(merchantEntry.getFinalAuditStatus())) {
            enabledRoleCodes.add("MERCHANT");
        }
        PartnerInfoDO partnerInfo = partnerInfoMapper.selectOne(PartnerInfoDO::getUserId, userId);
        if (partnerInfo != null && "ENABLE".equalsIgnoreCase(partnerInfo.getStatus())) {
            enabledRoleCodes.add("PARTNER");
        }
        List<MemberRoleApplyDO> applies = memberRoleApplyMapper.selectList(new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .eq(MemberRoleApplyDO::getUserId, userId)
                .eq(MemberRoleApplyDO::getAuditStatus, "APPROVED")
                .orderByDesc(MemberRoleApplyDO::getId));
        for (MemberRoleApplyDO apply : applies) {
            if (StrUtil.isNotBlank(apply.getApplyRoleCode())) {
                enabledRoleCodes.add(apply.getApplyRoleCode());
            }
        }
        return new ArrayList<>(enabledRoleCodes);
    }

}
