package cn.iocoder.yudao.module.linbang.service.memberrealname;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrecord.CreditRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditRecordService;
import cn.iocoder.yudao.module.linbang.service.merchantinfo.MerchantAccessStateService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 实名认证表 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class MemberUserRealNameServiceImpl implements MemberUserRealNameService {

    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private CreditRecordMapper creditRecordMapper;
    @Resource
    private CreditRecordService creditRecordService;
    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private MerchantAccessStateService merchantAccessStateService;

    @Override
    public Long createMemberUserRealName(MemberUserRealNameSaveReqVO createReqVO) {
        // 插入
        MemberUserRealNameDO memberUserRealName = BeanUtils.toBean(createReqVO, MemberUserRealNameDO.class);
        memberUserRealNameMapper.insert(memberUserRealName);

        // 返回
        return memberUserRealName.getId();
    }

    @Override
    public void updateMemberUserRealName(MemberUserRealNameSaveReqVO updateReqVO) {
        // 校验存在
        validateMemberUserRealNameExists(updateReqVO.getId());
        // 更新
        MemberUserRealNameDO updateObj = BeanUtils.toBean(updateReqVO, MemberUserRealNameDO.class);
        memberUserRealNameMapper.updateById(updateObj);
    }

    @Override
    public void deleteMemberUserRealName(Long id) {
        // 校验存在
        validateMemberUserRealNameExists(id);
        // 删除
        memberUserRealNameMapper.deleteById(id);
    }

    @Override
        public void deleteMemberUserRealNameListByIds(List<Long> ids) {
        // 删除
        memberUserRealNameMapper.deleteByIds(ids);
        }


    private void validateMemberUserRealNameExists(Long id) {
        if (memberUserRealNameMapper.selectById(id) == null) {
            throw exception(MEMBER_USER_REAL_NAME_NOT_EXISTS);
        }
    }

    @Override
    public MemberUserRealNameDO getMemberUserRealName(Long id) {
        return memberUserRealNameMapper.selectById(id);
    }

    @Override
    public MemberUserRealNameDetailRespVO getMemberUserRealNameDetail(Long id) {
        MemberUserRealNameDO realName = memberUserRealNameMapper.selectById(id);
        if (realName == null) {
            throw exception(MEMBER_USER_REAL_NAME_NOT_EXISTS);
        }
        MemberUserDO user = realName.getUserId() == null ? null : memberUserMapper.selectById(realName.getUserId());
        MerchantInfoDO merchant = realName.getUserId() == null ? null : merchantInfoMapper.selectOne(
                new LambdaQueryWrapperX<MerchantInfoDO>()
                        .eq(MerchantInfoDO::getUserId, realName.getUserId())
                        .last("LIMIT 1"));
        MerchantEntryDO latestEntry = realName.getUserId() == null ? null : merchantEntryMapper.selectOne(
                new LambdaQueryWrapperX<MerchantEntryDO>()
                        .eq(MerchantEntryDO::getUserId, realName.getUserId())
                        .orderByDesc(MerchantEntryDO::getCreateTime, MerchantEntryDO::getId)
                        .last("LIMIT 1"));
        List<MemberUserQualificationDO> qualifications = realName.getUserId() == null ? Collections.emptyList()
                : memberUserQualificationMapper.selectListByUserId(realName.getUserId());
        List<CreditRecordDO> creditRecords = realName.getUserId() == null ? Collections.emptyList()
                : creditRecordMapper.selectList(new LambdaQueryWrapperX<CreditRecordDO>()
                .eq(CreditRecordDO::getUserId, realName.getUserId())
                .orderByDesc(CreditRecordDO::getCreateTime, CreditRecordDO::getId));
        return MemberUserRealNameDetailAssembler.build(realName, user, merchant, latestEntry, qualifications, creditRecords);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMemberUserRealName(MemberUserRealNameAuditReqVO reqVO) {
        MemberUserRealNameDO snapshot = memberUserRealNameMapper.selectById(reqVO.getId());
        if (snapshot == null) {
            throw exception(MEMBER_USER_REAL_NAME_NOT_EXISTS);
        }
        MemberUserDO user = memberUserMapper.selectByIdForUpdate(snapshot.getUserId());
        if (user == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        MemberUserRealNameDO record = memberUserRealNameMapper.selectByIdForUpdate(reqVO.getId());
        if (record == null || !Objects.equals(record.getUserId(), user.getId())) {
            throw exception(MEMBER_USER_REAL_NAME_NOT_EXISTS);
        }
        String auditStatus = StrUtil.trimToEmpty(reqVO.getAuditStatus()).toUpperCase(Locale.ROOT);
        if (!"PENDING".equals(record.getAuditStatus())
                || (!"APPROVED".equals(auditStatus) && !"REJECTED".equals(auditStatus))) {
            throw exception(MEMBER_USER_REAL_NAME_STATUS_INVALID);
        }
        if ("APPROVED".equals(auditStatus)
                && (!"PASS".equals(record.getLivenessResult()) || !"PASS".equals(record.getFaceVerifyResult()))) {
            throw exception(MEMBER_USER_REAL_NAME_STATUS_INVALID);
        }
        if ("REJECTED".equals(auditStatus) && StrUtil.isBlank(reqVO.getRejectReason())) {
            throw exception(MEMBER_USER_REAL_NAME_STATUS_INVALID);
        }
        LocalDateTime now = LocalDateTime.now();
        MemberUserRealNameDO updateObj = new MemberUserRealNameDO();
        updateObj.setId(reqVO.getId());
        updateObj.setAuditStatus(auditStatus);
        updateObj.setAuditRemark(reqVO.getAuditRemark());
        updateObj.setRejectReason("REJECTED".equals(auditStatus) ? reqVO.getRejectReason() : null);
        updateObj.setAuditBy(SecurityFrameworkUtils.getLoginUserId());
        updateObj.setAuditTime(now);
        memberUserRealNameMapper.updateById(updateObj);
        if ("APPROVED".equals(auditStatus)) {
            memberUserService.updateMemberUserNickname(record.getUserId(), record.getRealName());
            creditRecordService.applyCreditRule(record.getUserId(), null, "REAL_NAME_APPROVED",
                    "REAL_NAME", record.getId(), "实名认证审核通过");
        }
        merchantAccessStateService.refreshMerchantAcceptStatus(record.getUserId());
        messagePushDispatchService.dispatchSingle("",
                "APPROVED".equals(auditStatus) ? "实名认证审核已通过" : "实名认证审核已驳回",
                "REAL_NAME", record.getId(), record.getUserId(), "实名认证审核结果已更新");
    }

    @Override
    public PageResult<MemberUserRealNameRespVO> getMemberUserRealNamePage(MemberUserRealNamePageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<MemberUserRealNameDO> pageResult = memberUserRealNameMapper.selectPage(pageReqVO, matchedUserIds);
        List<MemberUserRealNameRespVO> list = BeanUtils.toBean(pageResult.getList(), MemberUserRealNameRespVO.class);
        list.forEach(item -> item.setIdCardNo(maskIdCardNo(item.getIdCardNo())));
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<MemberUserRealNameRespVO> list) {
        Set<Long> userIds = convertSet(list, MemberUserRealNameRespVO::getUserId,
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

    private String maskIdCardNo(String idCardNo) {
        if (StrUtil.isBlank(idCardNo)) {
            return idCardNo;
        }
        if (idCardNo.length() <= 4) {
            return "****";
        }
        return idCardNo.substring(0, 2) + "********" + idCardNo.substring(idCardNo.length() - 2);
    }

}
