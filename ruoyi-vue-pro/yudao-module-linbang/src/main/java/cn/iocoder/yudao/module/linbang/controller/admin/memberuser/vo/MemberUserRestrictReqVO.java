package cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户限制/封禁 Request VO")
@Data
public class MemberUserRestrictReqVO {

    @Schema(description = "用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户不能为空")
    private Long userId;

    @Schema(description = "限制类型：RESTRICT 限制、BAN 封禁、BLACKLIST 拉黑", requiredMode = Schema.RequiredMode.REQUIRED, example = "BAN")
    @NotBlank(message = "限制类型不能为空")
    private String actionType;

    @Schema(description = "限制细分类型，例如 ORDER_PUBLISH / ORDER_ACCEPT / LOGIN", requiredMode = Schema.RequiredMode.REQUIRED, example = "LOGIN")
    @NotBlank(message = "限制细分类型不能为空")
    private String restrictType;

    @Schema(description = "原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "多次异常下单")
    @NotBlank(message = "限制原因不能为空")
    private String reason;

    @Schema(description = "限制截止时间，可为空表示长期", example = "2026-07-31T23:59:59")
    private LocalDateTime endTime;
}
