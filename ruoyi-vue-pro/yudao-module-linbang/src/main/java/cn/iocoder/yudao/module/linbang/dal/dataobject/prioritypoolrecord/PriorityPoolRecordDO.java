package cn.iocoder.yudao.module.linbang.dal.dataobject.prioritypoolrecord;

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

/**
 * 优先池记录 DO
 */
@TableName("lb_priority_pool_record")
@KeySequence("lb_priority_pool_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityPoolRecordDO extends BaseDO {

    @TableId
    private Long id;

    private Long merchantId;

    private Long userId;

    private String status;

    private String reasonCode;

    private String reasonRemark;

    private Boolean currentFlag;

    private LocalDateTime effectiveTime;

    private LocalDateTime expireTime;
}
