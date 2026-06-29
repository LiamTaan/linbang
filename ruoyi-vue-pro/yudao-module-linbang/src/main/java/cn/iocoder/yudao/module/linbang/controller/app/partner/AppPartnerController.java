package cn.iocoder.yudao.module.linbang.controller.app.partner;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerCoordinationCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerDisputePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerDisputeRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerEntryAuditPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerEntryAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerEntryAuditRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerInstructionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPriceReportCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPriceReportPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPriceReportRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPromoteStatRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerRegionRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerWorkbenchRespVO;
import cn.iocoder.yudao.module.linbang.service.app.partner.AppPartnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 区域合作商工作台")
@RestController
@RequestMapping("/partner")
@Validated
public class AppPartnerController {

    @Resource
    private AppPartnerService appPartnerService;

    @GetMapping("/workbench/get")
    @Operation(summary = "获取区域合作商工作台")
    public CommonResult<AppPartnerWorkbenchRespVO> getWorkbench() {
        return success(appPartnerService.getWorkbench(getLoginUserId()));
    }

    @GetMapping("/region/get")
    @Operation(summary = "获取合作商辖区详情")
    public CommonResult<AppPartnerRegionRespVO> getRegionDetail() {
        return success(appPartnerService.getRegionDetail(getLoginUserId()));
    }

    @GetMapping("/entry-audit/page")
    @Operation(summary = "获取合作商辖区内入驻初审分页")
    public CommonResult<PageResult<AppPartnerEntryAuditRespVO>> getEntryAuditPage(@Valid AppPartnerEntryAuditPageReqVO reqVO) {
        return success(appPartnerService.getEntryAuditPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/entry-audit/get")
    @Operation(summary = "获取合作商辖区内入驻初审详情")
    public CommonResult<AppPartnerEntryAuditRespVO> getEntryAudit(@RequestParam("id") Long id) {
        return success(appPartnerService.getEntryAudit(getLoginUserId(), id));
    }

    @PostMapping("/entry-audit/audit")
    @Operation(summary = "合作商执行入驻初审")
    public CommonResult<Boolean> auditEntry(@Valid @RequestBody AppPartnerEntryAuditReqVO reqVO) {
        appPartnerService.auditEntry(getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/dispute/page")
    @Operation(summary = "获取合作商辖区纠纷分页")
    public CommonResult<PageResult<AppPartnerDisputeRespVO>> getDisputePage(@Valid AppPartnerDisputePageReqVO reqVO) {
        return success(appPartnerService.getDisputePage(getLoginUserId(), reqVO));
    }

    @GetMapping("/dispute/{disputeType}/{disputeId}")
    @Operation(summary = "获取合作商辖区纠纷详情")
    public CommonResult<AppPartnerDisputeRespVO> getDispute(
            @Parameter(name = "disputeType", required = true, example = "COMPLAINT") @PathVariable("disputeType") String disputeType,
            @Parameter(name = "disputeId", required = true, example = "1") @PathVariable("disputeId") Long disputeId) {
        return success(appPartnerService.getDispute(getLoginUserId(), disputeType, disputeId));
    }

    @PostMapping("/dispute/coordination/create")
    @Operation(summary = "合作商发起纠纷协调")
    public CommonResult<Long> createCoordination(@Valid @RequestBody AppPartnerCoordinationCreateReqVO reqVO) {
        return success(appPartnerService.createCoordination(getLoginUserId(), reqVO));
    }

    @GetMapping("/price-report/page")
    @Operation(summary = "获取合作商价格申报分页")
    public CommonResult<PageResult<AppPartnerPriceReportRespVO>> getPriceReportPage(@Valid AppPartnerPriceReportPageReqVO reqVO) {
        return success(appPartnerService.getPriceReportPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/price-report/get")
    @Operation(summary = "获取合作商价格申报详情")
    public CommonResult<AppPartnerPriceReportRespVO> getPriceReport(@RequestParam("id") Long id) {
        return success(appPartnerService.getPriceReport(getLoginUserId(), id));
    }

    @PostMapping("/price-report/create")
    @Operation(summary = "创建合作商价格申报")
    public CommonResult<Long> createPriceReport(@Valid @RequestBody AppPartnerPriceReportCreateReqVO reqVO) {
        return success(appPartnerService.createPriceReport(getLoginUserId(), reqVO));
    }

    @PutMapping("/price-report/withdraw")
    @Operation(summary = "撤回合作商价格申报")
    public CommonResult<Boolean> withdrawPriceReport(@RequestParam("id") Long id) {
        appPartnerService.withdrawPriceReport(getLoginUserId(), id);
        return success(true);
    }

    @GetMapping("/promote-stat/get")
    @Operation(summary = "获取合作商辖区推广数据")
    public CommonResult<AppPartnerPromoteStatRespVO> getPromoteStat() {
        return success(appPartnerService.getPromoteStat(getLoginUserId()));
    }

    @GetMapping("/instruction/page")
    @Operation(summary = "获取合作商会议通知/上级指令分页")
    public CommonResult<PageResult<AppMessageRecordRespVO>> getInstructionPage(@Valid AppPartnerInstructionPageReqVO reqVO) {
        return success(appPartnerService.getInstructionPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/instruction/get")
    @Operation(summary = "获取合作商会议通知/上级指令详情")
    public CommonResult<AppMessageRecordDetailRespVO> getInstruction(@RequestParam("id") Long id) {
        return success(appPartnerService.getInstruction(getLoginUserId(), id));
    }
}
