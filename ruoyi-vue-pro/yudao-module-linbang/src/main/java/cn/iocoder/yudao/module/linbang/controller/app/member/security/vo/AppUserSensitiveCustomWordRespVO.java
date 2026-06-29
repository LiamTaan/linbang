package cn.iocoder.yudao.module.linbang.controller.app.member.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 用户自定义脱敏词 Response VO")
@Data
public class AppUserSensitiveCustomWordRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "关键词")
    private String word;

    @Schema(description = "适用场景", example = "ORDER_PUBLISH")
    private String sceneType;

    @Schema(description = "状态", example = "ENABLE")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
