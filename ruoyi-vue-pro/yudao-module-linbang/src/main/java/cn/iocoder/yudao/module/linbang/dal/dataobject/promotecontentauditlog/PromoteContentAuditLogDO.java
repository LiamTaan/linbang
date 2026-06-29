package cn.iocoder.yudao.module.linbang.dal.dataobject.promotecontentauditlog;

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

@TableName("lb_promote_content_audit_log")
@KeySequence("lb_promote_content_audit_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoteContentAuditLogDO extends BaseDO {

    @TableId
    private Long id;

    private Long contentId;

    private String auditType;

    private String auditResult;

    private String auditRemark;

    private String rejectReason;

    private Long auditBy;

    private LocalDateTime auditTime;
}
