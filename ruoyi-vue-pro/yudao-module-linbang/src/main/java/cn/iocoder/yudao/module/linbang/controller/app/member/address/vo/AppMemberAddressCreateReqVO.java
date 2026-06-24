package cn.iocoder.yudao.module.linbang.controller.app.member.address.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "用户 App - 新增地址 Request VO")
@Data
public class AppMemberAddressCreateReqVO {

    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @NotBlank(message = "联系人不能为空")
    private String receiverName;

    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "联系电话不能为空")
    private String receiverMobile;

    @Schema(description = "省", example = "Shanghai")
    private String province;

    @Schema(description = "市", example = "Shanghai")
    private String city;

    @Schema(description = "区", example = "Pudong")
    private String district;

    @Schema(description = "街道", example = "Lujiazui Street")
    private String street;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "No.1 Lane 2")
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    @Schema(description = "经度", example = "121.5")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "31.2")
    private BigDecimal latitude;

    @Schema(description = "区域编码", example = "310115")
    private String adcode;

    @Schema(description = "是否默认地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否默认不能为空")
    private Boolean isDefault;
}
