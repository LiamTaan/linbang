package cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 拆分单元详情 Response VO")
@Data
public class OrderUnitDetailRespVO {

    @Schema(description = "单元 ID", example = "1")
    private Long id;
    @Schema(description = "主订单 ID", example = "1001")
    private Long orderId;
    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "订单标题")
    private String orderTitle;
    @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "SERVING")
    private String orderStatus;
    @Schema(description = "下单用户 ID", example = "1001")
    private Long userId;
    @Schema(description = "下单用户编号")
    private String userNo;
    @Schema(description = "下单用户昵称")
    private String userNickname;
    @Schema(description = "下单用户手机号")
    private String userMobile;
    @Schema(description = "当前服务商 ID", example = "2001")
    private Long merchantId;
    @Schema(description = "服务商名称")
    private String merchantName;
    @Schema(description = "服务商联系人")
    private String merchantContactName;
    @Schema(description = "服务商联系电话")
    private String merchantContactMobile;
    @Schema(description = "单元号")
    private String unitNo;
    @Schema(description = "单元序号", example = "1")
    private Integer unitSeq;
    @Schema(description = "单元标题")
    private String unitTitle;
    @Schema(description = "单元类型：DIRECT 直单、AUTO_SPLIT 自动拆分")
    private String unitType;
    @Schema(description = "单元金额")
    private BigDecimal unitAmount;
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
    @Schema(description = "前置单元号")
    private String prevUnitNo;
    @Schema(description = "是否锁定")
    private Boolean isLocked;
    @Schema(description = "锁定原因")
    private String lockReason;
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
    @Schema(description = "完工凭证")
    private List<OrderUnitProofRespVO> proofs;
    @Schema(description = "接单记录")
    private List<OrderAcceptRecordRespVO> acceptRecords;
    @Schema(description = "投诉记录")
    private List<OrderComplaintRespVO> complaints;
    @Schema(description = "申诉记录")
    private List<OrderAppealRespVO> appeals;
    @Schema(description = "操作日志")
    private List<OrderOperateLogRespVO> operateLogs;
    @Schema(description = "统一进度时间线")
    private List<OrderTimelineRespVO> timeline;

    @Data
    public static class OrderUnitProofRespVO {
        @Schema(description = "凭证 ID", example = "1")
        private Long id;
        @Schema(description = "文件 ID", example = "6001")
        private Long fileId;
        @Schema(description = "文件访问地址")
        private String fileUrl;
        @Schema(description = "文件哈希")
        private String fileHash;
        @Schema(description = "服务商 ID", example = "2001")
        private Long merchantId;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "服务商联系人")
        private String merchantContactName;
        @Schema(description = "服务商联系电话")
        private String merchantContactMobile;
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

    @Data
    public static class OrderAcceptRecordRespVO {
        @Schema(description = "接单记录 ID", example = "1")
        private Long id;
        @Schema(description = "服务商 ID", example = "2001")
        private Long merchantId;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "服务商联系人")
        private String merchantContactName;
        @Schema(description = "服务商联系电话")
        private String merchantContactMobile;
        @Schema(description = "接单时间")
        private LocalDateTime acceptTime;
        @Schema(description = "接单时与服务地址距离，单位公里")
        private BigDecimal distanceKm;
        @Schema(description = "接单结果，按接单结果字典展示，例如 ACCEPTED 抢单成功、REJECTED 未命中")
        private String acceptResult;
        @Schema(description = "备注")
        private String remark;
    }

    @Schema(description = "投诉摘要")
    @Data
    public static class OrderComplaintRespVO {
        @Schema(description = "投诉 ID", example = "1")
        private Long id;
        @Schema(description = "投诉单号")
        private String complaintNo;
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
        @Schema(description = "操作类型，按订单操作日志字典展示，例如 LOCK 锁单、UNLOCK 解锁、CONFIRM 验收")
        private String operateType;
        @Schema(description = "操作角色，按日志角色字典展示，例如 USER 用户、MERCHANT 服务商、ADMIN 管理员")
        private String operateRole;
        @Schema(description = "操作人 ID", example = "1")
        private Long operateBy;
        @Schema(description = "操作前状态，记录本次动作前的单元状态")
        private String beforeStatus;
        @Schema(description = "操作后状态，记录本次动作后的单元状态")
        private String afterStatus;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "操作时间")
        private LocalDateTime operateTime;
    }

    @Schema(description = "订单单元时间线")
    @Data
    public static class OrderTimelineRespVO {
        @Schema(description = "时间线类型")
        private String timelineType;
        @Schema(description = "关联主键 ID")
        private Long bizId;
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
