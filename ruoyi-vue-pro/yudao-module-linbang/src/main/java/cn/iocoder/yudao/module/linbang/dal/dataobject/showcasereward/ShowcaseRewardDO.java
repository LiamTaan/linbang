package cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward;

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

/**
 * 晒单悬赏优先记录 DO
 */
@TableName("lb_showcase_reward")
@KeySequence("lb_showcase_reward_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowcaseRewardDO extends BaseDO {

    @TableId
    private Long id;

    private Long merchantId;

    private Long userId;

    private String title;

    private String description;

    private String evidenceFileIdsJson;

    private String auditStatus;

    private String auditRemark;

    private String rejectReason;

    private Long auditBy;

    private LocalDateTime auditTime;

    private LocalDateTime effectiveStartTime;

    private LocalDateTime effectiveEndTime;

    private Boolean priorityEnabled;
}
