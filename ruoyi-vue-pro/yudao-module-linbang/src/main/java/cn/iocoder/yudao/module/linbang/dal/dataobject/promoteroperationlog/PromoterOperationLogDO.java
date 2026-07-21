package cn.iocoder.yudao.module.linbang.dal.dataobject.promoteroperationlog;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@TableName("lb_promoter_operation_log")
@KeySequence("lb_promoter_operation_log_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PromoterOperationLogDO extends BaseDO {

    @TableId
    private Long id;
    private Long promoterId;
    private Long userId;
    private String bizType;
    private Long bizId;
    private String operationType;
    private String beforeStatus;
    private String afterStatus;
    private String remark;
}
