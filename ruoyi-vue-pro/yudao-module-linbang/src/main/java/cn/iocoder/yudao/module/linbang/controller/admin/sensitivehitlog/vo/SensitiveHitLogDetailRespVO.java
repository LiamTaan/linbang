package cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 敏感词命中日志详情 Response VO")
@Data
public class SensitiveHitLogDetailRespVO extends SensitiveHitLogRespVO {

    @Schema(description = "敏感词 ID", example = "1001")
    private Long wordId;
}
