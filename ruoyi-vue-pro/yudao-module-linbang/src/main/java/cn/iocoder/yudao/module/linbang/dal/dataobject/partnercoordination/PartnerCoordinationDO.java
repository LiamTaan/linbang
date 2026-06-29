package cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@TableName("lb_partner_coordination")
@KeySequence("lb_partner_coordination_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerCoordinationDO extends BaseDO {

    @TableId
    private Long id;

    private Long partnerId;

    private String disputeType;

    private Long disputeId;

    private Long orderId;

    private Long unitId;

    private String status;

    private String coordinationRemark;

    private String escalateRemark;

    private Long initiatedBy;

    private LocalDateTime initiatedTime;

    private Long finishedBy;

    private LocalDateTime finishedTime;
}
