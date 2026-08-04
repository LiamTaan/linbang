package cn.iocoder.yudao.module.pay.dal.mysql.wallet;


import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.pay.controller.app.wallet.vo.transaction.AppPayWalletTransactionPageReqVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.wallet.PayWalletTransactionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Objects;

import static cn.iocoder.yudao.module.pay.controller.app.wallet.vo.transaction.AppPayWalletTransactionPageReqVO.*;

@Mapper
public interface PayWalletTransactionMapper extends BaseMapperX<PayWalletTransactionDO> {

    default PageResult<PayWalletTransactionDO> selectPage(Long walletId, Integer type,
                                                          PageParam pageParam, LocalDateTime[] createTime) {
        LambdaQueryWrapperX<PayWalletTransactionDO> query = new LambdaQueryWrapperX<PayWalletTransactionDO>()
                .eqIfPresent(PayWalletTransactionDO::getWalletId, walletId);
        if (Objects.equals(type, TYPE_INCOME)) {
            query.gt(PayWalletTransactionDO::getPrice, 0);
        } else if (Objects.equals(type, TYPE_EXPENSE)) {
            query.lt(PayWalletTransactionDO::getPrice, 0);
        }
        query.betweenIfPresent(PayWalletTransactionDO::getCreateTime, createTime);
        query.orderByDesc(PayWalletTransactionDO::getId);
        return selectPage(pageParam, query);
    }

    @Select({"<script>",
            "SELECT COALESCE(SUM(price), 0)",
            "FROM pay_wallet_transaction",
            "WHERE wallet_id = #{walletId}",
            "AND create_time BETWEEN #{createTime[0]} AND #{createTime[1]}",
            "<if test='type != null and type == 1'>AND price &gt; 0</if>",
            "<if test='type != null and type == 2'>AND price &lt; 0</if>",
            "</script>"})
    Integer selectPriceSum(@Param("walletId") Long walletId,
                           @Param("type") Integer type,
                           @Param("createTime") LocalDateTime[] createTime);

    default PayWalletTransactionDO selectByNo(String no) {
        return selectOne(PayWalletTransactionDO::getNo, no);
    }

    default PayWalletTransactionDO selectByBiz(String bizId, Integer bizType) {
        return selectOne(PayWalletTransactionDO::getBizId, bizId,
                PayWalletTransactionDO::getBizType, bizType);
    }

}



