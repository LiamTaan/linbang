package cn.iocoder.yudao.module.linbang.dal.dataobject.escrowproof;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

@TableName("lb_escrow_proof")
@KeySequence("lb_escrow_proof_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscrowProofDO extends BaseDO {

    @TableId
    private Long id;

    private String proofNo;

    private Long orderId;

    private Long unitId;

    private Long userId;

    private Long merchantId;

    private BigDecimal escrowAmount;

    private String proofStatus;

    private String lockReason;

    private String unlockReason;
}
