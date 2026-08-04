package cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 合作商辖区推广关系与佣金数据库聚合结果。
 */
@Data
public class PartnerPromoteAggregateDTO {

    private Long todayNewUserCount;
    private Long newUserCount;
    private Long boundPromoterCount;
    private Long relationCount;
    private Long convertedRelationCount;
    private Long commissionOrderCount;
    private BigDecimal commissionAmount;
}
