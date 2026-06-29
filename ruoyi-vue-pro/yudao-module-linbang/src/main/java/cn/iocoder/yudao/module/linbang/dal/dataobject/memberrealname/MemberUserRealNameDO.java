package cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname;

import lombok.*;
import java.time.LocalDate;
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
     * 手持证件视频文件ID
     */
    private Long holdCardVideoFileId;
    /**
     * 身份证有效期开始
     */
    private LocalDate idCardValidFrom;
    /**
     * 身份证有效期结束
     */
    private LocalDate idCardValidEnd;
    /**
     * 活体结果
     */
    private String livenessResult;
    /**
     * 人脸核验结果
     */
    private String faceVerifyResult;
    /**
     * 微信实名关联状态
     */
    private String wechatRealNameStatus;
    /**
     * 支付宝实名关联状态
     */
    private String alipayRealNameStatus;
    /**
     * 核验供应商
     */
    private String verifyProvider;
    /**
     * 核验流水号
     */
    private String verifyFlowNo;
    /**
     * 核验发起时间
     */
    private LocalDateTime verifyStartedTime;
    /**
     * 核验完成时间
     */
    private LocalDateTime verifyCompletedTime;
    /**
     * 核验失败原因
     */
    private String verifyFailReason;
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
