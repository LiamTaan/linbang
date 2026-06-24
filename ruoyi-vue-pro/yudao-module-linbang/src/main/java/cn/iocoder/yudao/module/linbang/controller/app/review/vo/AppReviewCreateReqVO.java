package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 提交评价 Request VO")
@Data
public class AppReviewCreateReqVO {

    @Schema(description = "订单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单不能为空")
    private Long orderId;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "评价目标用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "评价对象不能为空")
    private Long toUserId;

    @Schema(description = "星级", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    @NotNull(message = "星级不能为空")
    @Min(value = 1, message = "星级最少为 1")
    @Max(value = 5, message = "星级最多为 5")
    private Integer starLevel;

    @Schema(description = "评价内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "评价内容不能为空")
    private String content;

}
