package cn.iocoder.yudao.module.linbang.dal.dataobject.userrestrictrecord;

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

@TableName("lb_user_restrict_record")
@KeySequence("lb_user_restrict_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRestrictRecordDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String restrictType;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String sourceRuleCode;

    private String sourceBizType;

    private Long sourceBizId;

    private String reason;

    private Long releasedBy;

    private LocalDateTime releasedTime;

    private String releaseRemark;
}
