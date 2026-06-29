package cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 信用记录详情 Response VO")
@Data
public class CreditRecordDetailRespVO {

    @Schema(description = "信用记录 ID", example = "1")
    private Long id;
    @Schema(description = "用户 ID", example = "1001")
    private Long userId;
    @Schema(description = "服务商 ID", example = "2001")
    private Long merchantId;
    @Schema(description = "信用规则 ID", example = "3001")
    private Long ruleId;
    @Schema(description = "规则编码", example = "ORDER_FINISHED_POSITIVE")
    private String ruleCode;
    @Schema(description = "规则名称", example = "订单完结好评加分")
    private String ruleName;
    @Schema(description = "分值变动", example = "5")
    private Integer scoreChange;
    @Schema(description = "变动前分值", example = "100")
    private Integer beforeScore;
    @Schema(description = "变动后分值", example = "105")
    private Integer afterScore;
    @Schema(description = OpenApiSchemaConstants.CREDIT_TRIGGER_TYPE, example = "AUTO")
    private String triggerType;
    @Schema(description = OpenApiSchemaConstants.CREDIT_BIZ_TYPE, example = "ORDER")
    private String bizType;
    @Schema(description = "业务 ID，关联订单、单元、投诉、申诉或提现等业务主键", example = "1001")
    private Long bizId;
    @Schema(description = "业务对象展示文案，例如订单号、单元号、投诉单号", example = "LB202606260001")
    private String bizDisplay;
    @Schema(description = "备注，记录加减分原因说明")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "当前信用分", example = "110")
    private Integer currentScore;
    @Schema(description = "当前信用等级，例如 WARNING、NORMAL、GOOD、EXCELLENT", example = "GOOD")
    private String creditLevel;
    @Schema(description = "同一用户的信用记录数", example = "8")
    private Integer sameUserRecordCount;
    @Schema(description = "同一规则的命中记录数", example = "3")
    private Integer sameRuleRecordCount;
    @Schema(description = "正向记录数", example = "6")
    private Integer positiveRecordCount;
    @Schema(description = "负向记录数", example = "2")
    private Integer negativeRecordCount;
    @Schema(description = "用户摘要")
    private UserRespVO user;
    @Schema(description = "服务商摘要")
    private MerchantRespVO merchant;
    @Schema(description = "规则摘要")
    private RuleRespVO rule;

    @Data
    public static class UserRespVO {
        @Schema(description = "用户 ID", example = "1001")
        private Long id;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "手机号", example = "13800138000")
        private String mobile;
        @Schema(description = "昵称", example = "邻里用户")
        private String nickname;
        @Schema(description = "当前角色编码", example = "MERCHANT")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
    }

    @Data
    public static class MerchantRespVO {
        @Schema(description = "服务商 ID", example = "2001")
        private Long id;
        @Schema(description = "关联用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "联系人")
        private String contactName;
        @Schema(description = "联系电话")
        private String contactMobile;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
        private String acceptStatus;
        @Schema(description = "信用分", example = "110")
        private Integer creditScore;
        @Schema(description = "信用等级，例如 WARNING、NORMAL、GOOD、EXCELLENT", example = "GOOD")
        private String creditLevel;
    }

    @Data
    public static class RuleRespVO {
        @Schema(description = "规则 ID", example = "3001")
        private Long id;
        @Schema(description = "规则编码", example = "ORDER_FINISHED_POSITIVE")
        private String ruleCode;
        @Schema(description = "规则名称", example = "订单完结好评加分")
        private String ruleName;
        @Schema(description = "分值变动", example = "5")
        private Integer scoreChange;
        @Schema(description = OpenApiSchemaConstants.CREDIT_TRIGGER_TYPE, example = "AUTO")
        private String triggerType;
        @Schema(description = OpenApiSchemaConstants.CREDIT_RULE_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
