package cn.iocoder.yudao.module.linbang.dal.dataobject.complaint;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 投诉 DO
 *
 * @author dawn
 */
@TableName("lb_complaint")
@KeySequence("lb_complaint_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 投诉单号
     */
    private String complaintNo;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 单元ID
     */
    private Long unitId;
    /**
     * 投诉人用户ID
     */
    private Long complainantUserId;
    /**
     * 被投诉人用户ID
     */
    private Long respondentUserId;
    /**
     * 投诉类型
     */
    private String complaintType;
    /**
     * 投诉内容
     */
    private String content;
    /**
     * 状态
     *
     * 枚举 {@link TODO lb_complaint_status 对应的类}
     */
    private String status;
    /**
     * 处理人
     */
    private Long handleBy;
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    /**
     * 处理结果
     */
    private String resultDesc;


}