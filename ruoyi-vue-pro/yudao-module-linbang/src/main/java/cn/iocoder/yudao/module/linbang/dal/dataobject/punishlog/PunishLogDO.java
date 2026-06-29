package cn.iocoder.yudao.module.linbang.dal.dataobject.punishlog;

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

@TableName("lb_punish_log")
@KeySequence("lb_punish_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PunishLogDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String punishType;

    private String status;

    private String reason;

    private String sourceBizType;

    private Long sourceBizId;

    private String sourceRecordType;

    private Long sourceRecordId;

    private Long operatorId;

    private LocalDateTime operateTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long releaseOperatorId;

    private LocalDateTime releaseTime;

    private String releaseRemark;

    private String extJson;
}
