package cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 黑名单详情 Response VO")
@Data
public class BlacklistDetailRespVO {

    @Schema(description = "黑名单 ID", example = "1")
    private Long id;
    @Schema(description = "用户 ID", example = "5001")
    private Long userId;
    @Schema(description = OpenApiSchemaConstants.BLACKLIST_TYPE, example = "RISK")
    private String blackType;
    @Schema(description = "拉黑原因", example = "多次触发高风险规则")
    private String reason;
    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;
    @Schema(description = "生效结束时间；为空表示长期有效")
    private LocalDateTime endTime;
    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "用户摘要")
    private MemberUserSimpleRespVO user;
    @Schema(description = "近期关联的风险事件")
    private List<RiskEventSimpleRespVO> recentRiskEvents;

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
        @Schema(description = "最近登录时间")
        private LocalDateTime lastLoginTime;
        @Schema(description = "最近登录 IP", example = "127.0.0.1")
        private String lastLoginIp;
    }

    @Data
    public static class RiskEventSimpleRespVO {
        @Schema(description = "风险事件 ID", example = "1")
        private Long id;
        @Schema(description = OpenApiSchemaConstants.CREDIT_BIZ_TYPE, example = "ORDER")
        private String bizType;
        @Schema(description = "业务 ID", example = "2001")
        private Long bizId;
        @Schema(description = "风险类型，按平台风控事件类型字典展示", example = "ORDER_LIMIT")
        private String riskType;
        @Schema(description = OpenApiSchemaConstants.RISK_LEVEL, example = "HIGH")
        private String riskLevel;
        @Schema(description = "命中规则编码", example = "ORDER_DAILY_CREATE_LIMIT")
        private String hitRuleCode;
        @Schema(description = OpenApiSchemaConstants.RISK_EVENT_STATUS, example = "PENDING")
        private String status;
        @Schema(description = "处理人 ID", example = "1")
        private Long handleBy;
        @Schema(description = "处理时间")
        private LocalDateTime handleTime;
        @Schema(description = "处理备注")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

}
