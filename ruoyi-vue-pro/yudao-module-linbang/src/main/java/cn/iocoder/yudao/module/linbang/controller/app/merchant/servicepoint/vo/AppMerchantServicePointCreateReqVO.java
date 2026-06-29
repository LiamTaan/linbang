package cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "用户 App - 服务点新增 Request VO")
@Data
public class AppMerchantServicePointCreateReqVO {

    @Schema(description = "服务点名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "浦东服务点")
    @NotBlank(message = "服务点名称不能为空")
    private String pointName;

    @Schema(description = "省", example = "上海市")
    private String province;

    @Schema(description = "市", example = "上海市")
    private String city;

    @Schema(description = "区", example = "浦东新区")
    private String district;

    @Schema(description = "街道", example = "陆家嘴街道")
    private String street;

    @Schema(description = "详细地址", example = "世纪大道 1 号")
    private String detailAddress;

    @Schema(description = "经度", example = "121.500000")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "31.230000")
    private BigDecimal latitude;

    @Schema(description = "服务半径（公里）", requiredMode = Schema.RequiredMode.REQUIRED, example = "5.00")
    @NotNull(message = "服务半径不能为空")
    private BigDecimal serviceRadiusKm;

}
