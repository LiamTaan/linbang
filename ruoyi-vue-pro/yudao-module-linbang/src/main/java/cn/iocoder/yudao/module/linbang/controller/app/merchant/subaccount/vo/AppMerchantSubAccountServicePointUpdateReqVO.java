package cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "用户 App - 更新子账号服务点关联 Request VO")
@Data
public class AppMerchantSubAccountServicePointUpdateReqVO {

    @Schema(description = "子账号 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "子账号 ID 不能为空")
    private Long id;

    @Schema(description = "服务点 ID 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "服务点不能为空")
    private List<Long> servicePointIds;
}
