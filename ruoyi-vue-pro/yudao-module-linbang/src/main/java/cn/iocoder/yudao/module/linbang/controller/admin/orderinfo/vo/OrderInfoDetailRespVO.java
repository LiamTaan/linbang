package cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 订单详情 Response VO")
@Data
public class OrderInfoDetailRespVO {

    @Schema(description = "订单 ID", example = "1")
    private Long id;
    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "下单用户 ID", example = "1001")
    private Long userId;
    @Schema(description = "当前服务商 ID", example = "2001")
    private Long merchantId;
    @Schema(description = "服务商摘要")
    private MerchantRespVO merchant;
    @Schema(description = "类目 ID", example = "10")
    private Long categoryId;
    @Schema(description = "类目名称")
    private String categoryName;
    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
    private String pricingMode;
    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;
    @Schema(description = "订单金额")
    private BigDecimal orderAmount;
    @Schema(description = "数量")
    private BigDecimal quantity;
    @Schema(description = "服务人数")
    private Integer workerCount;
    @Schema(description = "服务时长/工期说明，例如 1小时、半天、今天内")
    private String serviceDurationDesc;
    @Schema(description = "需求说明")
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
    @Schema(description = "是否需要发票")
    private Boolean needInvoice;
    @Schema(description = "是否需要拆单")
    private Boolean needSplit;
    @Schema(description = "命中拆单规则 ID")
    private Long splitRuleId;
    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_STATUS, example = "SPLIT")
    private String splitStatus;
    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE, example = "BY_PROGRESS")
    private String splitMode;
    @Schema(description = "协议是否已确认")
    private Boolean agreementConfirmed;
    @Schema(description = "交易保障协议版本")
    private String tradeAgreementVersion;
    @Schema(description = "交易保障协议确认时间")
    private LocalDateTime tradeAgreementConfirmedTime;
    @Schema(description = "是否确认防逃单提醒")
    private Boolean antiEscapeConfirmed;
    @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "PENDING_PAY")
    private String status;
    @Schema(description = "支付订单 ID", example = "5001")
    private Long payOrderId;
    @Schema(description = "支付记录摘要")
    private OrderPayRecordRespVO payRecord;
    @Schema(description = "价格明细")
    private List<OrderPriceItemRespVO> priceItems;
    @Schema(description = "附件列表")
    private List<OrderAttachmentRespVO> attachments;
    @Schema(description = "拆分单元列表")
    private List<OrderUnitSimpleRespVO> units;
    @Schema(description = "接单记录")
    private List<OrderAcceptRecordRespVO> acceptRecords;
    @Schema(description = "完工凭证")
    private List<OrderUnitProofRespVO> proofs;
    @Schema(description = "退款记录")
    private List<OrderRefundRespVO> refunds;
    @Schema(description = "价格明细展示开关。`true` 表示平台当前允许 App 端直接展示价格明细")
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
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "服务商摘要")
    @Data
    public static class MerchantRespVO {
        @Schema(description = "服务商 ID", example = "2001")
        private Long id;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "联系人")
        private String contactName;
        @Schema(description = "联系电话")
        private String contactMobile;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
        private String acceptStatus;
        @Schema(description = "信用分")
        private Integer creditScore;
        @Schema(description = "信用等级")
        private String creditLevel;
    }

    @Data
    public static class OrderPriceItemRespVO {
        @Schema(description = "价格项类型，按价格明细字典展示，例如 BASE 基础服务费、MATERIAL 材料费、EXTRA 附加费")
        private String itemType;
        @Schema(description = "价格项名称")
        private String itemName;
        @Schema(description = "价格项金额")
        private BigDecimal itemAmount;
        @Schema(description = "排序号")
        private Integer sortNo;
    }

    @Data
    public static class OrderAttachmentRespVO {
        @Schema(description = "文件 ID", example = "6001")
        private Long fileId;
        @Schema(description = "文件类型，按附件类型字典展示，例如 IMAGE 图片、VIDEO 视频、DOCUMENT 文档")
        private String fileType;
        @Schema(description = "文件访问地址")
        private String fileUrl;
        @Schema(description = "排序号")
        private Integer sortNo;
    }

    @Schema(description = "单元摘要")
    @Data
    public static class OrderUnitSimpleRespVO {
        @Schema(description = "单元 ID", example = "1")
        private Long id;
        @Schema(description = "单元号")
        private String unitNo;
        @Schema(description = "单元序号", example = "1")
        private Integer unitSeq;
        @Schema(description = "单元标题")
        private String unitTitle;
        @Schema(description = "单元金额")
        private BigDecimal unitAmount;
        @Schema(description = "单元类型：DIRECT 直单、AUTO_SPLIT 自动拆分")
        private String unitType;
        @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE, example = "DIRECT")
        private String splitMode;
        @Schema(description = "单元内容摘要")
        private String unitContent;
        @Schema(description = "单元进度描述，例如 1/3")
        private String unitProgress;
        @Schema(description = "单元服务人数")
        private Integer workerCount;
        @Schema(description = "单元金额上限快照")
        private BigDecimal maxAmountLimit;
        @Schema(description = "前置单元 ID", example = "0")
        private Long prevUnitId;
        @Schema(description = "是否锁定")
        private Boolean isLocked;
        @Schema(description = "锁定原因")
        private String lockReason;
        @Schema(description = "当前服务商 ID", example = "2001")
        private Long merchantId;
        @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "PENDING_ACCEPT")
        private String status;
        @Schema(description = "接单截止时间")
        private LocalDateTime acceptDeadlineTime;
        @Schema(description = "完成时间")
        private LocalDateTime finishTime;
        @Schema(description = "申诉截止时间")
        private LocalDateTime appealExpireTime;
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
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderUnitProofRespVO {
        private Long id;
        private Long unitId;
        private Long merchantId;
        private Long fileId;
        private String fileUrl;
        private String fileHash;
        private String proofType;
        private String proofDesc;
        private LocalDateTime proofTime;
        private LocalDateTime deviceTime;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private String addressText;
    }

    @Data
    public static class OrderPayRecordRespVO {
        private Long id;
        private String merchantOrderId;
        private String subject;
        private Integer price;
        private Integer status;
        private String channelCode;
        private String channelOrderNo;
        private Integer refundPrice;
        private LocalDateTime expireTime;
        private LocalDateTime successTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderAcceptRecordRespVO {
        private Long id;
        private Long unitId;
        private Long merchantId;
        private String merchantName;
        private String merchantContactName;
        private String merchantContactMobile;
        private LocalDateTime acceptTime;
        private BigDecimal distanceKm;
        private String acceptResult;
        private String remark;
    }

    @Schema(description = "退款摘要")
    @Data
    public static class OrderRefundRespVO {
        @Schema(description = "退款 ID", example = "1")
        private Long id;
        @Schema(description = "支付订单 ID", example = "5001")
        private Long payOrderId;
        @Schema(description = "商户退款单号")
        private String merchantRefundId;
        @Schema(description = "退款状态码")
        private Integer status;
        @Schema(description = "退款状态名称")
        private String statusName;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
        private String auditStatus;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "驳回原因")
        private String rejectReason;
        @Schema(description = "退款金额，单位分")
        private Integer refundPrice;
        @Schema(description = "退款原因")
        private String reason;
        @Schema(description = "渠道错误信息")
        private String channelErrorMsg;
        @Schema(description = "退款成功时间")
        private LocalDateTime successTime;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "地方商城入口")
    @Data
    public static class MallEntryRespVO {
        @Schema(description = "是否展示入口")
        private Boolean enabled;
        @Schema(description = "入口标题")
        private String title;
        @Schema(description = "入口链接")
        private String url;
    }

    @Schema(description = "商城消费关联")
    @Data
    public static class MallConsumeRelationRespVO {
        @Schema(description = "关联商城消费记录 ID")
        private Long consumeRecordId;
        @Schema(description = "关联商城消费记录编号")
        private String consumeRecordNo;
        @Schema(description = "关联商城消费金额，单位元")
        private BigDecimal consumeAmount;
        @Schema(description = "关联商城消费状态")
        private String consumeStatus;
    }

    @Schema(description = "推广抵扣信息")
    @Data
    public static class PromoteDeductRespVO {
        @Schema(description = "推广抵扣金额，单位元")
        private BigDecimal deductAmount;
        @Schema(description = "推广抵扣来源类型")
        private String sourceType;
        @Schema(description = "推广抵扣来源对象 ID")
        private Long sourceId;
        @Schema(description = "推广抵扣来源对象编号")
        private String sourceNo;
        @Schema(description = "抵扣后订单应付金额，单位元")
        private BigDecimal payableAmountAfterDeduct;
    }

    @Schema(description = "投诉摘要")
    @Data
    public static class OrderComplaintRespVO {
        @Schema(description = "投诉 ID", example = "1")
        private Long id;
        @Schema(description = "投诉单号")
        private String complaintNo;
        @Schema(description = "单元 ID", example = "1")
        private Long unitId;
        @Schema(description = "投诉人用户 ID", example = "1001")
        private Long complainantUserId;
        @Schema(description = "被投诉人用户 ID", example = "1002")
        private Long respondentUserId;
        @Schema(description = "投诉类型，按投诉业务类型字典展示")
        private String complaintType;
        @Schema(description = "投诉内容")
        private String content;
        @Schema(description = OpenApiSchemaConstants.COMPLAINT_STATUS, example = "PROCESSING")
        private String status;
        @Schema(description = "处理结果")
        private String resultDesc;
        @Schema(description = "处理时间")
        private LocalDateTime handleTime;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "申诉摘要")
    @Data
    public static class OrderAppealRespVO {
        @Schema(description = "申诉 ID", example = "1")
        private Long id;
        @Schema(description = "申诉单号")
        private String appealNo;
        @Schema(description = "单元 ID", example = "1")
        private Long unitId;
        @Schema(description = "申诉人用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "申诉类型，按申诉业务类型字典展示")
        private String appealType;
        @Schema(description = "申诉内容")
        private String content;
        @Schema(description = OpenApiSchemaConstants.APPEAL_STATUS, example = "PROCESSING")
        private String status;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
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

    @Data
    public static class OrderOperateLogRespVO {
        @Schema(description = "日志 ID", example = "1")
        private Long id;
        @Schema(description = "关联单元 ID", example = "2001")
        private Long unitId;
        @Schema(description = "操作类型，按订单操作日志字典展示，例如 CREATE 创建、LOCK 锁单、CONFIRM 验收")
        private String operateType;
        @Schema(description = "操作角色，按日志角色字典展示，例如 USER 用户、MERCHANT 服务商、ADMIN 管理员")
        private String operateRole;
        @Schema(description = "操作人 ID", example = "1")
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

    @Schema(description = "订单时间线")
    @Data
    public static class OrderTimelineRespVO {
        @Schema(description = "时间线类型")
        private String timelineType;
        @Schema(description = "关联主键 ID")
        private Long bizId;
        @Schema(description = "关联单元 ID")
        private Long unitId;
        @Schema(description = "标题")
        private String title;
        @Schema(description = "内容摘要")
        private String content;
        @Schema(description = "状态或结果")
        private String status;
        @Schema(description = "发生时间")
        private LocalDateTime eventTime;
    }

}
