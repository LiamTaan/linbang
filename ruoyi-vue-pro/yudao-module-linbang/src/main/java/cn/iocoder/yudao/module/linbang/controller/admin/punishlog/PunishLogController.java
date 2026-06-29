package cn.iocoder.yudao.module.linbang.controller.admin.punishlog;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo.PunishLogRespVO;
import cn.iocoder.yudao.module.linbang.service.punishlog.PunishLogService;
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

@Tag(name = "管理后台 - 处罚执行日志")
@RestController
@RequestMapping("/risk/punish-log")
@Validated
public class PunishLogController {

    @Resource
    private PunishLogService punishLogService;

    @GetMapping("/page")
    @Operation(summary = "获取处罚执行日志分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:punish-log:query')")
    public CommonResult<PageResult<PunishLogRespVO>> getPage(@Valid PunishLogPageReqVO reqVO) {
        return success(punishLogService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取处罚执行日志详情")
    @PreAuthorize("@ss.hasPermission('linbang:risk:punish-log:query')")
    public CommonResult<PunishLogDetailRespVO> get(@RequestParam("id") Long id) {
        return success(punishLogService.getDetail(id));
    }
}
