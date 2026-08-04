package cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletWithdrawStatDTO {

    private Integer totalCount;
    private BigDecimal totalApplyAmount;
    private Integer pendingCount;
    private BigDecimal pendingAmount;
    private Integer successCount;
    private BigDecimal successAmount;

}
