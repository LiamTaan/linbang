package cn.iocoder.yudao.module.linbang.dal.dataobject.orderdividerecord;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

@TableName("lb_order_divide_record")
@KeySequence("lb_order_divide_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDivideRecordDO extends BaseDO {

    @TableId
    private Long id;

    private String divideNo;

    private Long orderId;

    private Long unitId;

    private Long divideRuleId;

    private String targetType;

    private Long targetBizId;

    private BigDecimal divideRate;

    private BigDecimal divideAmount;

    private BigDecimal taxAmount;

    private String settleStatus;

    private String cityLevel;

    private Long categoryId;

    private String remark;
}
