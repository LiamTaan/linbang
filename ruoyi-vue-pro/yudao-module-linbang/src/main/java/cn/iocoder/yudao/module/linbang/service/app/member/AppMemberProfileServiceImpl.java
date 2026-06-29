package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.module.linbang.controller.app.member.user.vo.AppMemberProfileRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext.vo.AppMemberRoleContextRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.user.vo.AppMemberProfileUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditlevelbenefit.CreditLevelBenefitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditLevelBenefitService;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditLevelResolver;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.linbang.service.reviewcomment.MerchantReviewMetricsResp;
import cn.iocoder.yudao.module.linbang.service.reviewcomment.ReviewCommentMetricsService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class AppMemberProfileServiceImpl implements AppMemberProfileService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private CreditLevelBenefitService creditLevelBenefitService;
    @Resource
    private ReviewCommentMetricsService reviewCommentMetricsService;
    @Resource
    private AppMemberRoleContextService appMemberRoleContextService;

    @Override
    public AppMemberProfileRespVO getProfile(Long authUserId) {
        MemberUserDO user = memberUserService.getOrCreateMemberUser(authUserId);
        MemberUserRealNameDO realName = memberUserRealNameMapper.selectByUserId(user.getId());
        MerchantInfoDO merchantInfo = merchantInfoMapper.selectOne(MerchantInfoDO::getUserId, authUserId);
        int creditScore = merchantInfo != null && merchantInfo.getCreditScore() != null ? merchantInfo.getCreditScore() : 100;
        String creditLevel = merchantInfo != null && merchantInfo.getCreditLevel() != null
                ? merchantInfo.getCreditLevel() : CreditLevelResolver.resolve(creditScore);
        MerchantReviewMetricsResp metrics = merchantInfo != null
                ? reviewCommentMetricsService.calculateMerchantMetrics(merchantInfo.getId()) : null;
        List<CreditLevelBenefitDO> benefits = creditLevelBenefitService.getEnabledBenefits();

        AppMemberProfileRespVO respVO = new AppMemberProfileRespVO();
        respVO.setId(user.getId());
        respVO.setUserNo(user.getUserNo());
        respVO.setMobile(user.getMobile());
        respVO.setNickname(user.getNickname());
        respVO.setAvatar(user.getAvatar());
        respVO.setCurrentRoleCode(user.getCurrentRoleCode());
        AppMemberRoleContextRespVO roleContext = appMemberRoleContextService.getRoleContext(authUserId);
        respVO.setCurrentRoleName(roleContext.getCurrentRoleName());
        respVO.setEnabledRoleCodes(roleContext.getEnabledRoleCodes());
        respVO.setRoleSummaries(roleContext.getRoleSummaries().stream().map(item -> {
            AppMemberProfileRespVO.RoleSummaryItem roleSummaryItem = new AppMemberProfileRespVO.RoleSummaryItem();
            roleSummaryItem.setRoleCode(item.getRoleCode());
            roleSummaryItem.setRoleName(item.getRoleName());
            roleSummaryItem.setRoleStatus(item.getRoleStatus());
            roleSummaryItem.setCurrent(item.getCurrent());
            roleSummaryItem.setSwitchable(item.getSwitchable());
            roleSummaryItem.setPermissionDesc(item.getPermissionDesc());
            return roleSummaryItem;
        }).collect(Collectors.toList()));
        respVO.setRealNameStatus(realName != null ? realName.getAuditStatus() : null);
        respVO.setCreditScore(creditScore);
        respVO.setCreditLevel(creditLevel);
        respVO.setNextLevelCode(CreditLevelResolver.nextLevel(creditLevel));
        respVO.setNextLevelNeedScore(CreditLevelResolver.nextLevelNeedScore(creditScore));
        respVO.setCompositeScore(metrics != null ? metrics.getCompositeScore() : BigDecimal.ZERO);
        respVO.setPositiveRate(metrics != null ? metrics.getPositiveRate() : BigDecimal.ZERO);
        respVO.setInPositivePriorityPool(metrics != null ? metrics.getInPositivePriorityPool() : Boolean.FALSE);
        respVO.setBenefits(benefits.stream().map(item -> {
            AppMemberProfileRespVO.CreditBenefitItem benefitItem = new AppMemberProfileRespVO.CreditBenefitItem();
            benefitItem.setLevelCode(item.getLevelCode());
            benefitItem.setLevelName(item.getLevelName());
            benefitItem.setBenefitTitle(item.getBenefitTitle());
            benefitItem.setBenefitDesc(item.getBenefitDesc());
            return benefitItem;
        }).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public void updateProfile(Long authUserId, AppMemberProfileUpdateReqVO reqVO) {
        memberUserService.updateMemberUserProfile(authUserId, reqVO.getNickname(), reqVO.getAvatar(),
                reqVO.getGender(), reqVO.getBirthday());
    }
}
