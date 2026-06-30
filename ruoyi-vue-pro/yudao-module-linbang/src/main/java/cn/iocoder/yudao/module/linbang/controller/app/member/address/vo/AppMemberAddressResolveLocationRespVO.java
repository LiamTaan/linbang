package cn.iocoder.yudao.module.linbang.controller.app.member.address.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "用户 App - 坐标解析地址 Response VO")
@Data
public class AppMemberAddressResolveLocationRespVO {

    @Schema(description = "省", example = "广东省")
    private String province;

    @Schema(description = "市", example = "深圳市")
    private String city;

    @Schema(description = "区", example = "南山区")
    private String district;

    @Schema(description = "街道/乡镇", example = "粤海街道")
    private String street;

    @Schema(description = "详细地址，优先保留地图选择器返回的 POI 名称或门牌信息", example = "腾讯大厦")
    private String detailAddress;

    @Schema(description = "经度", example = "113.943520")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "22.540503")
    private BigDecimal latitude;

    @Schema(description = "高德 adcode", example = "440305")
    private String adcode;

    @Schema(description = "用于前端展示的完整地址", example = "广东省深圳市南山区粤海街道腾讯大厦")
    private String displayAddress;
}
