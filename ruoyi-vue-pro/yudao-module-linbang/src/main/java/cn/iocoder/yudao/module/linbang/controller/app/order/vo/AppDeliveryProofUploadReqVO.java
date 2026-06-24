package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "用户 App - 上传交付凭证 Request VO")
@Data
public class AppDeliveryProofUploadReqVO {

    @Schema(description = "单元 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "单元不能为空")
    private Long unitId;

    @Schema(description = "凭证类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "凭证类型不能为空")
    private String proofType;

    @Schema(description = "凭证说明")
    private String proofDesc;

    @Schema(description = "文件 ID 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "请至少上传一个交付文件")
    private List<Long> fileIds;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

}
