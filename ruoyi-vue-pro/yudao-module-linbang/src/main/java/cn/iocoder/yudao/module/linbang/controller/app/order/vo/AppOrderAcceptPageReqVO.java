package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "用户 App - 待接单需求分页 Request VO；pageNo × pageSize 最大为 10000")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppOrderAcceptPageReqVO extends PageParam {

    @Schema(description = "顶部搜索关键字，匹配需求描述和类目名称", example = "电路检修")
    @Size(max = 100, message = "搜索关键字不能超过 100 个字符")
    private String keyword;

    @Schema(description = "类目 ID。传父类目时，会查询当前类目及全部下级类目", example = "340504")
    @Positive(message = "类目 ID 必须大于 0")
    private Long categoryId;

    @Schema(description = "计价方式，传 lb_pricing_mode 字典的 dict.value，例如 FIXED_PRICE、CONTRACT、OUTSOURCING、HOURLY、BY_UNIT", example = "HOURLY")
    @Size(max = 32, message = "计价方式不能超过 32 个字符")
    private String pricingMode;

    @Schema(description = "距离排序：NEAREST 最近优先、FARTHEST 最远优先", example = "NEAREST")
    @Pattern(regexp = "(?i)^(?:|NEAREST|FARTHEST)$", message = "距离排序仅支持 NEAREST 或 FARTHEST")
    private String distanceSort;

    @Schema(description = "价格排序：PRICE_ASC 价格升序、PRICE_DESC 价格降序", example = "PRICE_ASC")
    @Pattern(regexp = "(?i)^(?:|PRICE_ASC|PRICE_DESC)$", message = "价格排序仅支持 PRICE_ASC 或 PRICE_DESC")
    private String priceSort;

    @Schema(description = "时间排序：NEWEST 最新发布优先、OLDEST 最早发布优先", example = "NEWEST")
    @Pattern(regexp = "(?i)^(?:|NEWEST|OLDEST)$", message = "时间排序仅支持 NEWEST 或 OLDEST")
    private String publishTimeSort;

    @Schema(description = "最低价格，单位元", example = "100.00")
    @DecimalMin(value = "0.00", message = "最低价格不能小于 0")
    @Digits(integer = 16, fraction = 2, message = "最低价格最多 16 位整数和 2 位小数")
    private BigDecimal minOrderAmount;

    @Schema(description = "最高价格，单位元", example = "500.00")
    @DecimalMin(value = "0.00", message = "最高价格不能小于 0")
    @Digits(integer = 16, fraction = 2, message = "最高价格最多 16 位整数和 2 位小数")
    private BigDecimal maxOrderAmount;
}
