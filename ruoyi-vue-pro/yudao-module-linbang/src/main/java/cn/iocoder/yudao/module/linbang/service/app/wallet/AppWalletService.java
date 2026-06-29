package cn.iocoder.yudao.module.linbang.service.app.wallet;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardDefaultReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletBillPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletBillRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletFlowPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletFlowRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawRespVO;

import javax.validation.Valid;

public interface AppWalletService {

    AppWalletAccountRespVO getWalletAccount(Long authUserId);

    PageResult<AppBankCardRespVO> getBankCardPage(Long authUserId, AppBankCardPageReqVO reqVO);

    AppBankCardRespVO getBankCard(Long authUserId, Long id);

    Long createWithdraw(Long authUserId, @Valid AppWalletWithdrawCreateReqVO reqVO);

    PageResult<AppWalletWithdrawRespVO> getWithdrawPage(Long authUserId, AppWalletWithdrawPageReqVO reqVO);

    AppWalletWithdrawRespVO getWithdraw(Long authUserId, Long id);

    Long createBankCard(Long authUserId, @Valid AppBankCardCreateReqVO reqVO);

    void updateBankCard(Long authUserId, @Valid AppBankCardUpdateReqVO reqVO);

    void setDefaultBankCard(Long authUserId, @Valid AppBankCardDefaultReqVO reqVO);

    void deleteBankCard(Long authUserId, Long id);

    PageResult<AppWalletBillRespVO> getWalletBillPage(Long authUserId, AppWalletBillPageReqVO reqVO);

    PageResult<AppWalletFlowRespVO> getWalletFlowPage(Long authUserId, AppWalletFlowPageReqVO reqVO);

    AppWalletFlowRespVO getWalletFlow(Long authUserId, Long id);
}
