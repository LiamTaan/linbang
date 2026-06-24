package cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo.*;

/**
 * 钱包账户 Mapper
 *
 * @author dawn
 */
@Mapper
public interface WalletAccountMapper extends BaseMapperX<WalletAccountDO> {

    default PageResult<WalletAccountDO> selectPage(WalletAccountPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WalletAccountDO>()
                .eqIfPresent(WalletAccountDO::getUserId, reqVO.getUserId())
                .inIfPresent(WalletAccountDO::getUserId, userIds)
                .eqIfPresent(WalletAccountDO::getRoleCode, reqVO.getRoleCode())
                .eqIfPresent(WalletAccountDO::getTotalAmount, reqVO.getTotalAmount())
                .eqIfPresent(WalletAccountDO::getAvailableAmount, reqVO.getAvailableAmount())
                .eqIfPresent(WalletAccountDO::getFrozenAmount, reqVO.getFrozenAmount())
                .eqIfPresent(WalletAccountDO::getEscrowAmount, reqVO.getEscrowAmount())
                .eqIfPresent(WalletAccountDO::getCommissionAmount, reqVO.getCommissionAmount())
                .eqIfPresent(WalletAccountDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(WalletAccountDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(WalletAccountDO::getId));
    }

}
