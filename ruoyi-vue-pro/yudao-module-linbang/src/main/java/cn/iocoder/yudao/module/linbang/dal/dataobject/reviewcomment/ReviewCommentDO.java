package cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment;

import lombok.*;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;

/**
 * 评价 DO
 *
 * @author dawn
 */
@TableName("lb_review")
@KeySequence("lb_review_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommentDO {

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
     * 评价发起人
     */
    private Long fromUserId;
    /**
     * 评价目标用户
     */
    private Long toUserId;
    /**
     * 星级
     */
    private Integer starLevel;
    /**
     * 评价内容
     */
    private String content;
    /**
     * 是否自动评价
     */
    private Boolean isAutoReview;
    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
