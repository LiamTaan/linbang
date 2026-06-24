package cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord;

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

/**
 * 信用记录 DO
 *
 * @author Codex
 */
@TableName("lb_credit_record")
@KeySequence("lb_credit_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditRecordDO extends BaseDO {

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
     * 服务商ID
     */
    private Long merchantId;
    /**
     * 信用规则ID
     */
    private Long ruleId;
    /**
     * 规则编码
     */
    private String ruleCode;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 分值变动
     */
    private Integer scoreChange;
    /**
     * 变动前分值
     */
    private Integer beforeScore;
    /**
     * 变动后分值
     */
    private Integer afterScore;
    /**
     * 触发类型
     */
    private String triggerType;
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 业务ID
     */
    private Long bizId;
    /**
     * 备注
     */
    private String remark;

}
