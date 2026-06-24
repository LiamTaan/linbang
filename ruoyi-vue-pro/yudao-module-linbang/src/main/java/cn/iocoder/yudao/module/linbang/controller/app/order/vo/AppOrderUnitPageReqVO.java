package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 订单单元分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppOrderUnitPageReqVO extends PageParam {

    @Schema(description = "订单 ID")
    @NotNull(message = "订单 ID 不能为空")
    private Long orderId;

    @Schema(description = "单元状态筛选：PENDING_CREATE 待生成、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、PENDING_CONFIRM 待验收、FINISHED 已完成、APPEALING 申诉中、REFUNDED 已退款、CLOSED 已关闭")
    private String status;
}
