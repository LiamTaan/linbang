package cn.iocoder.yudao.module.linbang.dal.dataobject.promoterrelation;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("lb_promoter_relation")
@KeySequence("lb_promoter_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoterRelationDO extends BaseDO {

    @TableId
    private Long id;

    private Long promoterId;

    private Long userId;

    private LocalDateTime bindTime;

    private Long firstOrderId;

    private String convertStatus;
}
