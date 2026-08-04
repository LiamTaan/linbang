package cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard;

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

import cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo.*;
import cn.iocoder.yudao.module.linbang.service.walletbankcard.WalletBankCardService;

@Tag(name = "管理后台 - 用户银行卡")
@RestController
@RequestMapping("/wallet/bank-card")
@Validated
public class WalletBankCardController {

    @Resource
    private WalletBankCardService walletBankCardService;

    @GetMapping("/get")
    @Operation(summary = "获得用户银行卡")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:bank-card:query')")
    public CommonResult<WalletBankCardDetailRespVO> getWalletBankCard(@RequestParam("id") Long id) {
        return success(walletBankCardService.getWalletBankCardDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户银行卡分页")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:bank-card:query')")
    public CommonResult<PageResult<WalletBankCardRespVO>> getWalletBankCardPage(@Valid WalletBankCardPageReqVO pageReqVO) {
        return success(walletBankCardService.getWalletBankCardPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户银行卡 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:bank-card:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWalletBankCardExcel(@Valid WalletBankCardPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(MAX_EXPORT_ROWS);
        List<WalletBankCardRespVO> list = walletBankCardService.getWalletBankCardPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户银行卡.xls", "数据", WalletBankCardRespVO.class, list);
    }

}
