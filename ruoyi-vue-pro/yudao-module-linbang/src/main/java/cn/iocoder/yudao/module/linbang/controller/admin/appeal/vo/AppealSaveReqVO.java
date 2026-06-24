package cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 申诉新增/修改 Request VO")
@Data
public class AppealSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16782")
    private Long id;

    @Schema(description = "申诉单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "申诉单号不能为空")
    private String appealNo;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32173")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "单元ID", example = "6416")
    private Long unitId;

    @Schema(description = "申诉人用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2605")
    @NotNull(message = "申诉人用户ID不能为空")
    private Long userId;

    @Schema(description = "申诉类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "申诉类型不能为空")
    private String appealType;

    @Schema(description = "申诉内容")
    private String content;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核备注", example = "你猜")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "不喜欢")
    private String rejectReason;

}