package cn.iocoder.yudao.module.linbang.dal.dataobject.matchpushbatch;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 派单推送批次 DO
 */
@TableName("lb_match_push_batch")
@KeySequence("lb_match_push_batch_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchPushBatchDO extends BaseDO {

    @TableId
    private Long id;

    private Long orderId;

    private Long unitId;

    private Integer stageNo;

    private Integer pushBatchNo;

    private BigDecimal radiusStartKm;

    private BigDecimal radiusEndKm;

    private LocalDateTime plannedAt;

    private LocalDateTime expiredAt;

    private String status;

    private String triggerType;
}
