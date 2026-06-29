package cn.iocoder.yudao.module.linbang.service.app.benefit;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.benefit.vo.AppBenefitOverviewRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditlevelbenefit.CreditLevelBenefitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberpoint.MemberPointRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditLevelBenefitService;
import cn.iocoder.yudao.module.linbang.service.match.ShowcaseRewardService;
import cn.iocoder.yudao.module.linbang.service.memberpoint.MemberPointRecordService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Validated
public class AppBenefitServiceImpl implements AppBenefitService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private CreditLevelBenefitService creditLevelBenefitService;
    @Resource
    private PromoterMapper promoterMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private ShowcaseRewardService showcaseRewardService;
    @Resource
    private MemberPointRecordService memberPointRecordService;

    @Override
    public AppBenefitOverviewRespVO getBenefitOverview(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        AppBenefitOverviewRespVO respVO = new AppBenefitOverviewRespVO();
        respVO.setCouponCount(0);
        respVO.setPointBalance(resolvePointBalance(loginUser));
        respVO.setPriorityDisplayEnabled(resolvePriorityDisplayEnabled(loginUser));
        respVO.setPriorityDispatchEnabled(Boolean.FALSE);
        respVO.setPromoterBenefitEnabled(promoterMapper.selectByUserId(loginUser.getId()) != null);
        respVO.setBenefits(buildBenefits(loginUser));
        respVO.setRecentPointRecords(buildRecentPointRecords(loginUser));
        return respVO;
    }

    private Integer resolvePointBalance(MemberUserDO loginUser) {
        return memberPointRecordService.getUserPointBalance(loginUser.getId());
    }

    private Boolean resolvePriorityDisplayEnabled(MemberUserDO loginUser) {
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId())
                .last("LIMIT 1"));
        return merchant != null && showcaseRewardService.hasActiveReward(merchant.getId());
    }

    private List<AppBenefitOverviewRespVO.BenefitItemRespVO> buildBenefits(MemberUserDO loginUser) {
        List<AppBenefitOverviewRespVO.BenefitItemRespVO> list = new ArrayList<>();
        for (CreditLevelBenefitDO item : creditLevelBenefitService.getEnabledBenefits()) {
            AppBenefitOverviewRespVO.BenefitItemRespVO benefit = new AppBenefitOverviewRespVO.BenefitItemRespVO();
            benefit.setBenefitType("CREDIT_LEVEL");
            benefit.setBenefitTitle(item.getBenefitTitle());
            benefit.setBenefitDesc(item.getBenefitDesc());
            list.add(benefit);
            if (list.size() >= 4) {
                break;
            }
        }
        if (Boolean.TRUE.equals(resolvePriorityDisplayEnabled(loginUser))) {
            AppBenefitOverviewRespVO.BenefitItemRespVO benefit = new AppBenefitOverviewRespVO.BenefitItemRespVO();
            benefit.setBenefitType("PRIORITY_DISPLAY");
            benefit.setBenefitTitle("优先展示中");
            benefit.setBenefitDesc("当前服务商悬赏审核通过，享有有限期优先展示资格");
            list.add(benefit);
        }
        PromoterDO promoter = promoterMapper.selectByUserId(loginUser.getId());
        if (promoter != null) {
            AppBenefitOverviewRespVO.BenefitItemRespVO benefit = new AppBenefitOverviewRespVO.BenefitItemRespVO();
            benefit.setBenefitType("PROMOTER");
            benefit.setBenefitTitle("推广身份已开通");
            benefit.setBenefitDesc("可查看推广收益、团队数据与两级分销统计");
            list.add(benefit);
        }
        AppBenefitOverviewRespVO.BenefitItemRespVO pointBenefit = new AppBenefitOverviewRespVO.BenefitItemRespVO();
        pointBenefit.setBenefitType("POINT");
        pointBenefit.setBenefitTitle("积分权益");
        pointBenefit.setBenefitDesc("当前仓库已接入积分余额与积分记录，优惠券能力待营销模块接入后扩展");
        list.add(pointBenefit);
        return list;
    }

    private List<AppBenefitOverviewRespVO.PointRecordSimpleRespVO> buildRecentPointRecords(MemberUserDO loginUser) {
        cn.iocoder.yudao.framework.common.pojo.PageParam reqVO = new cn.iocoder.yudao.framework.common.pojo.PageParam();
        reqVO.setPageNo(1);
        reqVO.setPageSize(5);
        PageResult<MemberPointRecordDO> pageResult = memberPointRecordService.getPointRecordPage(loginUser.getId(), reqVO);
        List<AppBenefitOverviewRespVO.PointRecordSimpleRespVO> list = new ArrayList<>();
        for (MemberPointRecordDO record : pageResult.getList()) {
            AppBenefitOverviewRespVO.PointRecordSimpleRespVO item = new AppBenefitOverviewRespVO.PointRecordSimpleRespVO();
            item.setId(record.getId());
            item.setTitle(record.getTitle());
            item.setDescription(record.getDescription());
            item.setPoint(record.getPoint());
            item.setCreateTime(record.getCreateTime());
            list.add(item);
        }
        return list;
    }
}
