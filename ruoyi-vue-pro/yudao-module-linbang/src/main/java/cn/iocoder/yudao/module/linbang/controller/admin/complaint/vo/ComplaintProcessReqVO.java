package cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 投诉处理 Request VO")
@Data
public class ComplaintProcessReqVO {

    @Schema(description = "投诉编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "投诉编号不能为空")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.COMPLAINT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "FINISHED")
    @NotBlank(message = "处理状态不能为空")
    private String status;

    @Schema(description = "处理结果", example = "已沟通并完成赔付")
    private String resultDesc;

}
