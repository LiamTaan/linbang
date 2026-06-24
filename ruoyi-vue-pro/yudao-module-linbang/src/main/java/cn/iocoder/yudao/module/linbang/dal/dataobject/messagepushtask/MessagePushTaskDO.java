package cn.iocoder.yudao.module.linbang.dal.dataobject.messagepushtask;

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

@TableName("lb_message_push_task")
@KeySequence("lb_message_push_task_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePushTaskDO extends BaseDO {

    @TableId
    private Long id;

    private String taskName;

    private String targetScope;

    private String channelType;

    private Long templateId;

    private String bizType;

    private Long bizId;

    private LocalDateTime plannedSendTime;

    private LocalDateTime executeTime;

    private String status;

    private Integer successCount;

    private Integer failCount;

    private String creatorRemark;
}
