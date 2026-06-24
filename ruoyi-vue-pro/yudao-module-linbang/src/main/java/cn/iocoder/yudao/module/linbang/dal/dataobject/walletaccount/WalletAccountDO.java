package cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 钱包账户 DO
 *
 * @author dawn
 */
@TableName("lb_wallet_account")
@KeySequence("lb_wallet_account_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletAccountDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 角色编码
     *
     * 枚举 {@link TODO lb_role_code 对应的类}
     */
    private String roleCode;
    /**
     * 总资产
     */
    private BigDecimal totalAmount;
    /**
     * 可提现金额
     */
    private BigDecimal availableAmount;
    /**
     * 冻结金额
     */
    private BigDecimal frozenAmount;
    /**
     * 托管金额
     */
    private BigDecimal escrowAmount;
    /**
     * 佣金金额
     */
    private BigDecimal commissionAmount;
    /**
     * 状态
     */
    private String status;


}