package cn.iocoder.yudao.module.linbang.controller.app.message;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageFeedbackReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSendReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSettingRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSettingUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.message.AppMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 消息中心")
@RestController
@RequestMapping("/message/record")
@Validated
public class AppMessageController {

    @Resource
    private AppMessageService appMessageService;

    @GetMapping("/page")
    @Operation(summary = "获取消息记录分页")
    public CommonResult<PageResult<AppMessageRecordRespVO>> getMessageRecordPage(@Valid AppMessageRecordPageReqVO reqVO) {
        return success(BeanUtils.toBean(appMessageService.getMessageRecordPage(getLoginUserId(), reqVO),
                AppMessageRecordRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获取消息记录详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppMessageRecordDetailRespVO> getMessageRecord(@RequestParam("id") Long id) {
        return success(appMessageService.getMessageRecord(getLoginUserId(), id));
    }

    @PostMapping("/send")
    @Operation(summary = "发送站内消息")
    public CommonResult<Long> sendMessage(@Valid @RequestBody AppMessageSendReqVO reqVO) {
        return success(appMessageService.sendMessage(getLoginUserId(), reqVO));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获得未读消息数量")
    public CommonResult<Long> getUnreadCount() {
        return success(appMessageService.getUnreadCount(getLoginUserId()));
    }

    @PutMapping("/mark-read")
    @Operation(summary = "标记单条消息已读")
    public CommonResult<Boolean> markRead(@RequestParam("id") Long id) {
        appMessageService.markRead(getLoginUserId(), id);
        return success(true);
    }

    @PutMapping("/mark-all-read")
    @Operation(summary = "按分类全部标记已读")
    public CommonResult<Boolean> markAllRead(@RequestParam(value = "messageCategory", required = false) String messageCategory) {
        appMessageService.markAllRead(getLoginUserId(), messageCategory);
        return success(true);
    }

    @PostMapping("/feedback/expose")
    @Operation(summary = "提交消息曝光回执")
    public CommonResult<Boolean> submitExposeFeedback(@Valid @RequestBody AppMessageFeedbackReqVO reqVO) {
        appMessageService.submitExposeFeedback(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/feedback/click")
    @Operation(summary = "提交消息点击回执")
    public CommonResult<Boolean> submitClickFeedback(@Valid @RequestBody AppMessageFeedbackReqVO reqVO) {
        appMessageService.submitClickFeedback(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/feedback/voice-played")
    @Operation(summary = "提交语音播放回执")
    public CommonResult<Boolean> submitVoicePlayedFeedback(@Valid @RequestBody AppMessageFeedbackReqVO reqVO) {
        appMessageService.submitVoicePlayedFeedback(getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/setting/get")
    @Operation(summary = "获得消息偏好设置")
    public CommonResult<AppMessageSettingRespVO> getMessageSetting() {
        return success(appMessageService.getMessageSetting(getLoginUserId()));
    }

    @PutMapping("/setting/update")
    @Operation(summary = "更新消息偏好设置")
    public CommonResult<Boolean> updateMessageSetting(@Valid @RequestBody AppMessageSettingUpdateReqVO reqVO) {
        appMessageService.updateMessageSetting(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/campaign/create")
    @Operation(summary = "创建用户定向推送申请")
    public CommonResult<Long> createDirectedCampaign(@Valid @RequestBody AppMessageCampaignCreateReqVO reqVO) {
        return success(appMessageService.createDirectedCampaign(getLoginUserId(), reqVO));
    }

    @GetMapping("/campaign/page")
    @Operation(summary = "获得我的定向推送申请分页")
    public CommonResult<PageResult<MessageCampaignRespVO>> getDirectedCampaignPage(@Valid AppMessageCampaignPageReqVO reqVO) {
        return success(appMessageService.getDirectedCampaignPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/campaign/get")
    @Operation(summary = "获得我的定向推送申请详情")
    public CommonResult<MessageCampaignRespVO> getDirectedCampaign(@RequestParam("id") Long id) {
        return success(appMessageService.getDirectedCampaign(getLoginUserId(), id));
    }

    @PutMapping("/campaign/withdraw")
    @Operation(summary = "撤回我的定向推送申请")
    public CommonResult<Boolean> withdrawDirectedCampaign(@RequestParam("id") Long id) {
        appMessageService.withdrawDirectedCampaign(getLoginUserId(), id);
        return success(true);
    }
}
