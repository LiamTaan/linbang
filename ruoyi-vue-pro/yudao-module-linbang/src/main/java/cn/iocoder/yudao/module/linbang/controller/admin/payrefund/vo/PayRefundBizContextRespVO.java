package cn.iocoder.yudao.module.linbang.controller.admin.payrefund.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 退款业务上下文 Response VO")
@Data
public class PayRefundBizContextRespVO {

    @Schema(description = "支付中心退款 ID")
    private Long payRefundId;
    @Schema(description = "业务侧退款单号，用于关联支付中心退款单")
    private String merchantRefundId;
    @Schema(description = "主订单 ID，关联主订单")
    private Long orderId;
    @Schema(description = "订单单元 ID，关联拆分后的订单单元")
    private Long unitId;
    @Schema(description = "关联主订单摘要")
    private OrderSimpleRespVO order;
    @Schema(description = "关联订单单元摘要")
    private OrderUnitSimpleRespVO unit;
    @Schema(description = "关联钱包流水列表")
    private List<WalletFlowSimpleRespVO> walletFlows;
    @Schema(description = "关联投诉记录列表")
    private List<ComplaintSimpleRespVO> complaints;
    @Schema(description = "关联申诉记录列表")
    private List<AppealSimpleRespVO> appeals;
    @Schema(description = "关联业务操作日志列表")
    private List<OrderOperateLogSimpleRespVO> operateLogs;

    @Data
    @Schema(description = "管理后台 - 主订单摘要 Response VO")
    public static class OrderSimpleRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "主订单业务编号")
        private String orderNo;
        @Schema(description = "平台用户 ID，关联用户档案")
        private Long userId;
        @Schema(description = "服务商 ID，关联服务商档案")
        private Long merchantId;
        @Schema(description = "计价方式：FIXED_PRICE 一口价、CONTRACT 承包、OUTSOURCING 外发、HOURLY 计时、BY_UNIT 按单位")
        private String pricingMode;
        @Schema(description = "主订单金额，单位：元，保留两位小数")
        private BigDecimal orderAmount;
        @Schema(description = "拆单状态：UNSPLIT 未拆单、SPLIT 已拆单")
        private String splitStatus;
        @Schema(description = "主订单状态：PENDING_PAY 待支付、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、PENDING_CONFIRM 待确认、AFTER_SALE 售后中、FINISHED 已完成、REFUNDED 已退款、CLOSED 已关闭")
        private String status;
        @Schema(description = "支付中心订单 ID")
        private Long payOrderId;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "管理后台 - 订单单元摘要 Response VO")
    public static class OrderUnitSimpleRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "主订单 ID，关联主订单")
        private Long orderId;
        @Schema(description = "订单单元业务编号")
        private String unitNo;
        @Schema(description = "订单单元在主订单中的顺序号，从 1 开始")
        private Integer unitSeq;
        @Schema(description = "订单单元标题")
        private String unitTitle;
        @Schema(description = "订单单元金额，单位：元，保留两位小数")
        private BigDecimal unitAmount;
        @Schema(description = "订单单元资金是否已锁定：true 已锁定、false 未锁定")
        private Boolean isLocked;
        @Schema(description = "订单单元资金锁定原因")
        private String lockReason;
        @Schema(description = "服务商 ID，关联服务商档案")
        private Long merchantId;
        @Schema(description = "订单单元状态：PENDING_CREATE 待生成、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、PENDING_CONFIRM 待验收、FINISHED 已完成、APPEALING 申诉中、REFUNDED 已退款、CLOSED 已关闭")
        private String status;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "管理后台 - 钱包流水摘要 Response VO")
    public static class WalletFlowSimpleRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "钱包流水业务编号")
        private String flowNo;
        @Schema(description = "平台用户 ID，关联用户档案")
        private Long userId;
        @Schema(description = "钱包账户 ID，关联用户对应角色的钱包账户")
        private Long walletAccountId;
        @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
        private String bizType;
        @Schema(description = "钱包流水方向：IN 入账、OUT 出账、FREEZE 冻结、UNFREEZE 解冻")
        private String flowType;
        @Schema(description = "钱包余额变动金额，单位：元，正数入账、负数出账，保留两位小数")
        private BigDecimal changeAmount;
        @Schema(description = "变动前钱包余额，单位：元，保留两位小数")
        private BigDecimal beforeAmount;
        @Schema(description = "变动后钱包余额，单位：元，保留两位小数")
        private BigDecimal afterAmount;
        @Schema(description = "关联主订单 ID")
        private Long relatedOrderId;
        @Schema(description = "关联订单单元 ID")
        private Long relatedUnitId;
        @Schema(description = "关联支付中心订单 ID")
        private Long relatedPayOrderId;
        @Schema(description = "关联支付中心退款 ID")
        private Long relatedRefundId;
        @Schema(description = "业务备注")
        private String remark;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "管理后台 - 投诉摘要 Response VO")
    public static class ComplaintSimpleRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "投诉业务编号")
        private String complaintNo;
        @Schema(description = "主订单 ID，关联主订单")
        private Long orderId;
        @Schema(description = "订单单元 ID，关联拆分后的订单单元")
        private Long unitId;
        @Schema(description = "投诉发起用户 ID，关联平台用户")
        private Long complainantUserId;
        @Schema(description = "被投诉用户 ID，关联平台用户")
        private Long respondentUserId;
        @Schema(description = "投诉类型，按平台投诉类型字典展示")
        private String complaintType;
        @Schema(description = "业务正文内容")
        private String content;
        @Schema(description = "投诉状态：PENDING 待受理、PROCESSING 处理中、FINISHED 已完结、REJECTED 已驳回")
        private String status;
        @Schema(description = "投诉处理结果说明")
        private String resultDesc;
        @Schema(description = "处理时间")
        private LocalDateTime handleTime;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "管理后台 - 申诉摘要 Response VO")
    public static class AppealSimpleRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "申诉业务编号")
        private String appealNo;
        @Schema(description = "主订单 ID，关联主订单")
        private Long orderId;
        @Schema(description = "订单单元 ID，关联拆分后的订单单元")
        private Long unitId;
        @Schema(description = "平台用户 ID，关联用户档案")
        private Long userId;
        @Schema(description = "申诉类型，按平台申诉类型字典展示")
        private String appealType;
        @Schema(description = "业务正文内容")
        private String content;
        @Schema(description = "申诉状态：PENDING 待审核、PROCESSING 处理中、APPROVED 已通过、REJECTED 已驳回、FINISHED 已完结")
        private String status;
        @Schema(description = "申诉审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
        private String auditStatus;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "审核驳回原因")
        private String rejectReason;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "管理后台 - 订单操作日志摘要 Response VO")
    public static class OrderOperateLogSimpleRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "主订单 ID，关联主订单")
        private Long orderId;
        @Schema(description = "订单单元 ID，关联拆分后的订单单元")
        private Long unitId;
        @Schema(description = "操作类型，按订单或关联业务操作类型枚举展示")
        private String operateType;
        @Schema(description = "操作人角色编码")
        private String operateRole;
        @Schema(description = "操作人用户或后台用户 ID")
        private Long operateBy;
        @Schema(description = "操作前的业务状态；取值沿用关联业务对象的状态机")
        private String beforeStatus;
        @Schema(description = "操作后的业务状态；取值沿用关联业务对象的状态机")
        private String afterStatus;
        @Schema(description = "业务备注")
        private String remark;
        @Schema(description = "操作时间")
        private LocalDateTime operateTime;
    }

}
