package cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 身份申请详情 Response VO")
@Data
public class MemberRoleApplyDetailRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1001")
    private Long userId;

    @Schema(description = "申请角色编码", example = "PROMOTER")
    private String applyRoleCode;

    @Schema(description = "申请原因", example = "具备社区推广资源")
    private String applyReason;

    @Schema(description = "资源说明", example = "覆盖多个小区业主群和线下点位")
    private String resourceDesc;

    @Schema(description = "预期转化说明", example = "预计首月完成 20 个线索转化")
    private String expectedConversionDesc;

    @Schema(description = "能力说明", example = "有本地生活平台管理经验")
    private String abilityDesc;

    @Schema(description = "可投入时间说明", example = "每周可投入 5 天")
    private String availableTimeDesc;

    @Schema(description = OpenApiSchemaConstants.ROLE_APPLY_AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注", example = "资料完整")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "角色说明不足")
    private String rejectReason;

    @Schema(description = "审核人", example = "1")
    private Long auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "用户摘要")
    private UserSummary user;

    @Schema(description = "实名认证摘要")
    private RealNameSummary realName;

    @Schema(description = "最近一次资质摘要")
    private QualificationSummary latestQualification;

    @Schema(description = "推广员摘要")
    private PromoterSummary promoter;

    @Schema(description = "用户摘要")
    @Data
    public static class UserSummary {
        @Schema(description = "用户 ID", example = "1001")
        private Long id;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "用户昵称", example = "邻里用户")
        private String nickname;
        @Schema(description = "用户手机号", example = "13800138000")
        private String mobile;
        @Schema(description = "当前角色编码", example = "USER")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
    }

    @Schema(description = "实名认证摘要")
    @Data
    public static class RealNameSummary {
        @Schema(description = "实名认证 ID", example = "1")
        private Long id;
        @Schema(description = "真实姓名", example = "张三")
        private String realName;
        @Schema(description = "身份证号")
        private String idCardNo;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;
    }

    @Schema(description = "资质摘要")
    @Data
    public static class QualificationSummary {
        @Schema(description = "资质 ID", example = "1")
        private Long id;
        @Schema(description = "资质类型", example = "BUSINESS_LICENSE")
        private String qualificationType;
        @Schema(description = "资质名称", example = "营业执照")
        private String qualificationName;
        @Schema(description = "有效截止日期")
        private LocalDate validEndDate;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;
    }

    @Schema(description = "推广员摘要")
    @Data
    public static class PromoterSummary {
        @Schema(description = "推广员 ID", example = "1")
        private Long id;
        @Schema(description = "邀请码")
        private String inviteCode;
        @Schema(description = "等级编码", example = "LEVEL_1")
        private String levelCode;
        @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
        private String status;
    }
}
