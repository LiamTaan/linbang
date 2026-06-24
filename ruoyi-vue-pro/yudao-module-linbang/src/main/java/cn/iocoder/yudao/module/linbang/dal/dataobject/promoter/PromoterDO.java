package cn.iocoder.yudao.module.linbang.dal.dataobject.promoter;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

@TableName("lb_promoter")
@KeySequence("lb_promoter_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoterDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String levelCode;

    private String inviteCode;

    private String inviteUrl;

    private Integer bindUserCount;

    private Integer convertCount;

    private BigDecimal totalCommissionAmount;

    private BigDecimal availableCommissionAmount;

    private String status;
}
