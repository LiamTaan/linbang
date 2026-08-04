package cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 合作商辖区推广成交数据库聚合结果。
 */
@Data
public class PartnerPromoteTradeAggregateDTO {

    private Long convertOrderCount;
    private BigDecimal tradeAmount;
}
