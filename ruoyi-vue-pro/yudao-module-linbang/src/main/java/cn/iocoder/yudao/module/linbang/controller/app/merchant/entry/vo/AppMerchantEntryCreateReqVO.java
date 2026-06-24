package cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "用户 App - 服务商入驻申请 Request VO")
@Data
public class AppMerchantEntryCreateReqVO {

    @Schema(description = "服务商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "LinBang Services")
    @NotBlank(message = "服务商名称不能为空")
    private String merchantName;

    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @NotBlank(message = "联系人不能为空")
    private String contactName;

    @Schema(description = "联系人手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "联系人手机号不能为空")
    private String contactMobile;

    @Schema(description = "区域编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "310115")
    @NotBlank(message = "区域编码不能为空")
    private String regionCode;

    @Schema(description = "服务类目 ID 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "服务类目不能为空")
    private List<Long> serviceCategoryIds;

    @Schema(description = "服务范围说明", example = "Home repair and cleaning")
    private String serviceScopeDesc;

    @Schema(description = "银行卡 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "银行卡不能为空")
    private Long bankCardId;

    @Schema(description = "资质 ID 列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "资质不能为空")
    private List<Long> qualificationIds;

}
