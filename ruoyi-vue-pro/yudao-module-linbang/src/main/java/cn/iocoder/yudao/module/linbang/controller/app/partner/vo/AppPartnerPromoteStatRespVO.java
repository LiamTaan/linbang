package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "用户 App - 合作商辖区推广数据 Response VO")
@Data
public class AppPartnerPromoteStatRespVO {

    @Schema(description = "合作商 ID", example = "1")
    private Long partnerId;

    @Schema(description = "今日新增用户数，按用户默认地址归属到当前辖区且今日注册成功的真实用户统计", example = "12")
    private Integer todayNewUserCount;

    @Schema(description = "辖区累计用户数，按用户默认地址归属到当前辖区的真实用户统计", example = "32")
    private Integer newUserCount;

    @Schema(description = "辖区推广员数，指在当前辖区用户推广关系中出现过的真实推广员数量", example = "8")
    private Integer boundPromoterCount;

    @Schema(description = "辖区推广转化订单数，按真实推广佣金单关联的 sourceOrderId 去重统计", example = "15")
    private Integer convertOrderCount;

    @Schema(description = "辖区推广成交额，按真实推广佣金单关联成交订单金额汇总，单位元", example = "3280.00")
    private BigDecimal tradeAmount;
}
