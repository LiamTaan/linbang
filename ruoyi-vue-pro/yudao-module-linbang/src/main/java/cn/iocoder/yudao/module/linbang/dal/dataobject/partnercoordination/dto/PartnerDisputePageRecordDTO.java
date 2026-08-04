package cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 合作商辖区纠纷分页数据库投影。
 */
@Data
public class PartnerDisputePageRecordDTO {

    private String disputeType;
    private Long disputeId;
    private String disputeNo;
    private Long orderId;
    private String orderNo;
    private Long unitId;
    private String unitNo;
    private String regionCode;
    private String status;
    private String content;
    private String resultDesc;
    private LocalDateTime createTime;
}
