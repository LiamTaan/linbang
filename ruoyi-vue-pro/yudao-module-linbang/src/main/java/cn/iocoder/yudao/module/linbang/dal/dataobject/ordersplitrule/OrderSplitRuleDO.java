package cn.iocoder.yudao.module.linbang.dal.dataobject.ordersplitrule;

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

/**
 * 订单拆单规则 DO
 */
@TableName("lb_order_split_rule")
@KeySequence("lb_order_split_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSplitRuleDO extends BaseDO {

    @TableId
    private Long id;

    private String ruleName;

    private String ruleCode;

    private String matchMode;

    private Long categoryId;

    private String applicablePricingModes;

    private BigDecimal minOrderAmount;

    private BigDecimal minQuantity;

    private Integer minWorkerCount;

    private String splitMode;

    private Integer defaultUnitCount;

    private BigDecimal unitAmountLimit;

    private String unitTemplate;

    private Integer sortNo;

    private String status;

    private String remark;

}
