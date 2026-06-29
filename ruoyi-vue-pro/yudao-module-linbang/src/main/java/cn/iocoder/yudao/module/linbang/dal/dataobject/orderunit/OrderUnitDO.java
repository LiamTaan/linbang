package cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit;

import lombok.*;
import java.math.BigDecimal;
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
     * 单元类型
     */
    private String unitType;
    /**
     * 单元金额
     */
    private BigDecimal unitAmount;
    /**
     * 拆分模式
     */
    private String splitMode;
    /**
     * 单元内容摘要
     */
    private String unitContent;
    /**
     * 单元进度描述
     */
    private String unitProgress;
    /**
     * 单元服务人数
     */
    private Integer workerCount;
    /**
     * 单元金额上限快照
     */
    private BigDecimal maxAmountLimit;
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
     * 派单状态
     */
    private String dispatchStatus;
    /**
     * 当前推送批次号
     */
    private Integer currentBatchNo;
    /**
     * 流单时间
     */
    private LocalDateTime flowTime;
    /**
     * 流单原因
     */
    private String flowReason;
    /**
     * 自动退款状态
     */
    private String autoRefundStatus;
    /**
     * 自动退款单 ID
     */
    private Long autoRefundId;
    /**
     * 完成时间
     */
    private LocalDateTime finishTime;
    /**
     * 申诉截止时间
     */
    private LocalDateTime appealExpireTime;
    /**
     * 核销码
     */
    private String verifyCode;
    /**
     * 核销状态
     */
    private String verifyStatus;
    /**
     * 核销时间
     */
    private LocalDateTime verifyTime;
    /**
     * 核销人
     */
    private Long verifyBy;
    /**
     * 核销备注
     */
    private String verifyRemark;

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public Integer getUnitSeq() {
        return unitSeq;
    }

    public String getUnitTitle() {
        return unitTitle;
    }

    public BigDecimal getUnitAmount() {
        return unitAmount;
    }

    public String getSplitMode() {
        return splitMode;
    }

    public Long getPrevUnitId() {
        return prevUnitId;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public String getLockReason() {
        return lockReason;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getAcceptDeadlineTime() {
        return acceptDeadlineTime;
    }

    public String getDispatchStatus() {
        return dispatchStatus;
    }

    public Integer getCurrentBatchNo() {
        return currentBatchNo;
    }

    public LocalDateTime getFlowTime() {
        return flowTime;
    }

    public String getFlowReason() {
        return flowReason;
    }

    public String getAutoRefundStatus() {
        return autoRefundStatus;
    }

    public Long getAutoRefundId() {
        return autoRefundId;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

}
