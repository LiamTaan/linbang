package cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 提现申请 DO
 *
 * @author dawn
 */
@TableName("lb_wallet_withdraw")
@KeySequence("lb_wallet_withdraw_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletWithdrawDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 提现单号
     */
    private String withdrawNo;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 钱包账户ID
     */
    private Long walletAccountId;
    /**
     * 银行卡ID
     */
    private Long bankCardId;
    /**
     * 申请金额
     */
    private BigDecimal applyAmount;
    /**
     * 手续费
     */
    private BigDecimal feeAmount;
    /**
     * 实际到账金额
     */
    private BigDecimal realAmount;
    /**
     * 状态
     *
     * 枚举 {@link TODO lb_withdraw_status 对应的类}
     */
    private String status;
    /**
     * 审核状态
     *
     * 枚举 {@link TODO lb_withdraw_status 对应的类}
     */
    private String auditStatus;
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 审核人
     */
    private Long auditBy;
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    /**
     * 驳回原因
     */
    private String rejectReason;
    /**
     * 打款时间
     */
    private LocalDateTime payTime;
    /**
     * 转账单 ID
     */
    private Long payTransferId;
    /**
     * 转账单号
     */
    private String payTransferNo;
    /**
     * 转账失败原因
     */
    private String transferErrorMsg;


}
