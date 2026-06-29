package cn.iocoder.yudao.module.linbang.service.match;

public interface MatchDispatchService {

    void startInitialDispatch(Long orderId);

    void processDispatchTick();

    void markAccepted(Long unitId, Long acceptedMerchantId);
}
