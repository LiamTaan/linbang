package cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard;

import java.util.List;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo.*;

/**
 * 用户银行卡 Mapper
 *
 * @author dawn
 */
@Mapper
public interface WalletBankCardMapper extends BaseMapperX<WalletBankCardDO> {

    default PageResult<WalletBankCardDO> selectPage(WalletBankCardPageReqVO reqVO, List<Long> matchedUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<WalletBankCardDO>()
                .inIfPresent(WalletBankCardDO::getUserId, matchedUserIds)
                .likeIfPresent(WalletBankCardDO::getBankName, reqVO.getBankName())
                .eqIfPresent(WalletBankCardDO::getBankCode, reqVO.getBankCode())
                .eqIfPresent(WalletBankCardDO::getCardNoEncrypt, reqVO.getCardNoEncrypt())
                .eqIfPresent(WalletBankCardDO::getCardNoMask, reqVO.getCardNoMask())
                .likeIfPresent(WalletBankCardDO::getAccountName, reqVO.getAccountName())
                .eqIfPresent(WalletBankCardDO::getReservedMobile, reqVO.getReservedMobile())
                .eqIfPresent(WalletBankCardDO::getStatus, reqVO.getStatus())
                .eqIfPresent(WalletBankCardDO::getIsDefault, reqVO.getIsDefault())
                .betweenIfPresent(WalletBankCardDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(WalletBankCardDO::getId));
    }

}
