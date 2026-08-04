package cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import static cn.iocoder.yudao.module.linbang.constants.LinbangExportConstants.MAX_EXPORT_ROWS;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo.*;
import cn.iocoder.yudao.module.linbang.service.walletwithdraw.WalletWithdrawService;

@Tag(name = "管理后台 - 提现申请")
@RestController
@RequestMapping("/wallet/withdraw")
@Validated
public class WalletWithdrawController {

    @Resource
    private WalletWithdrawService walletWithdrawService;

    @GetMapping("/get")
    @Operation(summary = "获得提现申请")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:query')")
    public CommonResult<WalletWithdrawDetailRespVO> getWalletWithdraw(@RequestParam("id") Long id) {
        return success(walletWithdrawService.getWalletWithdrawDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得提现申请分页")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:query')")
    public CommonResult<PageResult<WalletWithdrawRespVO>> getWalletWithdrawPage(@Valid WalletWithdrawPageReqVO pageReqVO) {
        return success(walletWithdrawService.getWalletWithdrawPage(pageReqVO));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核提现申请")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:audit')")
    public CommonResult<Boolean> auditWalletWithdraw(@Valid @RequestBody WithdrawAuditReqVO reqVO) {
        walletWithdrawService.auditWalletWithdraw(reqVO);
        return success(true);
    }

    @PostMapping("/retry-transfer")
    @Operation(summary = "重试提现打款")
    @Parameter(name = "id", description = "提现申请 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:audit')")
    public CommonResult<Long> retryWalletWithdrawTransfer(@RequestParam("id") Long id) {
        return success(walletWithdrawService.retryWalletWithdrawTransfer(id));
    }
    @GetMapping("/export-excel")
    @Operation(summary = "导出提现申请 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWalletWithdrawExcel(@Valid WalletWithdrawPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(MAX_EXPORT_ROWS);
        List<WalletWithdrawRespVO> list = walletWithdrawService.getWalletWithdrawPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "提现申请.xls", "数据", WalletWithdrawRespVO.class, list);
    }

}
