package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "用户 App - 合作商辖区推广数据 Response VO")
@Data
public class AppPartnerPromoteStatRespVO {

    @Schema(description = "合作商 ID", example = "1")
    private Long partnerId;

    @Schema(description = "新增用户数", example = "32")
    private Integer newUserCount;

    @Schema(description = "绑定推广员数", example = "8")
    private Integer boundPromoterCount;

    @Schema(description = "转化订单数", example = "15")
    private Integer convertOrderCount;

    @Schema(description = "推广成交额，单位元", example = "3280.00")
    private BigDecimal tradeAmount;
}
