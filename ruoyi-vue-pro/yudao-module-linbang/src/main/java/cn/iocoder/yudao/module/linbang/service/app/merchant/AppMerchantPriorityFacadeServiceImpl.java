package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.prioritypool.vo.AppPriorityPoolCurrentRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.prioritypoolrecord.PriorityPoolRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.prioritypoolrecord.PriorityPoolRecordMapper;
import cn.iocoder.yudao.module.linbang.service.match.ShowcaseRewardService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_INFO_NOT_EXISTS;

@Service
@Validated
public class AppMerchantPriorityFacadeServiceImpl implements AppMerchantPriorityFacadeService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private PriorityPoolRecordMapper priorityPoolRecordMapper;
    @Resource
    private ShowcaseRewardService showcaseRewardService;

    @Override
    public AppPriorityPoolCurrentRespVO getCurrentPriorityPool(Long authUserId) {
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        PriorityPoolRecordDO record = priorityPoolRecordMapper.selectCurrentByMerchantId(merchant.getId());
        AppPriorityPoolCurrentRespVO respVO = new AppPriorityPoolCurrentRespVO();
        respVO.setMerchantId(merchant.getId());
        if (record != null) {
            respVO.setStatus(record.getStatus());
            respVO.setReasonCode(record.getReasonCode());
            respVO.setReasonRemark(record.getReasonRemark());
            respVO.setEffectiveTime(record.getEffectiveTime());
            respVO.setExpireTime(record.getExpireTime());
        }
        return respVO;
    }

    @Override
    public Long createShowcaseReward(Long authUserId, @Valid AppShowcaseRewardCreateReqVO reqVO) {
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        return showcaseRewardService.createReward(ShowcaseRewardDO.builder()
                .merchantId(merchant.getId())
                .userId(merchant.getUserId())
                .title(reqVO.getTitle())
                .description(reqVO.getDescription())
                .evidenceFileIdsJson(reqVO.getEvidenceFileIdsJson())
                .build());
    }

    @Override
    public PageResult<AppShowcaseRewardRespVO> getShowcaseRewardPage(Long authUserId) {
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        List<AppShowcaseRewardRespVO> list = showcaseRewardService.getRewardListByMerchantId(merchant.getId()).stream()
                .map(this::convertReward)
                .collect(Collectors.toList());
        return new PageResult<>(list, (long) list.size());
    }

    @Override
    public AppShowcaseRewardRespVO getShowcaseReward(Long authUserId, Long id) {
        MerchantInfoDO merchant = getRequiredMerchant(authUserId);
        ShowcaseRewardDO rewardDO = showcaseRewardService.getReward(id);
        if (rewardDO == null || !merchant.getId().equals(rewardDO.getMerchantId())) {
            return null;
        }
        return convertReward(rewardDO);
    }

    private AppShowcaseRewardRespVO convertReward(ShowcaseRewardDO rewardDO) {
        AppShowcaseRewardRespVO respVO = new AppShowcaseRewardRespVO();
        respVO.setId(rewardDO.getId());
        respVO.setMerchantId(rewardDO.getMerchantId());
        respVO.setTitle(rewardDO.getTitle());
        respVO.setDescription(rewardDO.getDescription());
        respVO.setEvidenceFileIdsJson(rewardDO.getEvidenceFileIdsJson());
        respVO.setAuditStatus(rewardDO.getAuditStatus());
        respVO.setAuditRemark(rewardDO.getAuditRemark());
        respVO.setRejectReason(rewardDO.getRejectReason());
        respVO.setPriorityEnabled(rewardDO.getPriorityEnabled());
        respVO.setEffectiveStartTime(rewardDO.getEffectiveStartTime());
        respVO.setEffectiveEndTime(rewardDO.getEffectiveEndTime());
        respVO.setCreateTime(rewardDO.getCreateTime());
        return respVO;
    }

    private MerchantInfoDO getRequiredMerchant(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId())
                .last("LIMIT 1"));
        if (merchant == null) {
            throw exception(MERCHANT_INFO_NOT_EXISTS);
        }
        return merchant;
    }
}
