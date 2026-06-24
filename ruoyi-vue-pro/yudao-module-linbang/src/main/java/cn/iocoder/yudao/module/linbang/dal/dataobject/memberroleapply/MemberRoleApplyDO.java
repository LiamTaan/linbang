package cn.iocoder.yudao.module.linbang.dal.dataobject.memberroleapply;

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

@TableName("lb_user_role_apply")
@KeySequence("lb_user_role_apply_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleApplyDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String applyRoleCode;

    private String applyReason;

    private String resourceDesc;

    private String auditStatus;

    private String auditRemark;

    private String rejectReason;

    private Long auditBy;

    private LocalDateTime auditTime;
}
