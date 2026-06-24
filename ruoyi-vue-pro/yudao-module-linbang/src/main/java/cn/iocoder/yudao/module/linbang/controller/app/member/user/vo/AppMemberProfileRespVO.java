package cn.iocoder.yudao.module.linbang.controller.app.member.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 个人资料 Response VO")
@Data
public class AppMemberProfileRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号串", requiredMode = Schema.RequiredMode.REQUIRED, example = "LBU123")
    private String userNo;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    private String mobile;

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "邻里用户8000")
    private String nickname;

    @Schema(description = "头像", example = "https://example.com/avatar.png")
    private String avatar;

    @Schema(description = "当前角色编码，可选值见业务规则，例如 USER 普通用户、MERCHANT 服务商、PROMOTER 推广员、PARTNER 区域合作商", requiredMode = Schema.RequiredMode.REQUIRED, example = "USER")
    private String currentRoleCode;

    @Schema(description = "实名认证状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "APPROVED")
    private String realNameStatus;

    @Schema(description = "信用分", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer creditScore;

    @Schema(description = "信用等级，按信用等级业务字典展示，例如 NORMAL、GOOD、EXCELLENT", requiredMode = Schema.RequiredMode.REQUIRED, example = "NORMAL")
    private String creditLevel;
}
