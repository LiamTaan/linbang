package cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 风控规则详情 Response VO")
@Data
public class RiskRuleDetailRespVO {

    @Schema(description = "主键", example = "310001")
    private Long id;
    @Schema(description = "规则编码", example = "ORDER_DAILY_CREATE_LIMIT")
    private String ruleCode;
    @Schema(description = "规则名称", example = "用户单日发单次数上限")
    private String ruleName;
    @Schema(description = "规则分组", example = "ORDER")
    private String ruleGroup;
    @Schema(description = "规则值", example = "20")
    private String ruleValue;
    @Schema(description = OpenApiSchemaConstants.RISK_RULE_VALUE_TYPE, example = "INTEGER")
    private String valueType;
    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;
    @Schema(description = "备注", example = "超过阈值时转人工复核")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "命中次数")
    private Integer hitCount;
    @Schema(description = "待处理风险事件数")
    private Integer pendingEventCount;
    @Schema(description = "关联黑名单人数")
    private Integer userBlacklistCount;
    @Schema(description = "最近风险事件")
    private List<RiskEventSimpleRespVO> recentEvents;
    @Schema(description = "关联黑名单")
    private List<BlacklistSimpleRespVO> relatedBlacklists;

    @Data
    public static class RiskEventSimpleRespVO {
        @Schema(description = "风险事件 ID", example = "1")
        private Long id;
        @Schema(description = OpenApiSchemaConstants.CREDIT_BIZ_TYPE, example = "ORDER")
        private String bizType;
        @Schema(description = "业务 ID", example = "1001")
        private Long bizId;
        @Schema(description = "命中用户摘要")
        private MemberUserSimpleRespVO user;
        @Schema(description = "风险类型", example = "ORDER_LIMIT")
        private String riskType;
        @Schema(description = "风险等级", example = "HIGH")
        private String riskLevel;
        @Schema(description = "命中规则编码", example = "ORDER_DAILY_CREATE_LIMIT")
        private String hitRuleCode;
        @Schema(description = "风险事件状态：PENDING 待处理、PROCESSING 处理中、FINISHED 已完结", example = "PENDING")
        private String status;
        @Schema(description = "处理人 ID", example = "1")
        private Long handleBy;
        @Schema(description = "处理时间")
        private LocalDateTime handleTime;
        @Schema(description = "备注", example = "超过阈值时转人工复核")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class BlacklistSimpleRespVO {
        @Schema(description = "黑名单 ID", example = "1")
        private Long id;
        @Schema(description = "用户 ID", example = "5001")
        private Long userId;
        @Schema(description = "黑名单类型", example = "RISK")
        private String blackType;
        @Schema(description = "拉黑原因", example = "多次触发高风险规则")
        private String reason;
        @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "生效开始时间")
        private LocalDateTime startTime;
        @Schema(description = "生效结束时间")
        private LocalDateTime endTime;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
        @Schema(description = "用户摘要")
        private MemberUserSimpleRespVO user;
    }

    @Data
    public static class MemberUserSimpleRespVO {
        @Schema(description = "用户 ID", example = "5001")
        private Long id;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "手机号", example = "13800138000")
        private String mobile;
        @Schema(description = "昵称", example = "邻里用户")
        private String nickname;
        @Schema(description = "当前角色编码", example = "USER")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
    }
}
