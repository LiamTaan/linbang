package cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 区域合作商辖区配置 Request VO")
@Data
public class PartnerInfoUpdateRegionsReqVO {

    @Schema(description = "合作商 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "合作商编号不能为空")
    private Long id;

    @Schema(description = "辖区列表")
    @NotEmpty(message = "请至少配置一个辖区")
    @Valid
    private List<RegionItem> regions;

    @Schema(description = "辖区项")
    @Data
    public static class RegionItem {

        @Schema(description = "省份名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "浙江省")
        @NotBlank(message = "省份不能为空")
        private String province;

        @Schema(description = "城市名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "杭州市")
        @NotBlank(message = "城市不能为空")
        private String city;

        @Schema(description = "区县名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "西湖区")
        @NotBlank(message = "区县不能为空")
        private String district;

        @Schema(description = "行政区划编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "330106")
        @NotBlank(message = "行政区划编码不能为空")
        private String adcode;
    }
}
