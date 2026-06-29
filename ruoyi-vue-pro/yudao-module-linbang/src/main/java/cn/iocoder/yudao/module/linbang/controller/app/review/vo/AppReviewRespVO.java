package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 评价 Response VO")
@Data
public class AppReviewRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "评价发起人")
    private Long fromUserId;

    @Schema(description = "评价目标用户")
    private Long toUserId;

    @Schema(description = "星级")
    private Integer starLevel;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "是否自动评价")
    private Boolean isAutoReview;

    @Schema(description = "自动评价文字是否已补充")
    private Boolean isContentSupplemented;

    @Schema(description = "当前评价是否仍可编辑")
    private Boolean reviewEditable;

    @Schema(description = "当前评价可编辑截止时间")
    private LocalDateTime reviewEditDeadline;

    @Schema(description = "评价状态，按评价业务字典展示；常见值由前后端统一字典决定", example = "ENABLE")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
