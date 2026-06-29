package cn.iocoder.yudao.module.linbang.controller.admin.riskevent;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventDisposeReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventRespVO;
import cn.iocoder.yudao.module.linbang.service.riskevent.RiskEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 风险事件")
@RestController
@RequestMapping("/risk/event")
@Validated
public class RiskEventController {

    @Resource
    private RiskEventService riskEventService;

    @GetMapping("/get")
    @Operation(summary = "获得风险事件详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:risk:event:query')")
    public CommonResult<RiskEventDetailRespVO> getRiskEvent(@RequestParam("id") Long id) {
        return success(riskEventService.getRiskEventDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得风险事件分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:event:query')")
    public CommonResult<PageResult<RiskEventRespVO>> getRiskEventPage(@Valid RiskEventPageReqVO reqVO) {
        return success(riskEventService.getRiskEventPage(reqVO));
    }

    @PostMapping("/dispose")
    @Operation(summary = "处置风险事件")
    @PreAuthorize("@ss.hasPermission('linbang:risk:event:update')")
    public CommonResult<Boolean> disposeRiskEvent(@Valid @RequestBody RiskEventDisposeReqVO reqVO) {
        riskEventService.disposeRiskEvent(getLoginUserId(), reqVO);
        return success(true);
    }
}
