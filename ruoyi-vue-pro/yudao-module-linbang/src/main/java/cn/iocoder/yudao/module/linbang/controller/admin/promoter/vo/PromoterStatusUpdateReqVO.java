package cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Schema(description = "管理后台 - 推广员启停 Request VO")
@Data
public class PromoterStatusUpdateReqVO {

    @Schema(description = "推广员 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "推广员 ID 不能为空")
    private Long id;

    @Schema(description = "状态：ENABLE 启用、DISABLE 停用", requiredMode = Schema.RequiredMode.REQUIRED, example = "DISABLE")
    @Pattern(regexp = "ENABLE|DISABLE", message = "推广员状态必须为 ENABLE 或 DISABLE")
    private String status;
}
