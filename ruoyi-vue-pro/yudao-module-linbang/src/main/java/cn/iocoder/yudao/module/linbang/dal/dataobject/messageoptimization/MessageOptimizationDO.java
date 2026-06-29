package cn.iocoder.yudao.module.linbang.dal.dataobject.messageoptimization;

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
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("lb_message_optimization")
@KeySequence("lb_message_optimization_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageOptimizationDO extends BaseDO {

    @TableId
    private Long id;

    private String refType;

    private Long templateId;

    private Long campaignId;

    private String sceneCode;

    private String messageCategory;

    private String channelType;

    private LocalDate statStartDate;

    private LocalDate statEndDate;

    private BigDecimal reachRate;

    private BigDecimal clickRate;

    private String optimizationNote;

    private String nextAction;

    private String owner;

    private LocalDateTime deadline;
}
