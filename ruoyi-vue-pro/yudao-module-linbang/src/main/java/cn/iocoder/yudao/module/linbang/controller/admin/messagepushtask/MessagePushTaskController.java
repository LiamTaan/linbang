package cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskRespVO;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushTaskService;
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

@Tag(name = "管理后台 - 消息推送任务")
@RestController
@RequestMapping("/message/push-task")
@Validated
public class MessagePushTaskController {

    @Resource
    private MessagePushTaskService messagePushTaskService;

    @GetMapping("/page")
    @Operation(summary = "获得消息推送任务分页")
    @PreAuthorize("@ss.hasPermission('linbang:message:push-task:query')")
    public CommonResult<PageResult<MessagePushTaskRespVO>> getPushTaskPage(@Valid MessagePushTaskPageReqVO reqVO) {
        return success(messagePushTaskService.getPushTaskPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得消息推送任务详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:message:push-task:query')")
    public CommonResult<MessagePushTaskDetailRespVO> getPushTask(@RequestParam("id") Long id) {
        return success(messagePushTaskService.getPushTaskDetail(id));
    }
}
