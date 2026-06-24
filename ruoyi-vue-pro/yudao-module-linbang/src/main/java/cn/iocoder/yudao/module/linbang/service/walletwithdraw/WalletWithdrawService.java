package cn.iocoder.yudao.module.linbang.service.walletwithdraw;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 提现申请 Service 接口
 *
 * @author dawn
 */
public interface WalletWithdrawService {

    /**
     * 创建提现申请
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createWalletWithdraw(@Valid WalletWithdrawSaveReqVO createReqVO);

    /**
     * 更新提现申请
     *
     * @param updateReqVO 更新信息
     */
    void updateWalletWithdraw(@Valid WalletWithdrawSaveReqVO updateReqVO);

    /**
     * 删除提现申请
     *
     * @param id 编号
     */
    void deleteWalletWithdraw(Long id);

    /**
    * 批量删除提现申请
    *
    * @param ids 编号
    */
    void deleteWalletWithdrawListByIds(List<Long> ids);

    /**
     * 获得提现申请
     *
     * @param id 编号
     * @return 提现申请
     */
    WalletWithdrawDO getWalletWithdraw(Long id);

    /**
     * 获得提现申请详情
     *
     * @param id 编号
     * @return 提现申请详情
     */
    WalletWithdrawDetailRespVO getWalletWithdrawDetail(Long id);

    /**
     * 审核提现申请
     *
     * @param reqVO 审核请求
     */
    void auditWalletWithdraw(@Valid WithdrawAuditReqVO reqVO);

    /**
     * 获得提现申请分页
     *
     * @param pageReqVO 分页查询
     * @return 提现申请分页
     */
    PageResult<WalletWithdrawRespVO> getWalletWithdrawPage(WalletWithdrawPageReqVO pageReqVO);

}
