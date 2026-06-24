package cn.iocoder.yudao.module.linbang.service.walletflow;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;

/**
 * 钱包流水 Service 接口
 *
 * @author dawn
 */
public interface WalletFlowService {

    /**
     * 创建钱包流水
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWalletFlow(@Valid WalletFlowSaveReqVO createReqVO);

    /**
     * 更新钱包流水
     *
     * @param updateReqVO 更新信息
     */
    void updateWalletFlow(@Valid WalletFlowSaveReqVO updateReqVO);

    /**
     * 删除钱包流水
     *
     * @param id 编号
     */
    void deleteWalletFlow(Long id);

    /**
    * 批量删除钱包流水
    *
    * @param ids 编号
    */
    void deleteWalletFlowListByIds(List<Long> ids);

    /**
     * 获得钱包流水
     *
     * @param id 编号
     * @return 钱包流水
     */
    WalletFlowDO getWalletFlow(Long id);

    /**
     * 获得钱包流水详情
     *
     * @param id 编号
     * @return 钱包流水详情
     */
    WalletFlowDetailRespVO getWalletFlowDetail(Long id);

    /**
     * 获得钱包流水分页
     *
     * @param pageReqVO 分页查询
     * @return 钱包流水分页
     */
    PageResult<WalletFlowRespVO> getWalletFlowPage(WalletFlowPageReqVO pageReqVO);

}
