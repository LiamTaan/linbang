package cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户资质表 DO
 *
 * @author Codex
 */
@TenantIgnore
@TableName("lb_user_qualification")
@KeySequence("lb_user_qualification_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUserQualificationDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String qualificationType;

    private String qualificationName;

    private String qualificationNo;

    private Long fileId;

    private String evidenceFileIdsJson;

    private Long videoFileId;

    private LocalDate validStartDate;

    private LocalDate validEndDate;

    private String auditStatus;

    private String auditRemark;

    private Long auditBy;

    private LocalDateTime auditTime;

    private String rejectReason;

    private Boolean priorityEnabled;

}
