package cn.iocoder.yudao.module.linbang.dal.dataobject.registerreminder;

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

@TableName("lb_register_reminder_record")
@KeySequence("lb_register_reminder_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterReminderRecordDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String reminderKey;

    private String reminderScene;

    private String deviceId;

    private Integer socialType;

    private String socialOpenid;

    private Integer triggerCount;

    private Integer cooldownMinutes;

    private LocalDateTime lastTriggerTime;

    private LocalDateTime lastAckTime;

    private String status;
}
