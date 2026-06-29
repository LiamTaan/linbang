package cn.iocoder.yudao.module.linbang.dal.dataobject.appeal;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 申诉 DO
 *
 * @author dawn
 */
@TableName("lb_appeal")
@KeySequence("lb_appeal_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppealDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 申诉单号
     */
    private String appealNo;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 单元ID
     */
    private Long unitId;
    /**
     * 申诉人用户ID
     */
    private Long userId;
    /**
     * 申诉类型
     */
    private String appealType;
    /**
     * 申诉内容
     */
    private String content;
    /**
     * 状态
     *
     * 枚举 {@link TODO lb_appeal_status 对应的类}
     */
    private String status;
    /**
     * 审核状态
     *
     * 枚举 {@link TODO lb_appeal_status 对应的类}
     */
    private String auditStatus;
    /**
     * 审核人
     */
    private Long auditBy;
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 驳回原因
     */
    private String rejectReason;
    /**
     * 申诉时效截止时间
     */
    private LocalDateTime appealExpireTime;


}
