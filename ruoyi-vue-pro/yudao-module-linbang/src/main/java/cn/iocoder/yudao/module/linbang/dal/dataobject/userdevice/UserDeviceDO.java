package cn.iocoder.yudao.module.linbang.dal.dataobject.userdevice;

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

@TableName("lb_user_device")
@KeySequence("lb_user_device_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String deviceFingerprint;

    private String deviceName;

    private String lastIp;

    private LocalDateTime lastLoginTime;

    private String status;
}
