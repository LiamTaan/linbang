package cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 角色上下文 Response VO")
@Data
public class AppMemberRoleContextRespVO {

    @Schema(description = "当前生效角色编码；App 端角色专属动作均以该字段作为唯一生效口径，必须先切换到对应角色后再执行对应业务。", example = "USER")
    private String currentRoleCode;

    @Schema(description = "当前生效角色名称", example = "普通用户")
    private String currentRoleName;

    @Schema(description = "已开通角色编码；仅表示当前账号已经开通并可切换，不代表当前页面可直接执行该角色动作。")
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

        @Schema(description = "进入该角色后的主视角说明，例如默认进入发单视角、抢单视角或辖区协同视角。")
        private String entryModeDesc;

        @Schema(description = "该角色的主能力摘要，供前端角色切换提示和页面入口说明使用。")
        private List<String> mainPermissions;

        @Schema(description = "执行该角色专属动作前是否必须先切换到该角色。", example = "true")
        private Boolean switchRequiredForActions;
    }
}
