package cn.iocoder.yudao.module.linbang.controller.app.member.address.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 地址 Response VO")
@Data
public class AppMemberAddressRespVO {

    @Schema(description = "地址 ID", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "联系人", example = "Alice")
    private String receiverName;

    @Schema(description = "联系电话", example = "13800138000")
    private String receiverMobile;

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

    @Schema(description = "区域编码")
    private String adcode;

    @Schema(description = "是否默认地址")
    private Boolean isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
