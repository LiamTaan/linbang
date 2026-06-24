package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "用户 App - 提交申诉 Request VO")
@Data
public class AppAppealCreateReqVO {

    @Schema(description = "订单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单不能为空")
    private Long orderId;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "申诉类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申诉类型不能为空")
    private String appealType;

    @Schema(description = "申诉内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申诉内容不能为空")
    private String content;

    @Schema(description = "附件文件 ID 列表")
    private List<Long> fileIds;

}
