package cn.iocoder.yudao.module.linbang.controller.admin.messagescene;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageScenePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageSceneRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo.MessageSceneSaveReqVO;
import cn.iocoder.yudao.module.linbang.service.messagescene.MessageSceneService;
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

@Tag(name = "管理后台 - 消息场景")
@RestController
@RequestMapping("/message/scene")
@Validated
public class MessageSceneController {

    @Resource
    private MessageSceneService messageSceneService;

    @GetMapping("/page")
    @Operation(summary = "获得消息场景分页")
    @PreAuthorize("@ss.hasPermission('linbang:message:scene:query')")
    public CommonResult<PageResult<MessageSceneRespVO>> getPage(@Valid MessageScenePageReqVO reqVO) {
        return success(messageSceneService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得消息场景详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:message:scene:query')")
    public CommonResult<MessageSceneRespVO> get(@RequestParam("id") Long id) {
        return success(messageSceneService.get(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建消息场景")
    @PreAuthorize("@ss.hasPermission('linbang:message:scene:create')")
    public CommonResult<Long> create(@Valid @RequestBody MessageSceneSaveReqVO reqVO) {
        return success(messageSceneService.create(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新消息场景")
    @PreAuthorize("@ss.hasPermission('linbang:message:scene:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody MessageSceneSaveReqVO reqVO) {
        messageSceneService.update(reqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新消息场景状态")
    @PreAuthorize("@ss.hasPermission('linbang:message:scene:update')")
    public CommonResult<Boolean> updateStatus(@RequestParam("id") Long id, @RequestParam("status") String status) {
        messageSceneService.updateStatus(id, status);
        return success(true);
    }
}
