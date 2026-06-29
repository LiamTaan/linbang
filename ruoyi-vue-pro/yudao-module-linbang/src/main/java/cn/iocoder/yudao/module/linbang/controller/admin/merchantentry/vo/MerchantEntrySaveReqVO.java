package cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 服务商入驻申请表新增/修改 Request VO")
@Data
public class MerchantEntrySaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9209")
    private Long id;

    @Schema(description = "服务商ID", example = "18167")
    private Long merchantId;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7267")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "入驻单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "入驻单号不能为空")
    private String entryNo;

    @Schema(description = "区域编码")
    private String regionCode;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @NotEmpty(message = "初审状态不能为空")
    private String firstAuditStatus;

    @Schema(description = "初审人")
    private Long firstAuditBy;

    @Schema(description = "初审时间")
    private LocalDateTime firstAuditTime;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @NotEmpty(message = "终审状态不能为空")
    private String finalAuditStatus;

    @Schema(description = "终审人")
    private Long finalAuditBy;

    @Schema(description = "终审时间")
    private LocalDateTime finalAuditTime;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "备注", example = "??")
    private String remark;

}
