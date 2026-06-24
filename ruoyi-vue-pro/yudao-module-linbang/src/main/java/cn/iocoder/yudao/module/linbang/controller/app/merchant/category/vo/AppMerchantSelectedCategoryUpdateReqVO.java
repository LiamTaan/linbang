package cn.iocoder.yudao.module.linbang.controller.app.merchant.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "用户 App - 当前服务商类目更新 Request VO")
@Data
public class AppMerchantSelectedCategoryUpdateReqVO {

    @Schema(description = "服务类目 ID 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "服务类目不能为空")
    private List<Long> serviceCategoryIds;

}
