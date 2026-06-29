package cn.iocoder.yudao.module.linbang.service.reviewcomment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantReviewMetricsResp {

    private Long merchantId;

    private BigDecimal compositeScore;

    private Integer reviewCount;

    private Integer positiveReviewCount;

    private Integer neutralReviewCount;

    private Integer negativeReviewCount;

    private BigDecimal positiveRate;

    private Boolean inPositivePriorityPool;
}
