package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 订单详情 Response VO")
@Data
public class AppOrderDetailRespVO {

    @Schema(description = "订单 ID", example = "1")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "下单用户 ID")
    private Long userId;

    @Schema(description = "当前接单服务商 ID")
    private Long merchantId;

    @Schema(description = "服务类目 ID")
    private Long categoryId;

    @Schema(description = "服务类目名称")
    private String categoryName;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
    private String pricingMode;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "订单应付金额")
    private BigDecimal orderAmount;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "服务人数")
    private Integer workerCount;

    @Schema(description = "服务时长/工期说明，例如 1小时、半天、今天内、3天内上门")
    private String serviceDurationDesc;

    @Schema(description = "用户需求说明")
    private String requireDesc;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String district;

    @Schema(description = "街道")
    private String street;

    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "是否需要发票。当前仅作为订单标记字段")
    private Boolean needInvoice;

    @Schema(description = "是否需要拆单。当前版本下新订单统一为 false，不自动拆单")
    private Boolean needSplit;

    @Schema(description = "命中拆单规则 ID")
    private Long splitRuleId;

    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_STATUS, example = "SPLIT")
    private String splitStatus;

    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE, example = "DIRECT")
    private String splitMode;

    @Schema(description = "是否已确认服务协议")
    private Boolean agreementConfirmed;

    @Schema(description = "交易保障协议版本")
    private String tradeAgreementVersion;

    @Schema(description = "交易保障协议确认时间")
    private LocalDateTime tradeAgreementConfirmedTime;

    @Schema(description = "是否已确认防逃单提醒")
    private Boolean antiEscapeConfirmed;

    @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "PENDING_ACCEPT")
    private String status;

    @Schema(description = "关联支付订单 ID")
    private Long payOrderId;

    @Schema(description = "是否要求先缴纳大额订单保证金")
    private Boolean depositRequired;

    @Schema(description = "保证金金额，单位元")
    private BigDecimal depositAmount;

    @Schema(description = "保证金支付状态：NOT_REQUIRED 无需保证金、UNPAID 待支付、PAID 已支付")
    private String depositPayStatus;

    @Schema(description = "保证金支付单 ID")
    private Long depositPayOrderId;

    @Schema(description = "保证金支付完成时间")
    private LocalDateTime depositPaidTime;

    @Schema(description = "支付记录")
    private OrderPayRecordRespVO payRecord;

    @Schema(description = "价格明细")
    private List<OrderPriceItemRespVO> priceItems;

    @Schema(description = "附件列表。来源于下单时提交的 attachmentFileIds")
    private List<OrderAttachmentRespVO> attachments;

    @Schema(description = "订单单元列表。当前新订单默认只生成 1 个单元")
    private List<OrderUnitSimpleRespVO> units;

    @Schema(description = "抢单记录")
    private List<OrderAcceptRecordRespVO> acceptRecords;

    @Schema(description = "交付凭证")
    private List<OrderUnitProofRespVO> proofs;

    @Schema(description = "退款记录")
    private List<OrderRefundRespVO> refunds;

    @Schema(description = "价格明细展示开关。`true` 表示平台当前允许 App 直接渲染价格明细")
    private Boolean priceDetailEnabled;

    @Schema(description = "地方商城入口信息")
    private MallEntryRespVO mallEntry;

    @Schema(description = "商城消费关联信息")
    private MallConsumeRelationRespVO mallConsumeRelation;

    @Schema(description = "推广抵扣信息")
    private PromoteDeductRespVO promoteDeduct;

    @Schema(description = "投诉记录")
    private List<OrderComplaintRespVO> complaints;

    @Schema(description = "申诉记录")
    private List<OrderAppealRespVO> appeals;

    @Schema(description = "操作日志")
    private List<OrderOperateLogRespVO> operateLogs;

    @Schema(description = "统一进度时间线")
    private List<OrderTimelineRespVO> timeline;

    @Schema(description = "流单建议文案")
    private String flowAdvice;

    @Schema(description = "最近推送批次")
    private List<OrderMatchBatchRespVO> matchBatches;

    @Schema(description = "历史匹配摘要")
    private List<OrderMatchSummaryRespVO> matchSummaries;

    @Schema(description = "用户 App - 订单价格项 Response VO")
    @Data
    public static class OrderPriceItemRespVO {
        @Schema(description = "价格项类型")
        private String itemType;

        @Schema(description = "价格项名称")
        private String itemName;

        @Schema(description = "价格项金额")
        private BigDecimal itemAmount;

        @Schema(description = "排序号")
        private Integer sortNo;
    }

    @Schema(description = "用户 App - 订单附件 Response VO")
    @Data
    public static class OrderAttachmentRespVO {
        @Schema(description = "文件 ID")
        private Long fileId;

        @Schema(description = "文件类型，按附件类型字典展示，例如 IMAGE 图片、VIDEO 视频、DOCUMENT 文档")
        private String fileType;

        @Schema(description = "文件访问地址")
        private String fileUrl;

        @Schema(description = "排序号")
        private Integer sortNo;
    }

    @Schema(description = "用户 App - 单元简要 Response VO")
    @Data
    public static class OrderUnitSimpleRespVO {
        @Schema(description = "单元 ID")
        private Long id;

        @Schema(description = "单元订单号")
        private String unitNo;

        @Schema(description = "单元序号")
        private Integer unitSeq;

        @Schema(description = "单元标题")
        private String unitTitle;

        @Schema(description = "单元类型：DIRECT 直单、AUTO_SPLIT 自动拆分")
        private String unitType;

        @Schema(description = "单元金额")
        private BigDecimal unitAmount;

        @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "PENDING_ACCEPT")
        private String status;

        @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE, example = "BY_PROGRESS")
        private String splitMode;

        @Schema(description = "单元内容摘要")
        private String unitContent;

        @Schema(description = "单元进度描述，例如 1/3")
        private String unitProgress;

        @Schema(description = "单元服务人数")
        private Integer workerCount;

        @Schema(description = "单元金额上限快照，单位元")
        private BigDecimal maxAmountLimit;

        @Schema(description = "是否锁定")
        private Boolean isLocked;

        @Schema(description = "锁定原因")
        private String lockReason;

        @Schema(description = "派单状态")
        private String dispatchStatus;

        @Schema(description = "当前批次号")
        private Integer currentBatchNo;

        @Schema(description = "流单时间")
        private LocalDateTime flowTime;

        @Schema(description = "流单原因")
        private String flowReason;

        @Schema(description = "自动退款状态")
        private String autoRefundStatus;

        @Schema(description = "自动退款单 ID")
        private Long autoRefundId;

        @Schema(description = "申诉截止时间")
        private LocalDateTime appealExpireTime;

        @Schema(description = "当前是否仍在申诉期")
        private Boolean appealAvailable;

        @Schema(description = OpenApiSchemaConstants.ORDER_VERIFY_STATUS, example = "PENDING")
        private String verifyStatus;

        @Schema(description = "核销码")
        private String verifyCode;

        @Schema(description = "核销时间")
        private LocalDateTime verifyTime;

        @Schema(description = "核销人用户 ID")
        private Long verifyBy;

        @Schema(description = "核销备注")
        private String verifyRemark;
    }

    @Schema(description = "用户 App - 交付凭证 Response VO")
    @Data
    public static class OrderUnitProofRespVO {
        @Schema(description = "凭证 ID")
        private Long id;

        @Schema(description = "单元 ID")
        private Long unitId;

        @Schema(description = "服务商 ID")
        private Long merchantId;

        @Schema(description = "文件 ID")
        private Long fileId;

        @Schema(description = "文件访问地址")
        private String fileUrl;

        @Schema(description = "文件哈希")
        private String fileHash;

        @Schema(description = "凭证类型，按完工凭证字典展示，例如 BEFORE_SERVICE 服务前、AFTER_SERVICE 服务后")
        private String proofType;

        @Schema(description = "凭证说明")
        private String proofDesc;

        @Schema(description = "上传时间")
        private LocalDateTime proofTime;

        @Schema(description = "设备拍摄时间")
        private LocalDateTime deviceTime;

        @Schema(description = "上传时经度")
        private BigDecimal longitude;

        @Schema(description = "上传时纬度")
        private BigDecimal latitude;

        @Schema(description = "取证地址文本")
        private String addressText;
    }

    @Schema(description = "用户 App - 支付记录 Response VO")
    @Data
    public static class OrderPayRecordRespVO {
        @Schema(description = "支付订单 ID")
        private Long id;

        @Schema(description = "商户订单号")
        private String merchantOrderId;

        @Schema(description = "支付标题")
        private String subject;

        @Schema(description = "支付金额，单位分")
        private Integer price;

        @Schema(description = "支付状态数值，展示优先使用 statusName 或 payStatus 字段")
        private Integer status;

        @Schema(description = "支付渠道编码")
        private String channelCode;

        @Schema(description = "渠道订单号")
        private String channelOrderNo;

        @Schema(description = "累计退款金额，单位分")
        private Integer refundPrice;

        @Schema(description = "过期时间")
        private LocalDateTime expireTime;

        @Schema(description = "支付成功时间")
        private LocalDateTime successTime;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;

        @Schema(description = "申诉时效截止时间")
        private LocalDateTime appealExpireTime;
    }

    @Schema(description = "用户 App - 抢单记录 Response VO")
    @Data
    public static class OrderAcceptRecordRespVO {
        @Schema(description = "抢单记录 ID")
        private Long id;

        @Schema(description = "单元 ID")
        private Long unitId;

        @Schema(description = "服务商 ID")
        private Long merchantId;

        @Schema(description = "抢单时间")
        private LocalDateTime acceptTime;

        @Schema(description = "抢单时距离，单位公里")
        private BigDecimal distanceKm;

        @Schema(description = "抢单结果")
        private String acceptResult;

        @Schema(description = "备注")
        private String remark;
    }

    @Schema(description = "用户 App - 订单退款记录 Response VO")
    @Data
    public static class OrderRefundRespVO {
        @Schema(description = "退款记录 ID")
        private Long id;

        @Schema(description = "支付订单 ID")
        private Long payOrderId;

        @Schema(description = "商户退款单号")
        private String merchantRefundId;

        @Schema(description = "支付退款状态数值，展示优先使用 statusName")
        private Integer status;

        @Schema(description = "支付退款状态名称")
        private String statusName;

        @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
        private String auditStatus;

        @Schema(description = "审核备注")
        private String auditRemark;

        @Schema(description = "驳回原因")
        private String rejectReason;

        @Schema(description = "退款金额，单位分")
        private Integer refundPrice;

        @Schema(description = "退款原因")
        private String reason;

        @Schema(description = "渠道失败原因")
        private String channelErrorMsg;

        @Schema(description = "退款成功时间")
        private LocalDateTime successTime;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "用户 App - 地方商城入口 Response VO")
    @Data
    public static class MallEntryRespVO {
        @Schema(description = "是否展示入口。`true` 表示订单详情应展示商城入口")
        private Boolean enabled;

        @Schema(description = "入口标题")
        private String title;

        @Schema(description = "入口链接")
        private String url;
    }

    @Schema(description = "用户 App - 商城消费关联 Response VO")
    @Data
    public static class MallConsumeRelationRespVO {
        @Schema(description = "关联商城消费记录 ID")
        private Long consumeRecordId;

        @Schema(description = "关联商城消费记录编号")
        private String consumeRecordNo;

        @Schema(description = "关联商城消费金额，单位元")
        private BigDecimal consumeAmount;

        @Schema(description = "关联商城消费状态，例如 PENDING 待核销、SUCCESS 已完成、REFUNDED 已退款")
        private String consumeStatus;
    }

    @Schema(description = "用户 App - 推广抵扣 Response VO")
    @Data
    public static class PromoteDeductRespVO {
        @Schema(description = "推广抵扣金额，单位元")
        private BigDecimal deductAmount;

        @Schema(description = "推广抵扣来源类型，例如 PROMOTER 推广员、CAMPAIGN 活动、COUPON 推广券")
        private String sourceType;

        @Schema(description = "推广抵扣来源对象 ID")
        private Long sourceId;

        @Schema(description = "推广抵扣来源对象编号")
        private String sourceNo;

        @Schema(description = "抵扣后订单应付金额，单位元")
        private BigDecimal payableAmountAfterDeduct;
    }

    @Schema(description = "用户 App - 订单投诉记录 Response VO")
    @Data
    public static class OrderComplaintRespVO {
        @Schema(description = "投诉记录 ID")
        private Long id;

        @Schema(description = "投诉单号")
        private String complaintNo;

        @Schema(description = "关联单元 ID")
        private Long unitId;

        @Schema(description = "投诉人用户 ID")
        private Long complainantUserId;

        @Schema(description = "被投诉人用户 ID")
        private Long respondentUserId;

        @Schema(description = "投诉类型，按投诉业务类型字典展示")
        private String complaintType;

        @Schema(description = "投诉内容")
        private String content;

        @Schema(description = "投诉状态：PENDING 待受理、PROCESSING 处理中、FINISHED 已完结、REJECTED 已驳回")
        private String status;

        @Schema(description = "处理结果说明")
        private String resultDesc;

        @Schema(description = "处理时间")
        private LocalDateTime handleTime;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "用户 App - 订单申诉记录 Response VO")
    @Data
    public static class OrderAppealRespVO {
        @Schema(description = "申诉记录 ID")
        private Long id;

        @Schema(description = "申诉单号")
        private String appealNo;

        @Schema(description = "关联单元 ID")
        private Long unitId;

        @Schema(description = "申诉人用户 ID")
        private Long userId;

        @Schema(description = "申诉类型，按申诉业务类型字典展示")
        private String appealType;

        @Schema(description = "申诉内容")
        private String content;

        @Schema(description = "申诉状态：PENDING 待审核、PROCESSING 处理中、APPROVED 通过、REJECTED 驳回、FINISHED 已完结")
        private String status;

        @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
        private String auditStatus;

        @Schema(description = "审核备注")
        private String auditRemark;

        @Schema(description = "驳回原因")
        private String rejectReason;

        @Schema(description = "审核时间")
        private LocalDateTime auditTime;

        @Schema(description = "申诉时效截止时间")
        private LocalDateTime appealExpireTime;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "用户 App - 订单操作日志 Response VO")
    @Data
    public static class OrderOperateLogRespVO {
        @Schema(description = "日志 ID")
        private Long id;

        @Schema(description = "关联单元 ID")
        private Long unitId;

        @Schema(description = "操作类型，按订单操作日志字典展示，例如 CREATE 创建、LOCK 锁单、CONFIRM 验收")
        private String operateType;

        @Schema(description = "操作角色，按日志角色字典展示，例如 USER 用户、MERCHANT 服务商、ADMIN 管理员")
        private String operateRole;

        @Schema(description = "操作人 ID")
        private Long operateBy;

        @Schema(description = "操作前状态，记录本次动作前的订单或单元状态")
        private String beforeStatus;

        @Schema(description = "操作后状态，记录本次动作后的订单或单元状态")
        private String afterStatus;

        @Schema(description = "备注")
        private String remark;

        @Schema(description = "操作时间")
        private LocalDateTime operateTime;
    }

    @Schema(description = "用户 App - 推送批次摘要 Response VO")
    @Data
    public static class OrderMatchBatchRespVO {
        @Schema(description = "阶段号")
        private Integer stageNo;

        @Schema(description = "批次号")
        private Integer pushBatchNo;

        @Schema(description = "半径起始公里")
        private BigDecimal radiusStartKm;

        @Schema(description = "半径结束公里")
        private BigDecimal radiusEndKm;

        @Schema(description = "计划推送时间")
        private LocalDateTime plannedAt;

        @Schema(description = "过期时间")
        private LocalDateTime expiredAt;

        @Schema(description = "状态")
        private String status;
    }

    @Schema(description = "用户 App - 匹配摘要 Response VO")
    @Data
    public static class OrderMatchSummaryRespVO {
        @Schema(description = "服务商 ID")
        private Long merchantId;

        @Schema(description = "阶段号")
        private Integer stageNo;

        @Schema(description = "优先层")
        private String priorityLayer;

        @Schema(description = "是否优先池")
        private Boolean priorityPoolFlag;

        @Schema(description = "品类命中等级")
        private String categoryMatchLevel;

        @Schema(description = "距离公里")
        private BigDecimal distanceKm;

        @Schema(description = "推送时间")
        private LocalDateTime pushTime;

        @Schema(description = "最终结果")
        private String finalResult;
    }

    @Schema(description = "用户 App - 订单时间线 Response VO")
    @Data
    public static class OrderTimelineRespVO {
        @Schema(description = "时间线类型，例如 ORDER、UNIT、PAY、REFUND、COMPLAINT、APPEAL、VERIFY、LOG")
        private String timelineType;

        @Schema(description = "关联主键 ID")
        private Long bizId;

        @Schema(description = "关联单元 ID")
        private Long unitId;

        @Schema(description = "标题")
        private String title;

        @Schema(description = "内容摘要")
        private String content;

        @Schema(description = "状态或结果值")
        private String status;

        @Schema(description = "发生时间")
        private LocalDateTime eventTime;
    }
}
