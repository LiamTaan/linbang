package cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;

/**
 * 钱包流水 DO
 *
 * @author dawn
 */
@TableName("lb_wallet_flow")
@KeySequence("lb_wallet_flow_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletFlowDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 流水号
     */
    private String flowNo;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 钱包账户ID
     */
    private Long walletAccountId;
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 流水类型
     */
    private String flowType;
    /**
     * 变动金额
     */
    private BigDecimal changeAmount;
    /**
     * 变动前金额
     */
    private BigDecimal beforeAmount;
    /**
     * 变动后金额
     */
    private BigDecimal afterAmount;
    /**
     * 关联订单ID
     */
    private Long relatedOrderId;
    /**
     * 关联单元ID
     */
    private Long relatedUnitId;
    /**
     * 关联支付订单ID
     */
    private Long relatedPayOrderId;
    /**
     * 关联退款ID
     */
    private Long relatedRefundId;
    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
