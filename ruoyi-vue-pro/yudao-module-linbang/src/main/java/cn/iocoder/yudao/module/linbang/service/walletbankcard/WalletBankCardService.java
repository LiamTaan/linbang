package cn.iocoder.yudao.module.linbang.service.walletbankcard;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;

/**
 * 用户银行卡 Service 接口
 *
 * @author dawn
 */
public interface WalletBankCardService {

    /**
     * 创建用户银行卡
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWalletBankCard(@Valid WalletBankCardSaveReqVO createReqVO);

    /**
     * 更新用户银行卡
     *
     * @param updateReqVO 更新信息
     */
    void updateWalletBankCard(@Valid WalletBankCardSaveReqVO updateReqVO);

    /**
     * 删除用户银行卡
     *
     * @param id 编号
     */
    void deleteWalletBankCard(Long id);

    /**
    * 批量删除用户银行卡
    *
    * @param ids 编号
    */
    void deleteWalletBankCardListByIds(List<Long> ids);

    /**
     * 获得用户银行卡
     *
     * @param id 编号
     * @return 用户银行卡
     */
    WalletBankCardDO getWalletBankCard(Long id);

    /**
     * 获得用户银行卡详情
     *
     * @param id 编号
     * @return 用户银行卡详情
     */
    WalletBankCardDetailRespVO getWalletBankCardDetail(Long id);

    /**
     * 获得用户银行卡分页
     *
     * @param pageReqVO 分页查询
     * @return 用户银行卡分页
     */
    PageResult<WalletBankCardRespVO> getWalletBankCardPage(WalletBankCardPageReqVO pageReqVO);

}
