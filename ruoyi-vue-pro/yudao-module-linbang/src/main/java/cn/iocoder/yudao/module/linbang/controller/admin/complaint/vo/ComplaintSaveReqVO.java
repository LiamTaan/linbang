package cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 投诉新增/修改 Request VO")
@Data
public class ComplaintSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1039")
    private Long id;

    @Schema(description = "投诉单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "投诉单号不能为空")
    private String complaintNo;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "14645")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "单元ID", example = "14192")
    private Long unitId;

    @Schema(description = "投诉人用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20578")
    @NotNull(message = "投诉人用户ID不能为空")
    private Long complainantUserId;

    @Schema(description = "被投诉人用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17431")
    @NotNull(message = "被投诉人用户ID不能为空")
    private Long respondentUserId;

    @Schema(description = "投诉类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "投诉类型不能为空")
    private String complaintType;

    @Schema(description = "投诉内容")
    private String content;

    @Schema(description = OpenApiSchemaConstants.COMPLAINT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "处理人")
    private Long handleBy;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理结果")
    private String resultDesc;

}
