package cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("lb_order_operate_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOperateLogDO {

    @TableId
    private Long id;

    private Long orderId;

    private Long unitId;

    private String operateType;

    private String operateRole;

    private Long operateBy;

    private String beforeStatus;

    private String afterStatus;

    private String remark;

    private LocalDateTime operateTime;

}
