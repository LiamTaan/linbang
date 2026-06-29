package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Schema(description = "设备拍摄时间。用于业务级存证快照", example = "2026-06-28T10:30:00")
    private LocalDateTime deviceTime;

    @Schema(description = "取证地址文本，例如 XX 小区 3 栋 2 单元", example = "广东省深圳市南山区科技园 XX 大厦 1201")
    private String addressText;

}
