package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 订单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppOrderPageReqVO extends PageParam {

    @Schema(description = "订单状态筛选：PENDING_PAY 待支付、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、PENDING_CONFIRM 待确认、AFTER_SALE 售后中、FINISHED 已完成、REFUNDED 已退款、CLOSED 已关闭")
    private String status;

    @Schema(description = "订单业务分类筛选：WAIT_ACCEPT 待接、IN_SERVICE 服务中、FINISHED 已完成、AFTER_SALE 售后、WAIT_REVIEW 待评价、WAIT_PAY 待付款、REFUNDED 已退款")
    private String businessCategory;

    @Schema(description = "类目 ID")
    private Long categoryId;

    @Schema(description = "计价方式，例如一口价、按数量、按时长等业务配置值")
    private String pricingMode;

    @Schema(description = "距离排序标记，传入时由前端约定是否按距离优先排序")
    private String distanceSort;

}
