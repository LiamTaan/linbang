package cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 拆分单元 DO
 *
 * @author dawn
 */
@TableName("lb_order_unit")
@KeySequence("lb_order_unit_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUnitDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 主订单ID
     */
    private Long orderId;
    /**
     * 单元订单号
     */
    private String unitNo;
    /**
     * 单元序号
     */
    private Integer unitSeq;
    /**
     * 单元标题
     */
    private String unitTitle;
    /**
     * 单元金额
     */
    private BigDecimal unitAmount;
    /**
     * 拆分模式
     */
    private String splitMode;
    /**
     * 前置单元ID
     */
    private Long prevUnitId;
    /**
     * 是否锁定
     */
    private Boolean isLocked;
    /**
     * 锁定原因
     */
    private String lockReason;
    /**
     * 服务商ID
     */
    private Long merchantId;
    /**
     * 单元状态
     *
     * 枚举 {@link TODO lb_order_unit_status 对应的类}
     */
    private String status;
    /**
     * 接单截止时间
     */
    private LocalDateTime acceptDeadlineTime;
    /**
     * 完成时间
     */
    private LocalDateTime finishTime;


}