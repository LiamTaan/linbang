package cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 风控规则表 DO
 *
 * @author dawn
 */
@TableName("lb_risk_rule")
@KeySequence("lb_risk_rule_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 规则编码
     */
    private String ruleCode;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 规则分组
     */
    private String ruleGroup;
    /**
     * 规则值
     */
    private String ruleValue;
    /**
     * 值类型
     */
    private String valueType;
    /**
     * 状态
     */
    private String status;
    /**
     * 备注
     */
    private String remark;


}