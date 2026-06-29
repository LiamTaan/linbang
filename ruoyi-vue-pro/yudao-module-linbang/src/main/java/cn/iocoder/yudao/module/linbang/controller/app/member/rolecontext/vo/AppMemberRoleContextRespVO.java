package cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 角色上下文 Response VO")
@Data
public class AppMemberRoleContextRespVO {

    @Schema(description = "当前生效角色编码", example = "USER")
    private String currentRoleCode;

    @Schema(description = "当前生效角色名称", example = "普通用户")
    private String currentRoleName;

    @Schema(description = "已开通角色编码")
    private List<String> enabledRoleCodes;

    @Schema(description = "可切换角色编码")
    private List<String> switchableRoleCodes;

    @Schema(description = "角色摘要列表")
    private List<RoleSummaryItem> roleSummaries;

    @Data
    public static class RoleSummaryItem {

        @Schema(description = "角色编码", example = "PARTNER")
        private String roleCode;

        @Schema(description = "角色名称", example = "区域合作商")
        private String roleName;

        @Schema(description = "角色状态，ENABLED 已开通、PENDING 审核中、REJECTED 已驳回、AVAILABLE 可申请", example = "ENABLED")
        private String roleStatus;

        @Schema(description = "是否当前角色", example = "false")
        private Boolean current;

        @Schema(description = "是否允许切换", example = "true")
        private Boolean switchable;

        @Schema(description = "权限说明")
        private String permissionDesc;
    }
}
