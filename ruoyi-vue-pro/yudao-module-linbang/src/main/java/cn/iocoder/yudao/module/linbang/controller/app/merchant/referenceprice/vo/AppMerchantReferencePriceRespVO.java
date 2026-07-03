package cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 服务商参考价格 Response VO")
@Data
public class AppMerchantReferencePriceRespVO {

    @Schema(description = "参考价格 ID", example = "1")
    private Long id;

    @Schema(description = "服务类目 ID", example = "1")
    private Long categoryId;

    @Schema(description = "服务类目名称", example = "家庭保洁")
    private String categoryName;

    @Schema(description = "价格单位文案", example = "元/次")
    private String priceUnitLabel;

    @Schema(description = "参考最低价", example = "88.00")
    private BigDecimal referencePriceMin;

    @Schema(description = "参考最高价", example = "188.00")
    private BigDecimal referencePriceMax;

    @Schema(description = "参考价格说明")
    private String referencePriceDesc;

    @Schema(description = "状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String status;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

