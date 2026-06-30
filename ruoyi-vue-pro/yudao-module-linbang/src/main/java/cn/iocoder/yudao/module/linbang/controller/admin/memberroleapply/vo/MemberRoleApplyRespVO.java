package cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 身份申请 Response VO")
@Data
public class MemberRoleApplyRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1001")
    private Long userId;

    @Schema(description = "用户编号", example = "LBU123456")
    private String userNo;

    @Schema(description = "用户昵称", example = "邻里用户")
    private String userNickname;

    @Schema(description = "用户手机号", example = "13800138000")
    private String userMobile;

    @Schema(description = "申请角色编码", example = "PROMOTER")
    private String applyRoleCode;

    @Schema(description = "申请原因", example = "具备社区推广资源")
    private String applyReason;

    @Schema(description = "资源说明", example = "覆盖多个业主群和线下点位")
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

    @Schema(description = "驳回原因", example = "资质材料不足")
    private String rejectReason;

    @Schema(description = "审核人", example = "1")
    private Long auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
