package cn.iocoder.yudao.module.linbang.dal.dataobject.orderabnormal;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 异常订单 DO
 *
 * @author dawn
 */
@TableName("lb_order_abnormal")
@KeySequence("lb_order_abnormal_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAbnormalDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 单元ID
     */
    private Long unitId;
    /**
     * 异常单号
     */
    private String abnormalNo;
    /**
     * 异常类型
     */
    private String abnormalType;
    /**
     * 风险等级
     */
    private String riskLevel;
    /**
     * 命中规则编码
     */
    private String hitRuleCode;
    /**
     * 处理状态
     */
    private String handleStatus;
    /**
     * 处理人
     */
    private Long handleBy;
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    /**
     * 备注
     */
    private String remark;

    /**
     * 终审状态
     */
    private String finalAuditStatus;

    /**
     * 终审人
     */
    private Long finalAuditBy;

    /**
     * 终审时间
     */
    private LocalDateTime finalAuditTime;

    /**
     * 终审意见
     */
    private String finalAuditRemark;


}
