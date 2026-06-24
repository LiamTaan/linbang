package cn.iocoder.yudao.module.linbang.dal.dataobject.dividerule;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 分账规则 DO
 *
 * @author dawn
 */
@TableName("lb_divide_rule")
@KeySequence("lb_divide_rule_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DivideRuleDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 城市等级
     */
    private String cityLevel;
    /**
     * 类目ID
     */
    private Long categoryId;
    /**
     * 服务商比例
     */
    private BigDecimal merchantRate;
    /**
     * 平台比例
     */
    private BigDecimal platformRate;
    /**
     * 合作商比例
     */
    private BigDecimal partnerRate;
    /**
     * 推广员比例
     */
    private BigDecimal promoterRate;
    /**
     * 个税代扣比例
     */
    private BigDecimal taxWithholdRate;
    /**
     * 最低提现金额
     */
    private BigDecimal minWithdrawAmount;
    /**
     * 状态
     */
    private String status;
    /**
     * 生效时间
     */
    private LocalDateTime effectiveTime;


}