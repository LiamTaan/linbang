package cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 合作商工作台数据库聚合结果。
 */
@Data
public class PartnerWorkbenchAggregateDTO {

    private Long orderCount;
    private BigDecimal tradeAmount;
    private Long pendingDisputeCount;
}
