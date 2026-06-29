package cn.iocoder.yudao.module.linbang.controller.admin.punishlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 处罚执行日志详情 Response VO")
@Data
public class PunishLogDetailRespVO extends PunishLogRespVO {

    @Schema(description = "来源记录类型", example = "USER_RESTRICT_RECORD")
    private String sourceRecordType;

    @Schema(description = "来源记录 ID", example = "1001")
    private Long sourceRecordId;

    @Schema(description = "执行操作人 ID", example = "1")
    private Long operatorId;

    @Schema(description = "执行时间")
    private LocalDateTime operateTime;

    @Schema(description = "解除操作人 ID", example = "1")
    private Long releaseOperatorId;

    @Schema(description = "解除时间")
    private LocalDateTime releaseTime;

    @Schema(description = "解除备注", example = "人工复核解除")
    private String releaseRemark;

    @Schema(description = "扩展信息 JSON")
    private String extJson;
}
