package cn.iocoder.yudao.module.linbang.service.app.rewardorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipationRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.rewardorderparticipation.RewardOrderParticipationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.rewardorderparticipation.RewardOrderParticipationMapper;
import cn.iocoder.yudao.module.linbang.service.match.ShowcaseRewardService;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantPriorityFacadeService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_ACCESS_DENIED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_INFO_NOT_EXISTS;

@Service
@Validated
public class AppRewardOrderServiceImpl implements AppRewardOrderService {

    @Resource
    private AppMerchantPriorityFacadeService appMerchantPriorityFacadeService;
    @Resource
    private MemberUserService memberUserService;
    @Resource
    private ShowcaseRewardService showcaseRewardService;
    @Resource
    private RewardOrderParticipationMapper rewardOrderParticipationMapper;

    @Override
    public Long createRewardOrder(Long authUserId, @Valid AppRewardOrderCreateReqVO reqVO) {
        AppShowcaseRewardCreateReqVO createReqVO = new AppShowcaseRewardCreateReqVO();
        createReqVO.setTitle(reqVO.getTitle());
        createReqVO.setDescription(reqVO.getDescription());
        createReqVO.setEvidenceFileIdsJson(reqVO.getEvidenceFileIdsJson());
        return appMerchantPriorityFacadeService.createShowcaseReward(authUserId, createReqVO);
    }

    @Override
    public PageResult<AppRewardOrderRespVO> getRewardOrderPage(Long authUserId, AppRewardOrderPageReqVO reqVO) {
        List<AppRewardOrderRespVO> list = appMerchantPriorityFacadeService.getShowcaseRewardPage(authUserId).getList().stream()
                .map(this::convert)
                .filter(item -> reqVO.getAuditStatus() == null || Objects.equals(item.getAuditStatus(), reqVO.getAuditStatus()))
                .collect(Collectors.toList());
        return manualPage(list, reqVO.getPageNo(), reqVO.getPageSize());
    }

    @Override
    public AppRewardOrderRespVO getRewardOrder(Long authUserId, Long id) {
        AppShowcaseRewardRespVO reward = appMerchantPriorityFacadeService.getShowcaseReward(authUserId, id);
        return reward == null ? null : convert(reward);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long participateRewardOrder(Long authUserId, @Valid AppRewardOrderParticipateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        ShowcaseRewardDO reward = showcaseRewardService.getReward(reqVO.getRewardOrderId());
        if (reward == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (Objects.equals(reward.getUserId(), loginUser.getId())) {
            throw exception(ORDER_ACCESS_DENIED);
        }
        if (rewardOrderParticipationMapper.existsByRewardOrderIdAndParticipantUserId(reward.getId(), loginUser.getId())) {
            List<RewardOrderParticipationDO> exists = rewardOrderParticipationMapper.selectListByRewardOrderId(reward.getId()).stream()
                    .filter(item -> Objects.equals(item.getParticipantUserId(), loginUser.getId()))
                    .collect(Collectors.toList());
            return exists.isEmpty() ? null : exists.get(0).getId();
        }
        RewardOrderParticipationDO participation = RewardOrderParticipationDO.builder()
                .rewardOrderId(reward.getId())
                .rewardUserId(reward.getUserId())
                .participantUserId(loginUser.getId())
                .participantMobile(loginUser.getMobile())
                .participantNickname(loginUser.getNickname())
                .status("PENDING")
                .participateRemark(reqVO.getParticipateRemark())
                .build();
        rewardOrderParticipationMapper.insert(participation);
        return participation.getId();
    }

    @Override
    public PageResult<AppRewardOrderParticipationRespVO> getParticipatedRewardOrderPage(Long authUserId,
                                                                                         AppRewardOrderParticipationPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        PageResult<RewardOrderParticipationDO> pageResult = rewardOrderParticipationMapper.selectPageByParticipant(loginUser.getId(), reqVO);
        List<AppRewardOrderParticipationRespVO> list = new ArrayList<>();
        for (RewardOrderParticipationDO item : pageResult.getList()) {
            ShowcaseRewardDO reward = showcaseRewardService.getReward(item.getRewardOrderId());
            AppRewardOrderParticipationRespVO respVO = new AppRewardOrderParticipationRespVO();
            respVO.setId(item.getId());
            respVO.setRewardOrderId(item.getRewardOrderId());
            respVO.setRewardTitle(reward == null ? null : reward.getTitle());
            respVO.setRewardUserId(item.getRewardUserId());
            respVO.setParticipantUserId(item.getParticipantUserId());
            respVO.setParticipantNickname(item.getParticipantNickname());
            respVO.setParticipantMobile(item.getParticipantMobile());
            respVO.setStatus(item.getStatus());
            respVO.setParticipateRemark(item.getParticipateRemark());
            respVO.setAuditRemark(item.getAuditRemark());
            respVO.setCreateTime(item.getCreateTime());
            list.add(respVO);
        }
        return new PageResult<>(list, pageResult.getTotal());
    }

    private AppRewardOrderRespVO convert(AppShowcaseRewardRespVO reward) {
        AppRewardOrderRespVO respVO = new AppRewardOrderRespVO();
        respVO.setId(reward.getId());
        respVO.setMerchantId(reward.getMerchantId());
        respVO.setTitle(reward.getTitle());
        respVO.setDescription(reward.getDescription());
        respVO.setEvidenceFileIdsJson(reward.getEvidenceFileIdsJson());
        respVO.setAuditStatus(reward.getAuditStatus());
        respVO.setAuditRemark(reward.getAuditRemark());
        respVO.setRejectReason(reward.getRejectReason());
        respVO.setPriorityEnabled(reward.getPriorityEnabled());
        respVO.setEffectiveStartTime(reward.getEffectiveStartTime());
        respVO.setEffectiveEndTime(reward.getEffectiveEndTime());
        respVO.setCreateTime(reward.getCreateTime());
        return respVO;
    }

    private PageResult<AppRewardOrderRespVO> manualPage(List<AppRewardOrderRespVO> list, Integer pageNo, Integer pageSize) {
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
