package cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign;

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

@TableName("lb_message_campaign")
@KeySequence("lb_message_campaign_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageCampaignDO extends BaseDO {

    @TableId
    private Long id;

    private String campaignName;

    private String sourceType;

    private String auditStatus;

    private String executeStatus;

    private String targetMode;

    private String targetRegionCodes;

    private String targetCategoryIds;

    private String targetRoleCodes;

    private String deliveryTimeWindows;

    private LocalDateTime scheduleTime;

    private LocalDateTime executeTime;

    private String sceneCode;

    private String messageCategory;

    private String bizType;

    private Long bizId;

    private Long applicantUserId;

    private Long auditUserId;

    private LocalDateTime auditTime;

    private Integer plannedAudienceCount;

    private Integer reachedCount;

    private Integer clickedCount;

    private Integer readCount;

    private Integer voicePlayedCount;

    private String contentSnapshot;

    private String auditRemark;

    private String rejectReason;

    private String cancelReason;
}
