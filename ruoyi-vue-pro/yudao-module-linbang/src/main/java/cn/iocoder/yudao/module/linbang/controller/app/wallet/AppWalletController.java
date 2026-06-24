package cn.iocoder.yudao.module.linbang.controller.app.wallet;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardDefaultReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletFlowPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletFlowRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawRespVO;
import cn.iocoder.yudao.module.linbang.service.app.wallet.AppWalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 邻里钱包")
@RestController
@RequestMapping("/wallet")
@Validated
public class AppWalletController {

    @Resource
    private AppWalletService appWalletService;

    @GetMapping("/account/get")
    @Operation(summary = "获取钱包详情")
    public CommonResult<AppWalletAccountRespVO> getWalletAccount() {
        return success(appWalletService.getWalletAccount(getLoginUserId()));
    }

    @GetMapping("/bank-card/page")
    @Operation(summary = "获取银行卡分页")
    public CommonResult<PageResult<AppBankCardRespVO>> getBankCardPage(@Valid AppBankCardPageReqVO reqVO) {
        return success(appWalletService.getBankCardPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/bank-card/get")
    @Operation(summary = "获取银行卡详情")
    public CommonResult<AppBankCardRespVO> getBankCard(@RequestParam("id") Long id) {
        return success(appWalletService.getBankCard(getLoginUserId(), id));
    }

    @PostMapping("/withdraw/create")
    @Operation(summary = "创建提现申请")
    public CommonResult<Long> createWithdraw(@Valid @RequestBody AppWalletWithdrawCreateReqVO reqVO) {
        return success(appWalletService.createWithdraw(getLoginUserId(), reqVO));
    }

    @GetMapping("/withdraw/page")
    @Operation(summary = "获取提现记录分页")
    public CommonResult<PageResult<AppWalletWithdrawRespVO>> getWithdrawPage(@Valid AppWalletWithdrawPageReqVO reqVO) {
        return success(appWalletService.getWithdrawPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/withdraw/get")
    @Operation(summary = "获取提现记录详情")
    public CommonResult<AppWalletWithdrawRespVO> getWithdraw(@RequestParam("id") Long id) {
        return success(appWalletService.getWithdraw(getLoginUserId(), id));
    }

    @PostMapping("/bank-card/create")
    @Operation(summary = "新增银行卡")
    public CommonResult<Long> createBankCard(@Valid @RequestBody AppBankCardCreateReqVO reqVO) {
        return success(appWalletService.createBankCard(getLoginUserId(), reqVO));
    }

    @PutMapping("/bank-card/update")
    @Operation(summary = "更新银行卡")
    public CommonResult<Boolean> updateBankCard(@Valid @RequestBody AppBankCardUpdateReqVO reqVO) {
        appWalletService.updateBankCard(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @PutMapping("/bank-card/default")
    @Operation(summary = "设置默认银行卡")
    public CommonResult<Boolean> setDefaultBankCard(@Valid @RequestBody AppBankCardDefaultReqVO reqVO) {
        appWalletService.setDefaultBankCard(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @DeleteMapping("/bank-card/delete")
    @Operation(summary = "删除银行卡")
    @Parameter(name = "id", description = "银行卡 ID", required = true)
    public CommonResult<Boolean> deleteBankCard(@RequestParam("id") Long id) {
        appWalletService.deleteBankCard(getLoginUserId(), id);
        return success(Boolean.TRUE);
    }

    @GetMapping("/flow/page")
    @Operation(summary = "获取钱包流水分页")
    public CommonResult<PageResult<AppWalletFlowRespVO>> getWalletFlowPage(@Valid AppWalletFlowPageReqVO reqVO) {
        return success(appWalletService.getWalletFlowPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/flow/get")
    @Operation(summary = "获取钱包流水详情")
    public CommonResult<AppWalletFlowRespVO> getWalletFlow(@RequestParam("id") Long id) {
        return success(appWalletService.getWalletFlow(getLoginUserId(), id));
    }
}
