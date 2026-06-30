package cn.iocoder.yudao.module.linbang.controller.app.member.address.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "用户 App - 坐标解析地址 Request VO")
@Data
public class AppMemberAddressResolveLocationReqVO {

    @Schema(description = "经度，GCJ-02 坐标系", requiredMode = Schema.RequiredMode.REQUIRED, example = "113.943520")
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @Schema(description = "纬度，GCJ-02 坐标系", requiredMode = Schema.RequiredMode.REQUIRED, example = "22.540503")
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @Schema(description = "地图选择器返回的详细地址或 POI 名称，解析成功后优先保留", example = "腾讯大厦")
    private String detailAddress;
}
