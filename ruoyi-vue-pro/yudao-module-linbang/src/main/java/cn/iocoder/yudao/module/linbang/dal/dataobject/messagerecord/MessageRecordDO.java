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

    private Long campaignId;

    private Long pushTaskId;

    private Long receiverUserId;

    private String sceneCode;

    private String messageCategory;

    private String channelType;

    private String bizType;

    private Long bizId;

    private String dedupeKey;

    private String sendStatus;

    private LocalDateTime sendTime;

    private String failReason;

    private String title;

    private String contentSnapshot;

    private String routeType;

    private String routeValue;

    private String readStatus;

    private LocalDateTime readTime;

    private LocalDateTime exposedTime;

    private LocalDateTime clickTime;

    private LocalDateTime voicePlayedTime;

    private String voiceText;

    private String providerMessageId;
}
