package cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实名认证表 DO
 *
 * @author dawn
 */
@TableName("lb_user_real_name")
@KeySequence("lb_user_real_name_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUserRealNameDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 身份证号
     */
    private String idCardNo;
    /**
     * 身份证正面文件ID
     */
    private Long idCardFrontFileId;
    /**
     * 身份证反面文件ID
     */
    private Long idCardBackFileId;
    /**
     * 手持证件文件ID
     */
    private Long holdCardFileId;
    /**
     * 活体结果
     */
    private String livenessResult;
    /**
     * 人脸核验结果
     */
    private String faceVerifyResult;
    /**
     * 审核状态
     *
     * 对应字典类型 lb_audit_status
     */
    private String auditStatus;
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 审核人
     */
    private Long auditBy;
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    /**
     * 驳回原因
     */
    private String rejectReason;


}
