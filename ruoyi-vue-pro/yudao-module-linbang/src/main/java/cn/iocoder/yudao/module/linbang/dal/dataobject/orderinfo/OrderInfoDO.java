package cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 订单主 DO
 *
 * @author dawn
 */
@TableName("lb_order_info")
@KeySequence("lb_order_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 下单用户ID
     */
    private Long userId;
    /**
     * 服务商ID
     */
    private Long merchantId;
    /**
     * 类目ID
     */
    private Long categoryId;
    /**
     * 计价方式
     *
     * 枚举 {@link TODO lb_pricing_mode 对应的类}
     */
    private String pricingMode;
    /**
     * 预算金额
     */
    private BigDecimal budgetAmount;
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    /**
     * 服务时长说明
     */
    private String serviceDurationDesc;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 服务人数
     */
    private Integer workerCount;
    /**
     * 需求描述
     */
    private String requireDesc;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String district;
    /**
     * 街道
     */
    private String street;
    /**
     * 详细地址
     */
    private String detailAddress;
    /**
     * 经度
     */
    private BigDecimal longitude;
    /**
     * 纬度
     */
    private BigDecimal latitude;
    /**
     * 是否开票
     */
    private Boolean needInvoice;
    /**
     * 是否拆单
     */
    private Boolean needSplit;
    /**
     * 命中拆单规则ID
     */
    private Long splitRuleId;
    /**
     * 拆单状态
     */
    private String splitStatus;
    /**
     * 拆分方式
     */
    private String splitMode;
    /**
     * 拆单规则快照
     */
    private String splitRuleSnapshot;
    /**
     * 协议是否确认
     */
    private Boolean agreementConfirmed;
    /**
     * 交易保障协议版本
     */
    private String tradeAgreementVersion;
    /**
     * 交易保障协议确认时间
     */
    private LocalDateTime tradeAgreementConfirmedTime;
    /**
     * 是否确认防逃单提醒
     */
    private Boolean antiEscapeConfirmed;
    /**
     * 支付订单ID
     */
    private Long payOrderId;
    /**
     * 是否需要保证金
     */
    private Boolean depositRequired;
    /**
     * 保证金金额
     */
    private BigDecimal depositAmount;
    /**
     * 保证金支付状态
     */
    private String depositPayStatus;
    /**
     * 保证金支付单ID
     */
    private Long depositPayOrderId;
    /**
     * 保证金支付时间
     */
    private LocalDateTime depositPaidTime;
    /**
     * 关联商城消费记录 ID
     */
    private Long mallConsumeRecordId;
    /**
     * 关联商城消费记录编号
     */
    private String mallConsumeRecordNo;
    /**
     * 关联商城消费金额
     */
    private BigDecimal mallConsumeAmount;
    /**
     * 关联商城消费状态
     */
    private String mallConsumeStatus;
    /**
     * 推广抵扣金额
     */
    private BigDecimal promoteDeductAmount;
    /**
     * 推广抵扣来源类型
     */
    private String promoteDeductSourceType;
    /**
     * 推广抵扣来源 ID
     */
    private Long promoteDeductSourceId;
    /**
     * 推广抵扣来源编号
     */
    private String promoteDeductSourceNo;
    /**
     * 抵扣后订单应付金额
     */
    private BigDecimal payableAmountAfterDeduct;
    /**
     * 订单状态
     *
     * 枚举 {@link TODO lb_order_status 对应的类}
     */
    private String status;


}
