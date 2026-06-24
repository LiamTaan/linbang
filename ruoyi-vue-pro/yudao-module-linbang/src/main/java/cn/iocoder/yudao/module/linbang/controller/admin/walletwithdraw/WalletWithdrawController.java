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

import cn.iocoder.yudao.framework.common.pojo.PageParam;
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

    @PostMapping("/create")
    @Operation(summary = "创建提现申请")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:create')")
    public CommonResult<Long> createWalletWithdraw(@Valid @RequestBody WalletWithdrawSaveReqVO createReqVO) {
        return success(walletWithdrawService.createWalletWithdraw(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新提现申请")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:update')")
    public CommonResult<Boolean> updateWalletWithdraw(@Valid @RequestBody WalletWithdrawSaveReqVO updateReqVO) {
        walletWithdrawService.updateWalletWithdraw(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除提现申请")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:delete')")
    public CommonResult<Boolean> deleteWalletWithdraw(@RequestParam("id") Long id) {
        walletWithdrawService.deleteWalletWithdraw(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除提现申请")
                @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:delete')")
    public CommonResult<Boolean> deleteWalletWithdrawList(@RequestParam("ids") List<Long> ids) {
        walletWithdrawService.deleteWalletWithdrawListByIds(ids);
        return success(true);
    }

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

    @GetMapping("/export-excel")
    @Operation(summary = "导出提现申请 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:withdraw:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportWalletWithdrawExcel(@Valid WalletWithdrawPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<WalletWithdrawRespVO> list = walletWithdrawService.getWalletWithdrawPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "提现申请.xls", "数据", WalletWithdrawRespVO.class, list);
    }

}
