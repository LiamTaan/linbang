package cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 身份申请 Response VO")
@Data
public class AppMemberRoleApplyRespVO {

    @Schema(description = "申请编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "申请角色编码", example = "PROMOTER")
    private String applyRoleCode;

    @Schema(description = "申请说明", example = "我有本地社群推广资源")
    private String applyReason;

    @Schema(description = "资源说明", example = "覆盖 3 个社区群和线下地推点位")
    private String resourceDesc;

    @Schema(description = "审核状态", example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注", example = "资料完整")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "资源说明不足")
    private String rejectReason;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
