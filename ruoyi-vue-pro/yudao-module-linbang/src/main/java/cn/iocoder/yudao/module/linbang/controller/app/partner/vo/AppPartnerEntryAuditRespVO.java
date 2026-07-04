package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 合作商入驻初审 Response VO")
@Data
public class AppPartnerEntryAuditRespVO {

    @Schema(description = "入驻申请 ID", example = "1")
    private Long id;

    @Schema(description = "服务商 ID", example = "1001")
    private Long merchantId;

    @Schema(description = "申请用户 ID", example = "2001")
    private Long userId;

    @Schema(description = "申请用户编号", example = "LBU202606280001")
    private String userNo;

    @Schema(description = "申请用户昵称", example = "张三")
    private String userNickname;

    @Schema(description = "申请用户手机号", example = "13800138000")
    private String userMobile;

    @Schema(description = "服务商名称", example = "南山保洁服务站")
    private String merchantName;

    @Schema(description = "服务商联系人", example = "李师傅")
    private String merchantContactName;

    @Schema(description = "服务商联系手机号", example = "13800138000")
    private String merchantContactMobile;

    @Schema(description = "服务范围说明", example = "覆盖南山区家电清洗、保洁、维修")
    private String serviceScopeDesc;

    @Schema(description = "入驻单号", example = "ME202606280001")
    private String entryNo;

    @Schema(description = "区域编码", example = "440305")
    private String regionCode;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
    private String firstAuditStatus;

    @Schema(description = "初审人 ID", example = "1")
    private Long firstAuditBy;

    @Schema(description = "初审时间")
    private LocalDateTime firstAuditTime;

    @Schema(description = "初审意见/备注")
    private String remark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, example = "PENDING")
    private String status;

    @Schema(description = "入驻进度状态", example = "PENDING_FIRST_AUDIT")
    private String progressStatus;

    @Schema(description = "当前阶段名称", example = "待合作商初审")
    private String currentStageName;

    @Schema(description = "当前阶段时间")
    private LocalDateTime currentStageTime;

    @Schema(description = "当前阻塞原因")
    private String onboardingBlockedReason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "申请人实名姓名", example = "张三")
    private String applicantRealName;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
    private String applicantRealNameAuditStatus;

    @Schema(description = "申请服务类目")
    private List<CategoryItem> categories;

    @Schema(description = "申请资质")
    private List<QualificationItem> qualifications;

    @Data
    public static class CategoryItem {
        @Schema(description = "类目 ID", example = "1")
        private Long categoryId;

        @Schema(description = "类目名称", example = "家电清洗")
        private String categoryName;
    }

    @Data
    public static class QualificationItem {
        @Schema(description = "资质 ID", example = "1")
        private Long id;

        @Schema(description = OpenApiSchemaConstants.QUALIFICATION_TYPE, example = "ELECTRICIAN")
        private String qualificationType;

        @Schema(description = "资质名称", example = "低压电工证")
        private String qualificationName;

        @Schema(description = "资质编号", example = "CERT-2026-001")
        private String qualificationNo;

        @Schema(description = "资质附件文件 ID", example = "11")
        private Long fileId;

        @Schema(description = "资质附件访问地址", example = "https://file.linbang.cn/qualification/11.jpg")
        private String fileUrl;

        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;

        @Schema(description = "有效截止日期")
        private LocalDate validEndDate;
    }
}
