package cn.iocoder.yudao.module.linbang.service.walletaccount;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 钱包账户 Service 接口
 *
 * @author dawn
 */
public interface WalletAccountService {

    /**
     * 创建钱包账户
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWalletAccount(@Valid WalletAccountSaveReqVO createReqVO);

    /**
     * 更新钱包账户
     *
     * @param updateReqVO 更新信息
     */
    void updateWalletAccount(@Valid WalletAccountSaveReqVO updateReqVO);

    /**
     * 删除钱包账户
     *
     * @param id 编号
     */
    void deleteWalletAccount(Long id);

    /**
    * 批量删除钱包账户
    *
    * @param ids 编号
    */
    void deleteWalletAccountListByIds(List<Long> ids);

    /**
     * 获得钱包账户
     *
     * @param id 编号
     * @return 钱包账户
     */
    WalletAccountDO getWalletAccount(Long id);

    /**
     * 获得钱包账户详情
     *
     * @param id 编号
     * @return 钱包账户详情
     */
    WalletAccountDetailRespVO getWalletAccountDetail(Long id);

    /**
     * 获得钱包账户分页
     *
     * @param pageReqVO 分页查询
     * @return 钱包账户分页
     */
    PageResult<WalletAccountRespVO> getWalletAccountPage(WalletAccountPageReqVO pageReqVO);

}
