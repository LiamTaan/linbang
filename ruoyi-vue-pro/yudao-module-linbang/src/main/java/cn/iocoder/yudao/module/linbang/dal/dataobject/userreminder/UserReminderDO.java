package cn.iocoder.yudao.module.linbang.dal.dataobject.userreminder;

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

@TableName("lb_user_reminder")
@KeySequence("lb_user_reminder_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReminderDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String reminderType;

    private String title;

    private String content;

    private LocalDateTime eventTime;

    private LocalDateTime nextRemindTime;

    private LocalDateTime lastTriggerTime;

    private String repeatType;

    private String status;

    private String routeType;

    private String routeValue;
}
