package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 优先池 Response VO")
@Data
public class PriorityPoolRespVO {

    @Schema(description = "记录 ID")
    private Long id;

    @Schema(description = "服务商 ID")
    private Long merchantId;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "原因编码")
    private String reasonCode;

    @Schema(description = "原因说明")
    private String reasonRemark;

    @Schema(description = "是否当前生效")
    private Boolean currentFlag;

    @Schema(description = "生效时间")
    private LocalDateTime effectiveTime;

    @Schema(description = "失效时间")
    private LocalDateTime expireTime;
}
