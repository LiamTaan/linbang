package cn.iocoder.yudao.module.linbang.controller.admin.memberaddress.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 用户地址表新增/修改 Request VO")
@Data
public class MemberUserAddressSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21642")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17886")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @NotEmpty(message = "联系人不能为空")
    private String receiverName;

    @Schema(description = "联系电话", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "联系电话不能为空")
    private String receiverMobile;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String district;

    @Schema(description = "街道")
    private String street;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "详细地址不能为空")
    private String detailAddress;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "区域编码")
    private String adcode;

    @Schema(description = "是否默认", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认不能为空")
    private Boolean isDefault;

}