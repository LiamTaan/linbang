package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 流单退款看板分页 Request VO")
@Data
public class OrderFlowPageReqVO extends PageParam {

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "派单状态")
    private String dispatchStatus;

    @Schema(description = "自动退款状态")
    private String autoRefundStatus;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getDispatchStatus() {
        return dispatchStatus;
    }

    public void setDispatchStatus(String dispatchStatus) {
        this.dispatchStatus = dispatchStatus;
    }

    public String getAutoRefundStatus() {
        return autoRefundStatus;
    }

    public void setAutoRefundStatus(String autoRefundStatus) {
        this.autoRefundStatus = autoRefundStatus;
    }
}
