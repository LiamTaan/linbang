package cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 商户子账号服务点更新 Request VO")
@Data
public class MerchantSubAccountServicePointUpdateReqVO {

    @Schema(description = "子账号 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "子账号 ID 不能为空")
    private Long id;

    @Schema(description = "服务点 ID 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "服务点不能为空")
    private List<Long> servicePointIds;
}
