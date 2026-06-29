package cn.iocoder.yudao.module.linbang.dal.dataobject.usersensitivecustomword;

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

@TableName("lb_user_sensitive_custom_word")
@KeySequence("lb_user_sensitive_custom_word_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSensitiveCustomWordDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String word;

    private String sceneType;

    private String status;

    private String remark;
}
