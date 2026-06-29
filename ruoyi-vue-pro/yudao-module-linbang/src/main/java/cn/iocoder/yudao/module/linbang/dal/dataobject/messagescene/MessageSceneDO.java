package cn.iocoder.yudao.module.linbang.dal.dataobject.messagescene;

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

@TableName("lb_message_scene")
@KeySequence("lb_message_scene_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSceneDO extends BaseDO {

    @TableId
    private Long id;

    private String sceneCode;

    private String sceneName;

    private String messageCategory;

    private String defaultChannels;

    private Boolean mandatorySms;

    private Boolean voiceEnabled;

    private String status;

    private String bizType;

    private String remark;
}
