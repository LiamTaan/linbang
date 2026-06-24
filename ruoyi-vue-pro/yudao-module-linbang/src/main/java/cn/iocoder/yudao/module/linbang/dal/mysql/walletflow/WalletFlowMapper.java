package cn.iocoder.yudao.module.linbang.dal.mysql.walletflow;

import java.util.List;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo.*;

/**
 * 钱包流水 Mapper
 *
 * @author dawn
 */
@Mapper
public interface WalletFlowMapper extends BaseMapperX<WalletFlowDO> {

    default PageResult<WalletFlowDO> selectPage(WalletFlowPageReqVO reqVO, List<Long> matchedUserIds,
                                                List<Long> matchedOrderIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WalletFlowDO>()
                .eqIfPresent(WalletFlowDO::getFlowNo, reqVO.getFlowNo())
                .inIfPresent(WalletFlowDO::getUserId, matchedUserIds)
                .eqIfPresent(WalletFlowDO::getWalletAccountId, reqVO.getWalletAccountId())
                .eqIfPresent(WalletFlowDO::getBizType, reqVO.getBizType())
                .eqIfPresent(WalletFlowDO::getFlowType, reqVO.getFlowType())
                .eqIfPresent(WalletFlowDO::getChangeAmount, reqVO.getChangeAmount())
                .eqIfPresent(WalletFlowDO::getBeforeAmount, reqVO.getBeforeAmount())
                .eqIfPresent(WalletFlowDO::getAfterAmount, reqVO.getAfterAmount())
                .inIfPresent(WalletFlowDO::getRelatedOrderId, matchedOrderIds)
                .eqIfPresent(WalletFlowDO::getRelatedUnitId, reqVO.getRelatedUnitId())
                .eqIfPresent(WalletFlowDO::getRelatedPayOrderId, reqVO.getRelatedPayOrderId())
                .eqIfPresent(WalletFlowDO::getRelatedRefundId, reqVO.getRelatedRefundId())
                .eqIfPresent(WalletFlowDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(WalletFlowDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(WalletFlowDO::getId));
    }

}
