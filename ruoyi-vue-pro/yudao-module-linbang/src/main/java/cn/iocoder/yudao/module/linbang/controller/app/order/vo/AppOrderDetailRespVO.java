package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

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

    @Schema(description = "订单标题")
    private String title;

    @Schema(description = "计价方式")
    private String pricingMode;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "订单应付金额")
    private BigDecimal orderAmount;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "服务时长说明")
    private String serviceDurationDesc;

    @Schema(description = "用户需求说明")
    private String requireDesc;

    @Schema(description = "地址 ID")
    private Long addressId;

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

    @Schema(description = "拆单状态：UNSPLIT 不拆单、SPLIT 已拆单")
    private String splitStatus;

    @Schema(description = "是否已确认服务协议")
    private Boolean agreementConfirmed;

    @Schema(description = "主订单状态：PENDING_PAY 待支付、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、PENDING_CONFIRM 待确认、AFTER_SALE 售后中、FINISHED 已完成、REFUNDED 已退款、CLOSED 已关闭")
    private String status;

    @Schema(description = "关联支付订单 ID")
    private Long payOrderId;

    @Schema(description = "支付记录")
    private OrderPayRecordRespVO payRecord;

    @Schema(description = "价格明细")
    private List<OrderPriceItemRespVO> priceItems;

    @Schema(description = "附件列表")
    private List<OrderAttachmentRespVO> attachments;

    @Schema(description = "拆分单元列表")
    private List<OrderUnitSimpleRespVO> units;

    @Schema(description = "抢单记录")
    private List<OrderAcceptRecordRespVO> acceptRecords;

    @Schema(description = "交付凭证")
    private List<OrderUnitProofRespVO> proofs;

    @Schema(description = "退款记录")
    private List<OrderRefundRespVO> refunds;

    @Schema(description = "投诉记录")
    private List<OrderComplaintRespVO> complaints;

    @Schema(description = "申诉记录")
    private List<OrderAppealRespVO> appeals;

    @Schema(description = "操作日志")
    private List<OrderOperateLogRespVO> operateLogs;

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

        @Schema(description = "文件类型")
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

        @Schema(description = "单元金额")
        private BigDecimal unitAmount;

        @Schema(description = "单元状态：PENDING_CREATE 待生成、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、PENDING_CONFIRM 待验收、FINISHED 已完成、APPEALING 申诉中、REFUNDED 已退款、CLOSED 已关闭")
        private String status;

        @Schema(description = "是否锁定")
        private Boolean isLocked;
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

        @Schema(description = "凭证类型")
        private String proofType;

        @Schema(description = "凭证说明")
        private String proofDesc;

        @Schema(description = "上传时间")
        private LocalDateTime proofTime;

        @Schema(description = "上传时经度")
        private BigDecimal longitude;

        @Schema(description = "上传时纬度")
        private BigDecimal latitude;
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

        @Schema(description = "投诉类型")
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

        @Schema(description = "申诉类型")
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

        @Schema(description = "操作类型")
        private String operateType;

        @Schema(description = "操作角色")
        private String operateRole;

        @Schema(description = "操作人 ID")
        private Long operateBy;

        @Schema(description = "操作前状态")
        private String beforeStatus;

        @Schema(description = "操作后状态")
        private String afterStatus;

        @Schema(description = "备注")
        private String remark;

        @Schema(description = "操作时间")
        private LocalDateTime operateTime;
    }
}
