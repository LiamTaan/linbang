package cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 申诉详情 Response VO")
@Data
public class AppealDetailRespVO {

    @Schema(description = "申诉 ID", example = "1")
    private Long id;
    @Schema(description = "申诉单号")
    private String appealNo;
    @Schema(description = "订单 ID", example = "1001")
    private Long orderId;
    @Schema(description = "单元 ID", example = "2001")
    private Long unitId;
    @Schema(description = "申诉人用户 ID", example = "3001")
    private Long userId;
    @Schema(description = "申诉类型，由前端按业务字典展示")
    private String appealType;
    @Schema(description = "申诉内容")
    private String content;
    @Schema(description = OpenApiSchemaConstants.APPEAL_STATUS, example = "PENDING")
    private String status;
    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;
    @Schema(description = "审核人", example = "1")
    private Long auditBy;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    @Schema(description = "审核备注")
    private String auditRemark;
    @Schema(description = "驳回原因")
    private String rejectReason;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "关联订单")
    private OrderRespVO order;
    @Schema(description = "关联单元")
    private UnitRespVO unit;
    @Schema(description = "申诉人信息")
    private UserRespVO user;
    @Schema(description = "关联服务商信息")
    private MerchantRespVO merchant;
    @Schema(description = "统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "附件列表")
    private List<FileRespVO> files;
    @Schema(description = "同单或同用户关联申诉")
    private List<RelatedAppealRespVO> relatedAppeals;
    @Schema(description = "合作商协调记录")
    private List<CoordinationRespVO> coordinationRecords;
    @Schema(description = "操作日志")
    private List<OperateLogRespVO> operateLogs;

    @Schema(description = "订单摘要")
    @Data
    public static class OrderRespVO {
        @Schema(description = "订单 ID", example = "1001")
        private Long id;
        @Schema(description = "订单号")
        private String orderNo;
        @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "SERVING")
        private String status;
        @Schema(description = "下单用户 ID", example = "3001")
        private Long userId;
        @Schema(description = "下单用户编号")
        private String userNo;
        @Schema(description = "下单用户昵称")
        private String userNickname;
        @Schema(description = "下单用户手机号")
        private String userMobile;
        @Schema(description = "当前服务商 ID", example = "4001")
        private Long merchantId;
        @Schema(description = "订单金额")
        private BigDecimal orderAmount;
    }

    @Schema(description = "单元摘要")
    @Data
    public static class UnitRespVO {
        @Schema(description = "单元 ID", example = "2001")
        private Long id;
        @Schema(description = "主订单 ID", example = "1001")
        private Long orderId;
        @Schema(description = "单元号")
        private String unitNo;
        @Schema(description = "单元序号", example = "1")
        private Integer unitSeq;
        @Schema(description = "单元标题")
        private String unitTitle;
        @Schema(description = "单元金额")
        private BigDecimal unitAmount;
        @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "APPEALING")
        private String status;
        @Schema(description = "是否锁定")
        private Boolean isLocked;
        @Schema(description = "锁定原因")
        private String lockReason;
        @Schema(description = "当前服务商 ID", example = "4001")
        private Long merchantId;
        @Schema(description = "接单截止时间")
        private LocalDateTime acceptDeadlineTime;
        @Schema(description = "完成时间")
        private LocalDateTime finishTime;
    }

    @Schema(description = "用户摘要")
    @Data
    public static class UserRespVO {
        @Schema(description = "用户 ID", example = "3001")
        private Long id;
        @Schema(description = "用户编号")
        private String userNo;
        @Schema(description = "手机号")
        private String mobile;
        @Schema(description = "昵称")
        private String nickname;
        @Schema(description = "当前角色编码", example = "USER")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "最近登录时间")
        private LocalDateTime lastLoginTime;
    }

    @Schema(description = "服务商摘要")
    @Data
    public static class MerchantRespVO {
        @Schema(description = "服务商 ID", example = "4001")
        private Long id;
        @Schema(description = "关联用户 ID", example = "3002")
        private Long userId;
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
        @Schema(description = "服务范围说明")
        private String serviceScopeDesc;
    }

    @Schema(description = "统计摘要")
    @Data
    public static class SummaryRespVO {
        @Schema(description = "附件数量")
        private Integer attachmentCount;
        @Schema(description = "同订单申诉数")
        private Integer sameOrderAppealCount;
        @Schema(description = "同用户申诉数")
        private Integer sameUserAppealCount;
        @Schema(description = "待审核数量")
        private Integer pendingAuditCount;
        @Schema(description = "处理中数量")
        private Integer processingCount;
        @Schema(description = "通过数量")
        private Integer approvedCount;
        @Schema(description = "驳回数量")
        private Integer rejectedCount;
        @Schema(description = "已完结数量")
        private Integer finishedCount;
    }

    @Schema(description = "附件摘要")
    @Data
    public static class FileRespVO {
        @Schema(description = "文件 ID", example = "5001")
        private Long fileId;
    }

    @Schema(description = "关联申诉摘要")
    @Data
    public static class RelatedAppealRespVO {
        @Schema(description = "申诉 ID", example = "2")
        private Long id;
        @Schema(description = "申诉单号")
        private String appealNo;
        @Schema(description = "订单 ID", example = "1001")
        private Long orderId;
        @Schema(description = "单元 ID", example = "2001")
        private Long unitId;
        @Schema(description = "申诉类型，按申诉业务类型字典展示")
        private String appealType;
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
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "合作商协调记录")
    @Data
    public static class CoordinationRespVO {
        @Schema(description = "协调记录 ID", example = "1")
        private Long id;
        @Schema(description = "合作商 ID", example = "2001")
        private Long partnerId;
        @Schema(description = OpenApiSchemaConstants.PARTNER_COORDINATION_STATUS, example = "PROCESSING")
        private String status;
        @Schema(description = "协调意见")
        private String coordinationRemark;
        @Schema(description = "升级平台说明")
        private String escalateRemark;
        @Schema(description = "发起人 ID", example = "1")
        private Long initiatedBy;
        @Schema(description = "发起时间")
        private LocalDateTime initiatedTime;
        @Schema(description = "结束人 ID", example = "1")
        private Long finishedBy;
        @Schema(description = "结束时间")
        private LocalDateTime finishedTime;
    }

    @Schema(description = "操作日志")
    @Data
    public static class OperateLogRespVO {
        @Schema(description = "日志 ID", example = "1")
        private Long id;
        @Schema(description = "单元 ID", example = "2001")
        private Long unitId;
        @Schema(description = "操作类型，按订单/申诉操作日志字典展示，例如 SUBMIT 提交申诉、AUDIT 审核、FINISH 完结")
        private String operateType;
        @Schema(description = "操作角色，按日志角色字典展示，例如 USER 用户、MERCHANT 服务商、ADMIN 管理员")
        private String operateRole;
        @Schema(description = "操作人 ID", example = "1")
        private Long operateBy;
        @Schema(description = "操作前状态，记录本次操作发生前的申诉或关联单元状态")
        private String beforeStatus;
        @Schema(description = "操作后状态，记录本次操作完成后的申诉或关联单元状态")
        private String afterStatus;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "操作时间")
        private LocalDateTime operateTime;
    }
}
