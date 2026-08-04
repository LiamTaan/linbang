package cn.iocoder.yudao.module.linbang.controller.admin.walletflow;

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

import cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo.*;
import cn.iocoder.yudao.module.linbang.service.walletflow.WalletFlowService;

@Tag(name = "管理后台 - 钱包流水")
@RestController
@RequestMapping("/wallet/flow")
@Validated
public class WalletFlowController {

    @Resource
    private WalletFlowService walletFlowService;

    @GetMapping("/get")
    @Operation(summary = "获得钱包流水")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:flow:query')")
    public CommonResult<WalletFlowDetailRespVO> getWalletFlow(@RequestParam("id") Long id) {
        return success(walletFlowService.getWalletFlowDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得钱包流水分页")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:flow:query')")
    public CommonResult<PageResult<WalletFlowRespVO>> getWalletFlowPage(@Valid WalletFlowPageReqVO pageReqVO) {
        return success(walletFlowService.getWalletFlowPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出钱包流水 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:flow:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWalletFlowExcel(@Valid WalletFlowPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(MAX_EXPORT_ROWS);
        List<WalletFlowRespVO> list = walletFlowService.getWalletFlowPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "钱包流水.xls", "数据", WalletFlowRespVO.class, list);
    }

}
