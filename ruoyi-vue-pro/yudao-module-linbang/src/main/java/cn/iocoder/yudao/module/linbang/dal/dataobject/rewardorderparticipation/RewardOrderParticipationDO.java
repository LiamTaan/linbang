package cn.iocoder.yudao.module.linbang.dal.dataobject.rewardorderparticipation;

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

@TableName("lb_reward_order_participation")
@KeySequence("lb_reward_order_participation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardOrderParticipationDO extends BaseDO {

    @TableId
    private Long id;

    private Long rewardOrderId;

    private Long rewardUserId;

    private Long participantUserId;

    private String participantMobile;

    private String participantNickname;

    private String status;

    private String participateRemark;

    private String auditRemark;
}
