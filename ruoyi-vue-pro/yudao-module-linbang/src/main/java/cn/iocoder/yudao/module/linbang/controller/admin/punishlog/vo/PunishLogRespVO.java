package cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 处罚执行日志 Response VO")
@Data
public class PunishLogRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "处罚日志 ID", example = "1")
    private Long id;

    @Schema(description = "处罚类型", example = "RESTRICT_PUBLISH")
    private String punishType;
    @Schema(description = "处罚记录 ID", example = "1001")
    private Long recordId;
    @Schema(description = "处罚用户 ID", example = "2001")
    private Long userId;
    @Schema(description = "用户编号", example = "LBU202406280001")
    private String userNo;
    @Schema(description = "用户昵称", example = "张三")
    private String userNickname;
    @Schema(description = "用户手机号", example = "13800138000")
    private String userMobile;
    @Schema(description = "处罚状态", example = "ACTIVE")
    private String status;
    @Schema(description = "处罚原因", example = "命中刷单风险规则，人工确认处罚")
    private String reason;
    @Schema(description = "来源业务类型", example = "ORDER")
    private String sourceBizType;
    @Schema(description = "来源业务 ID", example = "3001")
    private Long sourceBizId;
    @Schema(description = "处罚生效时间")
    private LocalDateTime startTime;
    @Schema(description = "处罚结束/释放时间")
    private LocalDateTime endTime;
    @Schema(description = "处罚记录创建时间")
    private LocalDateTime createTime;
}
