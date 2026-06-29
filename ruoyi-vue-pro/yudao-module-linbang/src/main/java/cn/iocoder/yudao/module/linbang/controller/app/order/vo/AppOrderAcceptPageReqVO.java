package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Schema(description = "用户 App - 待接单需求分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppOrderAcceptPageReqVO extends PageParam {

    @Schema(description = "顶部搜索关键字，匹配需求描述和类目名称", example = "电路检修")
    private String keyword;

    @Schema(description = "类目 ID。传父类目时，会查询当前类目及全部下级类目", example = "340504")
    private Long categoryId;

    @Schema(description = "计价方式，传 lb_pricing_mode 字典的 dict.value，例如 FIXED_PRICE、CONTRACT、OUTSOURCING、HOURLY、BY_UNIT", example = "HOURLY")
    private String pricingMode;

    @Schema(description = "距离排序：NEAREST 最近优先、FARTHEST 最远优先", example = "NEAREST")
    private String distanceSort;

    @Schema(description = "价格排序：PRICE_ASC 价格升序、PRICE_DESC 价格降序", example = "PRICE_ASC")
    private String priceSort;

    @Schema(description = "时间排序：NEWEST 最新发布优先、OLDEST 最早发布优先", example = "NEWEST")
    private String publishTimeSort;

    @Schema(description = "最低价格，单位元", example = "100.00")
    private BigDecimal minOrderAmount;

    @Schema(description = "最高价格，单位元", example = "500.00")
    private BigDecimal maxOrderAmount;
}
