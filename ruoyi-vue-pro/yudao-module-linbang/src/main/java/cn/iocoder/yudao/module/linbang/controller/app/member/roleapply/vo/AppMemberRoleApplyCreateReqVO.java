package cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description = "用户 App - 提交身份申请 Request VO")
@Data
public class AppMemberRoleApplyCreateReqVO {

    @Schema(description = "申请角色编码，仅支持 PROMOTER 推广员、PARTNER 区域合作商", requiredMode = Schema.RequiredMode.REQUIRED, example = "PROMOTER")
    @NotBlank(message = "申请角色不能为空")
    private String applyRoleCode;

    @Schema(description = "申请说明", example = "我有本地社群推广资源")
    @Size(max = 255, message = "申请说明长度不能超过 255 个字符")
    private String applyReason;

    @Schema(description = "资源说明", example = "覆盖 3 个社区群和线下地推点位")
    @Size(max = 500, message = "资源说明长度不能超过 500 个字符")
    private String resourceDesc;
}
