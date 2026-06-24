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

import cn.iocoder.yudao.framework.common.pojo.PageParam;
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

    @PostMapping("/create")
    @Operation(summary = "创建用户银行卡")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:bank-card:create')")
    public CommonResult<Long> createWalletBankCard(@Valid @RequestBody WalletBankCardSaveReqVO createReqVO) {
        return success(walletBankCardService.createWalletBankCard(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户银行卡")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:bank-card:update')")
    public CommonResult<Boolean> updateWalletBankCard(@Valid @RequestBody WalletBankCardSaveReqVO updateReqVO) {
        walletBankCardService.updateWalletBankCard(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户银行卡")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:wallet:bank-card:delete')")
    public CommonResult<Boolean> deleteWalletBankCard(@RequestParam("id") Long id) {
        walletBankCardService.deleteWalletBankCard(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除用户银行卡")
                @PreAuthorize("@ss.hasPermission('linbang:wallet:bank-card:delete')")
    public CommonResult<Boolean> deleteWalletBankCardList(@RequestParam("ids") List<Long> ids) {
        walletBankCardService.deleteWalletBankCardListByIds(ids);
        return success(true);
    }

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
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<WalletBankCardRespVO> list = walletBankCardService.getWalletBankCardPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户银行卡.xls", "数据", WalletBankCardRespVO.class, list);
    }

}
