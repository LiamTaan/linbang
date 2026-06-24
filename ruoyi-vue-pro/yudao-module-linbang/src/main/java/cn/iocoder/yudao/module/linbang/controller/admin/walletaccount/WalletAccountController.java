package cn.iocoder.yudao.module.linbang.controller.admin.walletaccount;

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

import cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo.*;
import cn.iocoder.yudao.module.linbang.service.walletaccount.WalletAccountService;

@Tag(name = "管理后台 - 钱包账户")
@RestController
@RequestMapping("/wallet/account")
@Validated
public class WalletAccountController {

    @Resource
    private WalletAccountService walletAccountService;

    @PostMapping("/create")
    @Operation(summary = "创建钱包账户")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:account:create')")
    public CommonResult<Long> createWalletAccount(@Valid @RequestBody WalletAccountSaveReqVO createReqVO) {
        return success(walletAccountService.createWalletAccount(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新钱包账户")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:account:update')")
    public CommonResult<Boolean> updateWalletAccount(@Valid @RequestBody WalletAccountSaveReqVO updateReqVO) {
        walletAccountService.updateWalletAccount(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除钱包账户")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:wallet:account:delete')")
    public CommonResult<Boolean> deleteWalletAccount(@RequestParam("id") Long id) {
        walletAccountService.deleteWalletAccount(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除钱包账户")
                @PreAuthorize("@ss.hasPermission('linbang:wallet:account:delete')")
    public CommonResult<Boolean> deleteWalletAccountList(@RequestParam("ids") List<Long> ids) {
        walletAccountService.deleteWalletAccountListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得钱包账户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:account:query')")
    public CommonResult<WalletAccountDetailRespVO> getWalletAccount(@RequestParam("id") Long id) {
        return success(walletAccountService.getWalletAccountDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得钱包账户分页")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:account:query')")
    public CommonResult<PageResult<WalletAccountRespVO>> getWalletAccountPage(@Valid WalletAccountPageReqVO pageReqVO) {
        return success(walletAccountService.getWalletAccountPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出钱包账户 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:account:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWalletAccountExcel(@Valid WalletAccountPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<WalletAccountRespVO> list = walletAccountService.getWalletAccountPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "钱包账户.xls", "数据", WalletAccountRespVO.class, list);
    }

}
