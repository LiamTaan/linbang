package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 待评价单元 Response VO")
@Data
public class AppPendingReviewUnitRespVO {

    @Schema(description = "订单 ID", example = "1")
    private Long orderId;

    @Schema(description = "单元 ID", example = "2")
    private Long unitId;

    @Schema(description = "订单号", example = "LBO202606280001")
    private String orderNo;

    @Schema(description = "单元号", example = "LBU202606280001")
    private String unitNo;

    @Schema(description = "单元标题", example = "深度保洁")
    private String unitTitle;

    @Schema(description = "单元金额")
    private BigDecimal unitAmount;

    @Schema(description = "评价目标用户 ID", example = "100")
    private Long toUserId;

    @Schema(description = "评价目标昵称", example = "服务商小李")
    private String toUserNickname;

    @Schema(description = "评价目标手机号", example = "13800138000")
    private String toUserMobile;

    @Schema(description = "角色方向：TO_MERCHANT 评价服务商、TO_USER 评价用户", example = "TO_MERCHANT")
    private String reviewDirection;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "超时自动评价时间")
    private LocalDateTime autoReviewDeadlineTime;
}
