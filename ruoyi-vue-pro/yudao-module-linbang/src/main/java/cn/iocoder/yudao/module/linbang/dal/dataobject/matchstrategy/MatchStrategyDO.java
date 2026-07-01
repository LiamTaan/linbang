package cn.iocoder.yudao.module.linbang.dal.dataobject.matchstrategy;

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

/**
 * 智能匹配策略 DO
 */
@TableName("lb_match_strategy")
@KeySequence("lb_match_strategy_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchStrategyDO extends BaseDO {

    @TableId
    private Long id;

    private String strategyCode;

    private String strategyName;

    private String stageConfigJson;

    private Integer maxStageCount;

    private java.math.BigDecimal maxRadiusKm;

    private String flowAdviceTemplate;

    private Boolean autoDispatchEnabled;

    private Boolean autoRefundEnabled;

    private Integer autoRefundRetryTimes;

    private String status;
}
