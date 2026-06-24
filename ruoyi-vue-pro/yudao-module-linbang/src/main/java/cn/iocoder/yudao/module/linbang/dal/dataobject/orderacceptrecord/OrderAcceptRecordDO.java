package cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord;

import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;

/**
 * 抢单记录 DO
 *
 * @author dawn
 */
@TableName("lb_order_accept_record")
@KeySequence("lb_order_accept_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAcceptRecordDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 单元ID
     */
    private Long unitId;
    /**
     * 服务商ID
     */
    private Long merchantId;
    /**
     * 接单时间
     */
    private LocalDateTime acceptTime;
    /**
     * 距离公里
     */
    private BigDecimal distanceKm;
    /**
     * 接单结果
     */
    private String acceptResult;
    /**
     * 备注
     */
    private String remark;

}
