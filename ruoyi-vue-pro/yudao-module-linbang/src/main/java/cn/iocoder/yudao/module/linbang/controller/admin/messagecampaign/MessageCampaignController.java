package cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignCancelReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignRejectReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignSaveReqVO;
import cn.iocoder.yudao.module.linbang.service.messagecampaign.MessageCampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 消息投放活动")
@RestController
@RequestMapping("/message/campaign")
@Validated
public class MessageCampaignController {

    @Resource
    private MessageCampaignService messageCampaignService;

    @GetMapping("/page")
    @Operation(summary = "获得消息投放活动分页")
    @PreAuthorize("@ss.hasPermission('linbang:message:campaign:query')")
    public CommonResult<PageResult<MessageCampaignRespVO>> getPage(@Valid MessageCampaignPageReqVO reqVO) {
        return success(messageCampaignService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得消息投放活动详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:message:campaign:query')")
    public CommonResult<MessageCampaignRespVO> get(@RequestParam("id") Long id) {
        return success(messageCampaignService.get(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建消息投放活动")
    @PreAuthorize("@ss.hasPermission('linbang:message:campaign:create')")
    public CommonResult<Long> create(@Valid @RequestBody MessageCampaignSaveReqVO reqVO) {
        return success(messageCampaignService.create(reqVO));
    }

    @PostMapping("/approve")
    @Operation(summary = "审核通过消息投放活动")
    @PreAuthorize("@ss.hasPermission('linbang:message:campaign:audit')")
    public CommonResult<Boolean> approve(@Valid @RequestBody MessageCampaignAuditReqVO reqVO) {
        messageCampaignService.approve(reqVO.getId(), reqVO.getAuditRemark());
        return success(true);
    }

    @PostMapping("/reject")
    @Operation(summary = "驳回消息投放活动")
    @PreAuthorize("@ss.hasPermission('linbang:message:campaign:audit')")
    public CommonResult<Boolean> reject(@Valid @RequestBody MessageCampaignRejectReqVO reqVO) {
        messageCampaignService.reject(reqVO.getId(), reqVO.getRejectReason());
        return success(true);
    }

    @PostMapping("/execute-now")
    @Operation(summary = "立即执行消息投放活动")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:message:campaign:execute')")
    public CommonResult<Boolean> executeNow(@RequestParam("id") Long id) {
        messageCampaignService.executeNow(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消消息投放活动")
    @PreAuthorize("@ss.hasPermission('linbang:message:campaign:cancel')")
    public CommonResult<Boolean> cancel(@Valid @RequestBody MessageCampaignCancelReqVO reqVO) {
        messageCampaignService.cancel(reqVO.getId(), reqVO.getReason());
        return success(true);
    }
}
