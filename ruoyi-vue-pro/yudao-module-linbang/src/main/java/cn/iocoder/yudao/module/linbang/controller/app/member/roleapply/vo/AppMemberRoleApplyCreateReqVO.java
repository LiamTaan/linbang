package cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description = "用户 App - 提交身份申请 Request VO")
@Data
public class AppMemberRoleApplyCreateReqVO {

    @Schema(description = OpenApiSchemaConstants.ROLE_APPLY_ROLE_CODE, requiredMode = Schema.RequiredMode.REQUIRED, example = "PROMOTER")
    @NotBlank(message = "申请角色不能为空")
    private String applyRoleCode;

    @Schema(description = "申请说明", example = "我有本地社群推广资源")
    @Size(max = 255, message = "申请说明长度不能超过 255 个字符")
    private String applyReason;

    @Schema(description = "资源说明", example = "覆盖 3 个社区群和线下地推点位")
    @Size(max = 500, message = "资源说明长度不能超过 500 个字符")
    private String resourceDesc;

    @Schema(description = "预期转化说明，推广员申请必填，其他角色可为空", example = "预计首月完成 20 个线索转化")
    @Size(max = 500, message = "预期转化说明长度不能超过 500 个字符")
    private String expectedConversionDesc;

    @Schema(description = "能力说明，平台管理员申请必填，其他角色可为空", example = "有本地生活平台管理和活动策划经验")
    @Size(max = 500, message = "能力说明长度不能超过 500 个字符")
    private String abilityDesc;

    @Schema(description = "可投入时间说明，平台管理员申请必填，其他角色可为空", example = "每周可投入 5 天，每天 6 小时")
    @Size(max = 255, message = "可投入时间说明长度不能超过 255 个字符")
    private String availableTimeDesc;
}
