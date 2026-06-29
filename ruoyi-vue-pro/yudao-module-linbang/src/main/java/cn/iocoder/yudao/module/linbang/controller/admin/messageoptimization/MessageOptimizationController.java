package cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationSaveReqVO;
import cn.iocoder.yudao.module.linbang.service.messageoptimization.MessageOptimizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 消息优化工作台")
@RestController
@RequestMapping("/message/optimization")
@Validated
public class MessageOptimizationController {

    @Resource
    private MessageOptimizationService messageOptimizationService;

    @GetMapping("/page")
    @Operation(summary = "获得消息优化工作台分页")
    @PreAuthorize("@ss.hasPermission('linbang:message:optimization:query')")
    public CommonResult<PageResult<MessageOptimizationRespVO>> getPage(@Valid MessageOptimizationPageReqVO reqVO) {
        return success(messageOptimizationService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得消息优化记录详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:message:optimization:query')")
    public CommonResult<MessageOptimizationRespVO> get(@RequestParam("id") Long id) {
        return success(messageOptimizationService.get(id));
    }

    @PutMapping("/save")
    @Operation(summary = "保存消息优化备注")
    @PreAuthorize("@ss.hasPermission('linbang:message:optimization:update')")
    public CommonResult<Boolean> save(@Valid @RequestBody MessageOptimizationSaveReqVO reqVO) {
        messageOptimizationService.save(reqVO);
        return success(true);
    }
}
