package cn.iocoder.yudao.module.linbang.dal.dataobject.memberpoint;

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
 * 邻里用户积分记录 DO
 */
@TableName("lb_member_point_record")
@KeySequence("lb_member_point_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPointRecordDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 邻里用户 ID，对应 lb_user.id
     */
    private Long userId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务编号
     */
    private String bizId;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 本次变动积分
     */
    private Integer point;

    /**
     * 变动后总积分
     */
    private Integer totalPoint;
}
