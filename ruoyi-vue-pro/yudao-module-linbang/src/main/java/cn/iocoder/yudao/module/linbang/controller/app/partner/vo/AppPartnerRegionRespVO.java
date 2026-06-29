package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 合作商辖区明细 Response VO")
@Data
public class AppPartnerRegionRespVO {

    @Schema(description = "合作商 ID", example = "1")
    private Long partnerId;

    @Schema(description = "合作商名称", example = "深圳南山合作商")
    private String partnerName;

    @Schema(description = "辖区节点列表")
    private List<RegionItem> regions;

    @Data
    public static class RegionItem {

        @Schema(description = "辖区关联 ID", example = "101")
        private Long id;

        @Schema(description = "省", example = "广东省")
        private String province;

        @Schema(description = "市", example = "深圳市")
        private String city;

        @Schema(description = "区", example = "南山区")
        private String district;

        @Schema(description = "街道/乡镇编码，首版可为空，后续可扩展", example = "NS-YT")
        private String streetCode;

        @Schema(description = "街道/乡镇名称，首版默认复用区级名称或为空", example = "粤海街道")
        private String streetName;

        @Schema(description = "小区编码，首版预留", example = "YH-SQ-001")
        private String communityCode;

        @Schema(description = "小区名称，首版预留", example = "科技园社区")
        private String communityName;

        @Schema(description = "区域编码", example = "440305")
        private String regionCode;

        @Schema(description = OpenApiSchemaConstants.PARTNER_STATUS, example = "ENABLE")
        private String status;

        @Schema(description = "上级区域编码", example = "440300")
        private String parentRegionCode;

        @Schema(description = "排序号，首版默认按数据顺序返回", example = "1")
        private Integer sort;
    }
}
