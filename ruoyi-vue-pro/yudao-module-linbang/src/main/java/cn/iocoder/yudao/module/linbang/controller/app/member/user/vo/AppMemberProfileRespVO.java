package cn.iocoder.yudao.module.linbang.controller.app.member.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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

    @Schema(description = "当前角色名称", example = "普通用户")
    private String currentRoleName;

    @Schema(description = "已开通角色编码列表")
    private List<String> enabledRoleCodes;

    @Schema(description = "角色摘要列表")
    private List<RoleSummaryItem> roleSummaries;

    @Schema(description = "实名认证状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "APPROVED")
    private String realNameStatus;

    @Schema(description = "信用分", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer creditScore;

    @Schema(description = "信用等级，按信用等级业务字典展示，例如 WARNING、NORMAL、EXCELLENT、DISABLED", requiredMode = Schema.RequiredMode.REQUIRED, example = "NORMAL")
    private String creditLevel;

    @Schema(description = "下一等级编码，为空表示已是最高等级", example = "EXCELLENT")
    private String nextLevelCode;

    @Schema(description = "到下一等级还需补足的分值", example = "20")
    private Integer nextLevelNeedScore;

    @Schema(description = "综合评分，按金额加权平均，保留两位小数", example = "4.67")
    private BigDecimal compositeScore;

    @Schema(description = "好评率，百分比保留两位小数", example = "90.00")
    private BigDecimal positiveRate;

    @Schema(description = "是否在好评优先池", example = "true")
    private Boolean inPositivePriorityPool;

    @Schema(description = "信用等级权益")
    private List<CreditBenefitItem> benefits;

    @Data
    public static class RoleSummaryItem {

        @Schema(description = "角色编码", example = "PROMOTER")
        private String roleCode;

        @Schema(description = "角色名称", example = "推广员")
        private String roleName;

        @Schema(description = "角色状态，ENABLED 已开通、PENDING 审核中、REJECTED 已驳回、AVAILABLE 可申请", example = "ENABLED")
        private String roleStatus;

        @Schema(description = "是否当前生效角色", example = "true")
        private Boolean current;

        @Schema(description = "是否可切换", example = "true")
        private Boolean switchable;

        @Schema(description = "权限说明摘要")
        private String permissionDesc;
    }

    @Data
    public static class CreditBenefitItem {

        @Schema(description = "等级编码", example = "NORMAL")
        private String levelCode;

        @Schema(description = "等级名称", example = "正常")
        private String levelName;

        @Schema(description = "权益标题", example = "常规排序")
        private String benefitTitle;

        @Schema(description = "权益说明", example = "可参与平台标准排序")
        private String benefitDesc;
    }
}
