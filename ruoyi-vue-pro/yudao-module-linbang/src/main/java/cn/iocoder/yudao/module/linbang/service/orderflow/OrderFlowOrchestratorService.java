package cn.iocoder.yudao.module.linbang.service.orderflow;

public interface OrderFlowOrchestratorService {

    void onOrderPaid(Long orderId);

    void onOrderAccepted(Long orderId);

    void onOrderServing(Long orderId);

    void onUnitFinished(Long orderId);

    void onOrderFlowed(Long orderId);

    void onRefundSuccess(Long orderId);

    void onRefundFailed(Long orderId);

    void repairAbnormalOrders();
}
