package cn.iocoder.yudao.module.linbang.dal.dataobject.identityverifylog;

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

@TableName("lb_identity_verify_log")
@KeySequence("lb_identity_verify_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityVerifyLogDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private Long realNameId;

    private String verifyFlowNo;

    private String verifyProvider;

    private String verifyStage;

    private String verifyStatus;

    private String requestSnapshot;

    private String responseSnapshot;

    private String failReason;
}
