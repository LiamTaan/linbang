package cn.iocoder.yudao.module.linbang.dal.dataobject.riskevent;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("lb_risk_event")
@KeySequence("lb_risk_event_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskEventDO extends BaseDO {

    @TableId
    private Long id;

    private String bizType;

    private Long bizId;

    private String riskType;

    private String riskLevel;

    private String hitRuleCode;

    private String status;

    private Long handleBy;

    private LocalDateTime handleTime;

    private String remark;
}
