package cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 评价新增/修改 Request VO")
@Data
public class ReviewCommentSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27657")
    private Long id;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "25795")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "单元ID", example = "18837")
    private Long unitId;

    @Schema(description = "评价发起人", requiredMode = Schema.RequiredMode.REQUIRED, example = "351")
    @NotNull(message = "评价发起人不能为空")
    private Long fromUserId;

    @Schema(description = "评价目标用户", requiredMode = Schema.RequiredMode.REQUIRED, example = "219")
    @NotNull(message = "评价目标用户不能为空")
    private Long toUserId;

    @Schema(description = "星级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "星级不能为空")
    private Integer starLevel;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "是否自动评价", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否自动评价不能为空")
    private Boolean isAutoReview;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态不能为空")
    private String status;

}