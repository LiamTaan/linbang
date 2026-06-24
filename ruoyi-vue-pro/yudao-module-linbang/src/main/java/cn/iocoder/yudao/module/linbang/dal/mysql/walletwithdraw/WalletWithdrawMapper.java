package cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw;

import cn.hutool.core.util.StrUtil;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo.*;

/**
 * 提现申请 Mapper
 *
 * @author dawn
 */
@Mapper
public interface WalletWithdrawMapper extends BaseMapperX<WalletWithdrawDO> {

    default List<WalletWithdrawDO> selectListByWithdrawNo(String withdrawNo) {
        if (StrUtil.isBlank(withdrawNo)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<WalletWithdrawDO>()
                .like(WalletWithdrawDO::getWithdrawNo, withdrawNo)
                .orderByDesc(WalletWithdrawDO::getId));
    }

    default PageResult<WalletWithdrawDO> selectPage(WalletWithdrawPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WalletWithdrawDO>()
                .eqIfPresent(WalletWithdrawDO::getWithdrawNo, reqVO.getWithdrawNo())
                .eqIfPresent(WalletWithdrawDO::getUserId, reqVO.getUserId())
                .inIfPresent(WalletWithdrawDO::getUserId, userIds)
                .eqIfPresent(WalletWithdrawDO::getWalletAccountId, reqVO.getWalletAccountId())
                .eqIfPresent(WalletWithdrawDO::getBankCardId, reqVO.getBankCardId())
                .eqIfPresent(WalletWithdrawDO::getApplyAmount, reqVO.getApplyAmount())
                .eqIfPresent(WalletWithdrawDO::getFeeAmount, reqVO.getFeeAmount())
                .eqIfPresent(WalletWithdrawDO::getRealAmount, reqVO.getRealAmount())
                .eqIfPresent(WalletWithdrawDO::getStatus, reqVO.getStatus())
                .eqIfPresent(WalletWithdrawDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(WalletWithdrawDO::getAuditRemark, reqVO.getAuditRemark())
                .eqIfPresent(WalletWithdrawDO::getAuditBy, reqVO.getAuditBy())
                .betweenIfPresent(WalletWithdrawDO::getAuditTime, reqVO.getAuditTime())
                .eqIfPresent(WalletWithdrawDO::getRejectReason, reqVO.getRejectReason())
                .betweenIfPresent(WalletWithdrawDO::getPayTime, reqVO.getPayTime())
                .betweenIfPresent(WalletWithdrawDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(WalletWithdrawDO::getId));
    }

}
