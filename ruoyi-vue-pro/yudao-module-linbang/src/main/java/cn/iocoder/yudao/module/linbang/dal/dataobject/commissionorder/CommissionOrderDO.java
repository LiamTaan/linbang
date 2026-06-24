package cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("lb_commission_order")
@KeySequence("lb_commission_order_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionOrderDO extends BaseDO {

    @TableId
    private Long id;

    private String commissionNo;

    private Long promoterId;

    private Long userId;

    private Long sourceOrderId;

    private Long sourceUnitId;

    private String commissionType;

    private BigDecimal commissionAmount;

    private String status;

    private LocalDateTime settleTime;
}
