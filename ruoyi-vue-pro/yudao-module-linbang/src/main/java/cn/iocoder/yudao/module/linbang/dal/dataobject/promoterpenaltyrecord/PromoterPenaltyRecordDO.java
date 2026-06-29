package cn.iocoder.yudao.module.linbang.dal.dataobject.promoterpenaltyrecord;

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

@TableName("lb_promoter_penalty_record")
@KeySequence("lb_promoter_penalty_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoterPenaltyRecordDO extends BaseDO {

    @TableId
    private Long id;

    private Long promoterId;

    private Long userId;

    private Long contentId;

    private String penaltyAction;

    private Integer scoreChange;

    private String reason;

    private String status;
}
