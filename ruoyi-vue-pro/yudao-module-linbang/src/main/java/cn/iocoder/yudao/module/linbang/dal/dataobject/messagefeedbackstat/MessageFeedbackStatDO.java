package cn.iocoder.yudao.module.linbang.dal.dataobject.messagefeedbackstat;

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

import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("lb_message_feedback_stat")
@KeySequence("lb_message_feedback_stat_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageFeedbackStatDO extends BaseDO {

    @TableId
    private Long id;

    private LocalDate statDate;

    private String sceneCode;

    private String messageCategory;

    private Long templateId;

    private Long campaignId;

    private Long pushTaskId;

    private String channelType;

    private Integer plannedAudienceCount;

    private Integer reachedCount;

    private Integer clickedCount;

    private Integer readCount;

    private Integer voicePlayedCount;

    private BigDecimal reachRate;

    private BigDecimal clickRate;

    private BigDecimal readRate;
}
