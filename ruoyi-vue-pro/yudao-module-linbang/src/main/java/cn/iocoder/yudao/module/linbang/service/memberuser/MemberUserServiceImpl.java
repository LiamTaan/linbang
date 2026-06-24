package cn.iocoder.yudao.module.linbang.service.memberuser;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberaddress.MemberUserAddressDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrecord.CreditRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberaddress.MemberUserAddressMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_NOT_EXISTS;

/**
 * 用户主表 Service 实现类
 *
 * @author dawn
 */
@Service
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
    private CreditRecordMapper creditRecordMapper;

    @Override
    public Long createMemberUser(MemberUserSaveReqVO createReqVO) {
        MemberUserDO memberUser = MemberUserDO.builder()
                .userNo("LBU" + IdUtil.getSnowflakeNextIdStr())
                .mobile(createReqVO.getMobile())
                .nickname(createReqVO.getNickname())
                .avatar(createReqVO.getAvatar())
                .gender(createReqVO.getGender())
                .birthday(createReqVO.getBirthday())
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

    private void validateMemberUserExists(Long id) {
        if (memberUserMapper.selectById(id) == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
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
        return MemberUserDetailAssembler.build(memberUser, realName, merchant, latestEntry, qualifications, addresses, creditRecords);
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
                    .registerSource(StrUtil.blankToDefault(registerSource, "APP_SMS"))
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
    public void updateMemberUserRole(Long userId, String currentRoleCode) {
        validateMemberUserExists(userId);
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(userId)
                .currentRoleCode(currentRoleCode)
                .build());
    }

    @Override
    public PageResult<MemberUserDO> getMemberUserPage(MemberUserPageReqVO pageReqVO) {
        return memberUserMapper.selectPage(pageReqVO);
    }

}
