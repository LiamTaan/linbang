package cn.iocoder.yudao.module.linbang.dal.dataobject.blacklist;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("lb_blacklist")
@KeySequence("lb_blacklist_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String blackType;

    private String reason;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;
}
