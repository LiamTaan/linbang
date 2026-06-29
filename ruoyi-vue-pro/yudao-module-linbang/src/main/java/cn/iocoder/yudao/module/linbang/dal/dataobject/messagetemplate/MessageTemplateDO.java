package cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("lb_message_template")
@KeySequence("lb_message_template_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageTemplateDO extends BaseDO {

    @TableId
    private Long id;

    private String templateCode;

    private String templateName;

    private String sceneCode;

    private String messageCategory;

    private String templateType;

    private String channelType;

    private String titleTemplate;

    private String contentTemplate;

    private String routeType;

    private String routeValue;

    private String mpTemplateId;

    private String smsTemplateCode;

    private String voiceTextTemplate;

    private Integer sort;

    private String status;
}
