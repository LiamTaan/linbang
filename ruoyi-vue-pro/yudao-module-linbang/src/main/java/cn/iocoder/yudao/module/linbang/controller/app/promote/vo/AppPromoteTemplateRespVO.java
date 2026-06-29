package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 推广模板 Response VO")
@Data
public class AppPromoteTemplateRespVO {

    @Schema(description = "模板 ID", example = "380001")
    private Long id;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "模板标题")
    private String title;

    @Schema(description = "模板文案")
    private String content;

    @Schema(description = "模板类型", example = "TEXT")
    private String templateType;

    @Schema(description = "适用渠道", example = "APP_POPUP")
    private String channelType;

    @Schema(description = "消息分类", example = "MARKETING")
    private String messageCategory;

    @Schema(description = "路由类型", example = "APP_PAGE")
    private String routeType;

    @Schema(description = "路由值")
    private String routeValue;

    @Schema(description = "模板状态", example = "ENABLE")
    private String status;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
