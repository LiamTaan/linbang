package cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo.MessageFeedbackStatPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo.MessageFeedbackStatRespVO;
import cn.iocoder.yudao.module.linbang.service.messagefeedback.MessageFeedbackStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 消息反馈统计")
@RestController
@RequestMapping("/message/feedback")
@Validated
public class MessageFeedbackController {

    @Resource
    private MessageFeedbackStatService messageFeedbackStatService;

    @GetMapping("/page")
    @Operation(summary = "获得消息反馈统计分页")
    @PreAuthorize("@ss.hasPermission('linbang:message:feedback:query')")
    public CommonResult<PageResult<MessageFeedbackStatRespVO>> getPage(@Valid MessageFeedbackStatPageReqVO reqVO) {
        return success(messageFeedbackStatService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得消息反馈统计详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:message:feedback:query')")
    public CommonResult<MessageFeedbackStatRespVO> get(@RequestParam("id") Long id) {
        return success(messageFeedbackStatService.get(id));
    }
}
