package cn.iocoder.yudao.module.linbang.controller.app.member.address.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "用户 App - 附近地点 Response VO")
@Data
public class AppMemberAddressNearbyPoiRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "POI 唯一标识", example = "B0FFG92M6W")
    private String id;

    @Schema(description = "地点名称", example = "运城市中心医院")
    private String name;

    @Schema(description = "地点地址摘要", example = "红旗西街173号")
    private String address;

    @Schema(description = "距当前选点的距离，单位米", example = "12")
    private Integer distanceMeters;

    @Schema(description = "经度", example = "111.007520")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "35.028660")
    private BigDecimal latitude;
}
