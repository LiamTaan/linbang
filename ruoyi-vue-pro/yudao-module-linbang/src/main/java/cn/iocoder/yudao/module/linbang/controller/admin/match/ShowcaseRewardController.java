package cn.iocoder.yudao.module.linbang.controller.admin.match;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.ShowcaseRewardAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.ShowcaseRewardParticipationRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.ShowcaseRewardPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.ShowcaseRewardRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.rewardorderparticipation.RewardOrderParticipationMapper;
import cn.iocoder.yudao.module.linbang.service.match.ShowcaseRewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 邻里晒单悬赏")
@RestController
@RequestMapping("/linbang/showcase/reward")
@Validated
public class ShowcaseRewardController {

    @Resource
    private ShowcaseRewardService showcaseRewardService;
    @Resource
    private RewardOrderParticipationMapper rewardOrderParticipationMapper;

    @GetMapping("/page")
    @Operation(summary = "分页获取晒单悬赏申请")
    @PreAuthorize("@ss.hasPermission('linbang:showcase:reward:query')")
    public CommonResult<PageResult<ShowcaseRewardRespVO>> page(@Valid ShowcaseRewardPageReqVO reqVO) {
        PageResult<ShowcaseRewardDO> pageResult = showcaseRewardService.getRewardPage(reqVO, reqVO.getMerchantId(), reqVO.getAuditStatus());
        return success(new PageResult<>(pageResult.getList().stream().map(this::convert).collect(Collectors.toList()), pageResult.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取晒单悬赏详情")
    @PreAuthorize("@ss.hasPermission('linbang:showcase:reward:query')")
    public CommonResult<ShowcaseRewardRespVO> get(@RequestParam("id") Long id) {
        ShowcaseRewardDO reward = showcaseRewardService.getReward(id);
        return success(reward == null ? null : convert(reward));
    }

    @GetMapping("/participation/list")
    @Operation(summary = "获取晒单悬赏参与记录")
    @PreAuthorize("@ss.hasPermission('linbang:showcase:reward:query')")
    public CommonResult<List<ShowcaseRewardParticipationRespVO>> getParticipationList(@RequestParam("rewardOrderId") Long rewardOrderId) {
        return success(rewardOrderParticipationMapper.selectListByRewardOrderId(rewardOrderId).stream().map(item -> {
            ShowcaseRewardParticipationRespVO respVO = new ShowcaseRewardParticipationRespVO();
            respVO.setId(item.getId());
            respVO.setRewardOrderId(item.getRewardOrderId());
            respVO.setRewardUserId(item.getRewardUserId());
            respVO.setParticipantUserId(item.getParticipantUserId());
            respVO.setParticipantNickname(item.getParticipantNickname());
            respVO.setParticipantMobile(item.getParticipantMobile());
            respVO.setStatus(item.getStatus());
            respVO.setParticipateRemark(item.getParticipateRemark());
            respVO.setAuditRemark(item.getAuditRemark());
            respVO.setCreateTime(item.getCreateTime());
            return respVO;
        }).collect(Collectors.toList()));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核晒单悬赏申请")
    @PreAuthorize("@ss.hasPermission('linbang:showcase:reward:audit')")
    public CommonResult<Boolean> audit(@Valid @RequestBody ShowcaseRewardAuditReqVO reqVO) {
        showcaseRewardService.auditReward(reqVO.getId(), reqVO.getAuditStatus(), reqVO.getAuditRemark(),
                reqVO.getRejectReason(), SecurityFrameworkUtils.getLoginUserId());
        return success(Boolean.TRUE);
    }

    private ShowcaseRewardRespVO convert(ShowcaseRewardDO reward) {
        ShowcaseRewardRespVO respVO = new ShowcaseRewardRespVO();
        respVO.setId(reward.getId());
        respVO.setMerchantId(reward.getMerchantId());
        respVO.setUserId(reward.getUserId());
        respVO.setTitle(reward.getTitle());
        respVO.setDescription(reward.getDescription());
        respVO.setEvidenceFileIdsJson(reward.getEvidenceFileIdsJson());
        respVO.setAuditStatus(reward.getAuditStatus());
        respVO.setAuditRemark(reward.getAuditRemark());
        respVO.setRejectReason(reward.getRejectReason());
        respVO.setAuditBy(reward.getAuditBy());
        respVO.setAuditTime(reward.getAuditTime());
        respVO.setPriorityEnabled(reward.getPriorityEnabled());
        respVO.setEffectiveStartTime(reward.getEffectiveStartTime());
        respVO.setEffectiveEndTime(reward.getEffectiveEndTime());
        respVO.setCreateTime(reward.getCreateTime());
        return respVO;
    }
}
