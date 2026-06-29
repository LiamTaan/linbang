package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 申诉进度 Response VO")
@Data
public class AppOrderAppealProgressRespVO {

    @Schema(description = "申诉 ID", example = "1")
    private Long id;

    @Schema(description = "申诉单号")
    private String appealNo;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = OpenApiSchemaConstants.APPEAL_STATUS, example = "PENDING")
    private String status;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "提交时间")
    private LocalDateTime createTime;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

}
