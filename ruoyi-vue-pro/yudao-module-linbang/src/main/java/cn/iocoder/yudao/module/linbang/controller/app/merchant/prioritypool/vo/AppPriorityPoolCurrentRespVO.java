package cn.iocoder.yudao.module.linbang.controller.app.merchant.prioritypool.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 服务商优先池当前状态 Response VO")
@Data
public class AppPriorityPoolCurrentRespVO {

    @Schema(description = "服务商 ID", example = "1")
    private Long merchantId;

    @Schema(description = "当前状态", example = "IN_POOL")
    private String status;

    @Schema(description = "原因编码", example = "GOOD_REVIEW_15")
    private String reasonCode;

    @Schema(description = "原因说明")
    private String reasonRemark;

    @Schema(description = "生效时间")
    private LocalDateTime effectiveTime;

    @Schema(description = "失效时间")
    private LocalDateTime expireTime;
}
