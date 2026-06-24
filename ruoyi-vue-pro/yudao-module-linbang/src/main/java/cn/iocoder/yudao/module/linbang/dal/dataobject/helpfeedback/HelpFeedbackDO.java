package cn.iocoder.yudao.module.linbang.dal.dataobject.helpfeedback;

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

@TableName("lb_help_feedback")
@KeySequence("lb_help_feedback_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpFeedbackDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String feedbackType;

    private String content;

    private String contactMobile;

    private String attachmentUrls;

    private String status;

    private Long handleBy;

    private String handleRemark;
}
