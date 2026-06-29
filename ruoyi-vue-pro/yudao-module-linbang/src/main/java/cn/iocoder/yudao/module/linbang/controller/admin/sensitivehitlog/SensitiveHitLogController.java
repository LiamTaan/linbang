package cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo.SensitiveHitLogRespVO;
import cn.iocoder.yudao.module.linbang.service.sensitivehitlog.SensitiveHitLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 敏感词命中日志")
@RestController
@RequestMapping("/risk/sensitive-hit-log")
@Validated
public class SensitiveHitLogController {

    @Resource
    private SensitiveHitLogService sensitiveHitLogService;

    @GetMapping("/page")
    @Operation(summary = "获取敏感词命中日志分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:sensitive-hit-log:query')")
    public CommonResult<PageResult<SensitiveHitLogRespVO>> getPage(@Valid SensitiveHitLogPageReqVO reqVO) {
        return success(sensitiveHitLogService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取敏感词命中日志详情")
    @PreAuthorize("@ss.hasPermission('linbang:risk:sensitive-hit-log:query')")
    public CommonResult<SensitiveHitLogDetailRespVO> get(@RequestParam("id") Long id) {
        return success(sensitiveHitLogService.getDetail(id));
    }
}
