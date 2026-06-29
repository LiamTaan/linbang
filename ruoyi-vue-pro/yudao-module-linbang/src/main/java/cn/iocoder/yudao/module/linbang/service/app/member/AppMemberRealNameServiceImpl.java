package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameProgressRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameStartVerifyRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.identityverifylog.IdentityVerifyLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.identityverifylog.IdentityVerifyLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.service.memberrealname.IdentityVerifyFacade;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.system.api.social.SocialUserApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserRespDTO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Validated
public class AppMemberRealNameServiceImpl implements AppMemberRealNameService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private IdentityVerifyLogMapper identityVerifyLogMapper;
    @Resource
    private IdentityVerifyFacade identityVerifyFacade;
    @Resource
    private SocialUserApi socialUserApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppMemberRealNameStartVerifyRespVO startVerify(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserRealNameDO record = memberUserRealNameMapper.selectByUserId(loginUser.getId());
        if (record == null) {
            record = MemberUserRealNameDO.builder()
                    .userId(loginUser.getId())
                    .auditStatus("PENDING")
                    .wechatRealNameStatus(resolveChannelStatus(loginUser.getId(), SocialTypeEnum.WECHAT_OPEN.getType()))
                    .alipayRealNameStatus(resolveChannelStatus(loginUser.getId(), SocialTypeEnum.ALIPAY_MINI_PROGRAM.getType()))
                    .build();
            memberUserRealNameMapper.insert(record);
        }
        LocalDateTime now = LocalDateTime.now();
        memberUserRealNameMapper.updateById(MemberUserRealNameDO.builder()
                .id(record.getId())
                .verifyStartedTime(now)
                .verifyCompletedTime(null)
                .verifyFailReason(null)
                .rejectReason(null)
                .auditRemark(null)
                .auditBy(null)
                .auditTime(null)
                .auditStatus("PENDING")
                .build());
        MemberUserRealNameDO latest = memberUserRealNameMapper.selectById(record.getId());
        IdentityVerifyFacade.IdentityVerifyResult result = identityVerifyFacade.startVerify(latest);
        memberUserRealNameMapper.updateById(MemberUserRealNameDO.builder()
                .id(latest.getId())
                .verifyProvider(result.getVerifyProvider())
                .verifyFlowNo(result.getVerifyFlowNo())
                .livenessResult(result.getLivenessStatus())
                .faceVerifyResult(result.getFaceVerifyStatus())
                .verifyFailReason(result.getFailReason())
                .verifyCompletedTime("PASS".equals(result.getVerifyStatus()) ? LocalDateTime.now() : null)
                .build());
        identityVerifyLogMapper.insert(IdentityVerifyLogDO.builder()
                .userId(loginUser.getId())
                .realNameId(latest.getId())
                .verifyFlowNo(result.getVerifyFlowNo())
                .verifyProvider(result.getVerifyProvider())
                .verifyStage("SUBMITTED")
                .verifyStatus(result.getVerifyStatus())
                .requestSnapshot("REAL_NAME_START")
                .responseSnapshot("LIVENESS=" + result.getLivenessStatus() + ",FACE=" + result.getFaceVerifyStatus())
                .failReason(result.getFailReason())
                .build());
        AppMemberRealNameStartVerifyRespVO respVO = new AppMemberRealNameStartVerifyRespVO();
        respVO.setRealNameId(latest.getId());
        respVO.setVerifyFlowNo(result.getVerifyFlowNo());
        respVO.setVerifyProvider(result.getVerifyProvider());
        respVO.setVerifyStage("SUBMITTED");
        respVO.setVerifyStatus(result.getVerifyStatus());
        respVO.setVerifyStartedTime(now);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrUpdateRealName(Long authUserId, AppMemberRealNameCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserRealNameDO existed = memberUserRealNameMapper.selectByUserId(loginUser.getId());
        if (existed == null) {
            MemberUserRealNameDO record = MemberUserRealNameDO.builder()
                    .userId(loginUser.getId())
                    .realName(reqVO.getRealName())
                    .idCardNo(reqVO.getIdCardNo())
                    .idCardFrontFileId(reqVO.getIdCardFrontFileId())
                    .idCardBackFileId(reqVO.getIdCardBackFileId())
                    .holdCardFileId(reqVO.getHoldCardFileId())
                    .holdCardVideoFileId(reqVO.getHoldCardVideoFileId())
                    .idCardValidFrom(reqVO.getIdCardValidFrom())
                    .idCardValidEnd(reqVO.getIdCardValidEnd())
                    .wechatRealNameStatus(resolveChannelStatus(loginUser.getId(), SocialTypeEnum.WECHAT_OPEN.getType()))
                    .alipayRealNameStatus(resolveChannelStatus(loginUser.getId(), SocialTypeEnum.ALIPAY_MINI_PROGRAM.getType()))
                    .auditStatus("PENDING")
                    .build();
            memberUserRealNameMapper.insert(record);
            return record.getId();
        }

        memberUserRealNameMapper.updateById(MemberUserRealNameDO.builder()
                .id(existed.getId())
                .realName(reqVO.getRealName())
                .idCardNo(reqVO.getIdCardNo())
                .idCardFrontFileId(reqVO.getIdCardFrontFileId())
                .idCardBackFileId(reqVO.getIdCardBackFileId())
                .holdCardFileId(reqVO.getHoldCardFileId())
                .holdCardVideoFileId(reqVO.getHoldCardVideoFileId())
                .idCardValidFrom(reqVO.getIdCardValidFrom())
                .idCardValidEnd(reqVO.getIdCardValidEnd())
                .livenessResult(null)
                .faceVerifyResult(null)
                .wechatRealNameStatus(resolveChannelStatus(loginUser.getId(), SocialTypeEnum.WECHAT_OPEN.getType()))
                .alipayRealNameStatus(resolveChannelStatus(loginUser.getId(), SocialTypeEnum.ALIPAY_MINI_PROGRAM.getType()))
                .verifyProvider(null)
                .verifyFlowNo(null)
                .verifyStartedTime(null)
                .verifyCompletedTime(null)
                .verifyFailReason(null)
                .auditStatus("PENDING")
                .auditRemark(null)
                .auditBy(null)
                .auditTime(null)
                .rejectReason(null)
                .build());
        return existed.getId();
    }

    @Override
    public AppMemberRealNameRespVO getRealName(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserRealNameDO record = memberUserRealNameMapper.selectByUserId(loginUser.getId());
        if (record == null) {
            return null;
        }
        AppMemberRealNameRespVO respVO = new AppMemberRealNameRespVO();
        respVO.setId(record.getId());
        respVO.setUserId(record.getUserId());
        respVO.setRealName(record.getRealName());
        respVO.setIdCardNo(record.getIdCardNo());
        respVO.setIdCardFrontFileId(record.getIdCardFrontFileId());
        respVO.setIdCardBackFileId(record.getIdCardBackFileId());
        respVO.setHoldCardFileId(record.getHoldCardFileId());
        respVO.setHoldCardVideoFileId(record.getHoldCardVideoFileId());
        respVO.setIdCardValidFrom(record.getIdCardValidFrom());
        respVO.setIdCardValidEnd(record.getIdCardValidEnd());
        respVO.setLivenessResult(record.getLivenessResult());
        respVO.setFaceVerifyResult(record.getFaceVerifyResult());
        respVO.setWechatRealNameStatus(record.getWechatRealNameStatus());
        respVO.setAlipayRealNameStatus(record.getAlipayRealNameStatus());
        respVO.setVerifyProvider(record.getVerifyProvider());
        respVO.setVerifyFlowNo(record.getVerifyFlowNo());
        respVO.setVerifyStartedTime(record.getVerifyStartedTime());
        respVO.setVerifyCompletedTime(record.getVerifyCompletedTime());
        respVO.setVerifyFailReason(record.getVerifyFailReason());
        respVO.setAuditStatus(record.getAuditStatus());
        respVO.setAuditRemark(record.getAuditRemark());
        respVO.setRejectReason(record.getRejectReason());
        respVO.setCreateTime(record.getCreateTime());
        return respVO;
    }

    @Override
    public AppMemberRealNameProgressRespVO getProgress(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserRealNameDO record = memberUserRealNameMapper.selectByUserId(loginUser.getId());
        if (record == null) {
            return null;
        }
        AppMemberRealNameProgressRespVO respVO = new AppMemberRealNameProgressRespVO();
        respVO.setRealNameId(record.getId());
        respVO.setAuditStatus(record.getAuditStatus());
        respVO.setLivenessStatus(record.getLivenessResult());
        respVO.setFaceVerifyStatus(record.getFaceVerifyResult());
        respVO.setWechatRealNameStatus(record.getWechatRealNameStatus());
        respVO.setAlipayRealNameStatus(record.getAlipayRealNameStatus());
        respVO.setRejectReason(record.getRejectReason());
        respVO.setVerifyFailReason(record.getVerifyFailReason());
        respVO.setCanResubmit(Objects.equals(record.getAuditStatus(), "REJECTED")
                || Objects.equals(record.getLivenessResult(), "FAIL")
                || Objects.equals(record.getFaceVerifyResult(), "FAIL"));
        respVO.setVerifyStartedTime(record.getVerifyStartedTime());
        respVO.setVerifyCompletedTime(record.getVerifyCompletedTime());
        respVO.setUpdateTime(record.getUpdateTime());
        if (Objects.equals(record.getAuditStatus(), "APPROVED")) {
            respVO.setCurrentStepTitle("实名认证已通过");
            respVO.setCurrentStepDesc("您的实名认证资料、活体检测和人脸核验已全部通过");
        } else if (Objects.equals(record.getAuditStatus(), "REJECTED")) {
            respVO.setCurrentStepTitle("实名认证已驳回");
            respVO.setCurrentStepDesc("请根据驳回原因修正资料后重新提交");
        } else if (Objects.equals(record.getLivenessResult(), "FAIL") || Objects.equals(record.getFaceVerifyResult(), "FAIL")) {
            respVO.setCurrentStepTitle("实名认证核验失败");
            respVO.setCurrentStepDesc("人脸或活体校验未通过，请重新提交资料");
        } else if (record.getVerifyStartedTime() != null) {
            respVO.setCurrentStepTitle("实名认证资料已提交");
            respVO.setCurrentStepDesc("平台正在校验您的身份证、人脸和活体信息");
        } else {
            respVO.setCurrentStepTitle("待提交实名认证资料");
            respVO.setCurrentStepDesc("请上传身份证正反面、手持照片和手持视频后发起核验");
        }
        return respVO;
    }

    private String resolveChannelStatus(Long userId, Integer socialType) {
        SocialUserRespDTO socialUser = socialUserApi.getSocialUserByUserId(UserTypeEnum.MEMBER.getValue(), userId, socialType);
        return socialUser == null ? "UNBOUND" : "MATCHED";
    }
}
