package cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw;

import cn.hutool.core.util.StrUtil;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawStatDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo.*;

/**
 * 提现申请 Mapper
 *
 * @author dawn
 */
@Mapper
public interface WalletWithdrawMapper extends BaseMapperX<WalletWithdrawDO> {

    int WITHDRAW_NO_MATCH_LIMIT = 1_000;

    @Select("SELECT COALESCE(SUM(apply_amount), 0) FROM lb_wallet_withdraw "
            + "WHERE create_time >= #{start} AND create_time < #{end} AND deleted = b'0'")
    java.math.BigDecimal selectSumApplyAmount(@Param("start") java.time.LocalDateTime start,
                                              @Param("end") java.time.LocalDateTime end);

    @Select("SELECT COUNT(*) AS total_count, "
            + "COALESCE(SUM(apply_amount), 0) AS total_apply_amount, "
            + "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count, "
            + "COALESCE(SUM(CASE WHEN status = 'PENDING' THEN apply_amount ELSE 0 END), 0) AS pending_amount, "
            + "SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) AS success_count, "
            + "COALESCE(SUM(CASE WHEN status = 'SUCCESS' THEN apply_amount ELSE 0 END), 0) AS success_amount "
            + "FROM lb_wallet_withdraw WHERE bank_card_id = #{bankCardId} AND deleted = b'0'")
    WalletWithdrawStatDTO selectStatsByBankCardId(@Param("bankCardId") Long bankCardId);

    @Select("SELECT COUNT(*) AS total_count, "
            + "COALESCE(SUM(apply_amount), 0) AS total_apply_amount, "
            + "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending_count, "
            + "COALESCE(SUM(CASE WHEN status = 'PENDING' THEN apply_amount ELSE 0 END), 0) AS pending_amount, "
            + "SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) AS success_count, "
            + "COALESCE(SUM(CASE WHEN status = 'SUCCESS' THEN apply_amount ELSE 0 END), 0) AS success_amount "
            + "FROM lb_wallet_withdraw WHERE wallet_account_id = #{walletAccountId} AND deleted = b'0'")
    WalletWithdrawStatDTO selectStatsByWalletAccountId(@Param("walletAccountId") Long walletAccountId);

    default List<WalletWithdrawDO> selectListByWithdrawNo(String withdrawNo) {
        if (StrUtil.isBlank(withdrawNo)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<WalletWithdrawDO>()
                .like(WalletWithdrawDO::getWithdrawNo, withdrawNo)
                .orderByDesc(WalletWithdrawDO::getId)
                .last("LIMIT " + WITHDRAW_NO_MATCH_LIMIT));
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
