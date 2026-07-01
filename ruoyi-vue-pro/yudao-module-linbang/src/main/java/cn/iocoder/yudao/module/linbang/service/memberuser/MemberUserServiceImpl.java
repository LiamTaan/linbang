package cn.iocoder.yudao.module.linbang.service.memberuser;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.sms.SmsCodeApi;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
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
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_RESTRICT_STATUS_INVALID;
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
                    .username(mobile)
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
            return memberUserMapper.selectByMobile(mobile);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MemberUserDO registerMemberUser(String username, String mobile, String encodedPassword, String accountType,
                                           String registerSource, String registerSourceDetail,
                                           String agreementVersion, LocalDateTime agreementConfirmedTime) {
        MemberUserDO existedByMobile = memberUserMapper.selectByMobile(mobile);
        if (existedByMobile != null) {
            MemberUserDO update = MemberUserDO.builder()
                    .id(existedByMobile.getId())
                    .username(StrUtil.blankToDefault(existedByMobile.getUsername(), username))
                    .password(StrUtil.blankToDefault(existedByMobile.getPassword(), encodedPassword))
                    .accountType(StrUtil.blankToDefault(accountType, existedByMobile.getAccountType()))
                    .registerSource(StrUtil.blankToDefault(registerSource, existedByMobile.getRegisterSource()))
                    .registerSourceDetail(registerSourceDetail)
                    .registerAgreementVersion(agreementVersion)
                    .registerAgreementConfirmedTime(agreementConfirmedTime)
                    .build();
            memberUserMapper.updateById(update);
            return memberUserMapper.selectById(existedByMobile.getId());
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
        memberUserMapper.insert(memberUser);
        return memberUser;
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
        validateMemberUserExists(reqVO.getUserId());
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        LocalDateTime now = LocalDateTime.now();
        UserRestrictRecordDO record = UserRestrictRecordDO.builder()
                .userId(reqVO.getUserId())
                .restrictType(reqVO.getRestrictType())
                .status("ACTIVE")
                .startTime(now)
                .endTime(reqVO.getEndTime())
                .sourceBizType(reqVO.getActionType())
                .reason(reqVO.getReason())
                .build();
        userRestrictRecordMapper.insert(record);
        if ("BAN".equalsIgnoreCase(reqVO.getActionType())) {
            updateMemberUserStatus(reqVO.getUserId(), "DISABLE", reqVO.getReason());
        }
        if ("BLACKLIST".equalsIgnoreCase(reqVO.getActionType())) {
            blacklistMapper.insert(BlacklistDO.builder()
                    .userId(reqVO.getUserId())
                    .blackType(reqVO.getRestrictType())
                    .reason(reqVO.getReason())
                    .startTime(now)
                    .endTime(reqVO.getEndTime())
                    .status("ACTIVE")
                    .build());
            updateMemberUserStatus(reqVO.getUserId(), "DISABLE", reqVO.getReason());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseMemberUserRestrict(MemberUserReleaseRestrictReqVO reqVO) {
        UserRestrictRecordDO record = userRestrictRecordMapper.selectById(reqVO.getRestrictRecordId());
        if (record == null) {
            throw exception(USER_RESTRICT_RECORD_NOT_EXISTS);
        }
        if (!"ACTIVE".equalsIgnoreCase(record.getStatus())) {
            throw exception(MEMBER_USER_RESTRICT_STATUS_INVALID);
        }
        userRestrictRecordMapper.updateById(UserRestrictRecordDO.builder()
                .id(record.getId())
                .status("RELEASED")
                .releasedBy(SecurityFrameworkUtils.getLoginUserId())
                .releasedTime(LocalDateTime.now())
                .releaseRemark(reqVO.getReleaseRemark())
                .build());
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(record.getUserId())
                .status("ENABLE")
                .remark(reqVO.getReleaseRemark())
                .build());
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
