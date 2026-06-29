package cn.iocoder.yudao.module.linbang.service.match;

public interface PriorityPoolService {

    boolean isInPriorityPool(Long merchantId);

    void recomputeMerchantPriorityPool(Long merchantId);

    void recomputeAllPriorityPool();

    void freezeCurrent(Long merchantId, String reasonRemark);

    void unfreezeByRecompute(Long merchantId);
}
