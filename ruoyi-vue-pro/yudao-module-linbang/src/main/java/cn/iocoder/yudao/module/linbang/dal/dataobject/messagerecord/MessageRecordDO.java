package cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("lb_message_record")
@KeySequence("lb_message_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRecordDO extends BaseDO {

    @TableId
    private Long id;

    private Long templateId;

    private Long pushTaskId;

    private Long receiverUserId;

    private String channelType;

    private String bizType;

    private Long bizId;

    private String sendStatus;

    private LocalDateTime sendTime;

    private String failReason;
}
