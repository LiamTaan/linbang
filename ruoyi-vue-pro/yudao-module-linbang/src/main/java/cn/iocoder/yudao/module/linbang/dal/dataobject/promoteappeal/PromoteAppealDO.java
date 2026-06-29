package cn.iocoder.yudao.module.linbang.dal.dataobject.promoteappeal;

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

@TableName("lb_promote_appeal")
@KeySequence("lb_promote_appeal_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoteAppealDO extends BaseDO {

    @TableId
    private Long id;

    private Long contentId;

    private Long promoterId;

    private Long userId;

    private String appealReason;

    private String status;

    private String auditRemark;

    private String rejectReason;

    private Long auditBy;

    private LocalDateTime auditTime;
}
