package cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 服务点 Response VO")
@Data
public class AppMerchantServicePointRespVO {

    @Schema(description = "服务点 ID", example = "1")
    private Long id;
    @Schema(description = "服务商 ID", example = "1")
    private Long merchantId;
    @Schema(description = "服务点名称")
    private String pointName;
    @Schema(description = "省")
    private String province;
    @Schema(description = "市")
    private String city;
    @Schema(description = "区")
    private String district;
    @Schema(description = "街道")
    private String street;
    @Schema(description = "详细地址")
    private String detailAddress;
    @Schema(description = "经度")
    private BigDecimal longitude;
    @Schema(description = "纬度")
    private BigDecimal latitude;
    @Schema(description = "服务半径，单位公里")
    private BigDecimal serviceRadiusKm;
    @Schema(description = "服务点状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
