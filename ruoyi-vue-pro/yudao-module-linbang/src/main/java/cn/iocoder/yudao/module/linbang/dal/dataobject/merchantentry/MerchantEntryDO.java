package cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 服务商入驻申请表 DO
 *
 * @author dawn
 */
@TableName("lb_merchant_entry")
@KeySequence("lb_merchant_entry_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantEntryDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 服务商ID
     */
    private Long merchantId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 入驻单号
     */
    private String entryNo;
    /**
     * 区域编码
     */
    private String regionCode;
    /**
     * 初审状态
     */
    private String firstAuditStatus;
    /**
     * 初审人
     */
    private Long firstAuditBy;
    /**
     * 初审时间
     */
    private LocalDateTime firstAuditTime;
    /**
     * 终审状态
     */
    private String finalAuditStatus;
    /**
     * 终审人
     */
    private Long finalAuditBy;
    /**
     * 终审时间
     */
    private LocalDateTime finalAuditTime;
    /**
     * 状态
     */
    private String status;
    /**
     * 入驻进度状态
     */
    private String progressStatus;
    /**
     * 当前阶段名称
     */
    private String currentStageName;
    /**
     * 当前阶段时间
     */
    private LocalDateTime currentStageTime;
    /**
     * 驳回原因
     */
    private String rejectReason;
    /**
     * 当前阻塞原因
     */
    private String onboardingBlockedReason;
    /**
     * 是否已开通接单
     */
    private Boolean acceptEnabled;
    /**
     * 是否必须绑卡
     */
    private Boolean bankCardRequired;
    /**
     * 备注
     */
    private String remark;
}
