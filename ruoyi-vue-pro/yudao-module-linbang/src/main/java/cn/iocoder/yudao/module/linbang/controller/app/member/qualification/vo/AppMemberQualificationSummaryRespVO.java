package cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 认证与证件汇总 Response VO")
@Data
public class AppMemberQualificationSummaryRespVO {

    @Schema(description = "实名认证状态", example = "APPROVED")
    private String realNameAuditStatus;

    @Schema(description = "微信实名关联状态", example = "MATCHED")
    private String wechatRealNameStatus;

    @Schema(description = "支付宝实名关联状态", example = "UNBOUND")
    private String alipayRealNameStatus;

    @Schema(description = "营业执照审核状态", example = "APPROVED")
    private String businessLicenseAuditStatus;

    @Schema(description = "行业资质审核状态", example = "PENDING")
    private String industryQualificationAuditStatus;

    @Schema(description = "保险保单审核状态", example = "REJECTED")
    private String insuranceAuditStatus;

    @Schema(description = "即将到期资质数量", example = "1")
    private Integer expiringQualificationCount;

    @Schema(description = "身份证是否即将到期", example = "true")
    private Boolean idCardExpiringSoon;

    @Schema(description = "营业执照是否即将到期", example = "false")
    private Boolean businessLicenseExpiringSoon;

    @Schema(description = "行业资质是否即将到期", example = "true")
    private Boolean industryQualificationExpiringSoon;

    @Schema(description = "保险保单是否即将到期", example = "false")
    private Boolean insuranceExpiringSoon;

    @Schema(description = "身份证驳回原因摘要")
    private String realNameRejectReason;

    @Schema(description = "营业执照驳回原因摘要")
    private String businessLicenseRejectReason;

    @Schema(description = "行业资质驳回原因摘要")
    private String industryQualificationRejectReason;

    @Schema(description = "保险保单驳回原因摘要")
    private String insuranceRejectReason;

    @Schema(description = "是否可重新提交实名认证", example = "true")
    private Boolean realNameCanResubmit;

    @Schema(description = "是否可重新提交营业执照", example = "false")
    private Boolean businessLicenseCanResubmit;

    @Schema(description = "是否可重新提交行业资质", example = "true")
    private Boolean industryQualificationCanResubmit;

    @Schema(description = "是否可重新提交保险保单", example = "true")
    private Boolean insuranceCanResubmit;

    @Schema(description = "是否存在有效豁免", example = "false")
    private Boolean exemptionActive;

    @Schema(description = "证件提醒")
    private List<QualificationReminderRespVO> reminders;

    @Data
    public static class QualificationReminderRespVO {
        @Schema(description = "资质 ID", example = "1")
        private Long qualificationId;
        @Schema(description = "资质名称")
        private String qualificationName;
        @Schema(description = "提醒标题")
        private String reminderTitle;
        @Schema(description = "提醒内容")
        private String reminderContent;
        @Schema(description = "提醒状态", example = "UNREAD")
        private String readStatus;
        @Schema(description = "消息记录 ID", example = "100")
        private Long messageRecordId;
    }
}
