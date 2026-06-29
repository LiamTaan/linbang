package cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;

/**
 * 订单匹配记录 DO
 *
 * @author dawn
 */
@TableName("lb_order_match_record")
@KeySequence("lb_order_match_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMatchRecordDO {

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
     * 服务商ID
     */
    private Long merchantId;
    /**
     * 命中规则编码
     */
    private String matchRuleCode;
    /**
     * 匹配分值
     */
    private BigDecimal matchScore;
    /**
     * 距离公里
     */
    private BigDecimal distanceKm;
    /**
     * 推送时间
     */
    private LocalDateTime pushTime;
    /**
     * 推送阶段号
     */
    private Integer stageNo;
    /**
     * 推送批次号
     */
    private Integer pushBatchNo;
    /**
     * 优先层
     */
    private String priorityLayer;
    /**
     * 是否命中优先池
     */
    private Boolean priorityPoolFlag;
    /**
     * 品类命中等级
     */
    private String categoryMatchLevel;
    /**
     * 接单截止时间
     */
    private LocalDateTime acceptDeadlineTime;
    /**
     * 记录过期时间
     */
    private LocalDateTime expiredTime;
    /**
     * 状态
     */
    private String status;
    /**
     * 最终结果
     */
    private String finalResult;
    /**
     * 租户编号
     */
    private Long tenantId;


}
