package cn.iocoder.yudao.module.linbang.dal.dataobject.promotecontent;

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

@TableName("lb_promote_content")
@KeySequence("lb_promote_content_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoteContentDO extends BaseDO {

    @TableId
    private Long id;

    private Long promoterId;

    private Long userId;

    private String title;

    private String content;

    private String imageUrls;

    private String status;

    private String systemAuditResult;

    private String systemAuditRemark;

    private LocalDateTime systemAuditTime;

    private String manualAuditResult;

    private String manualAuditRemark;

    private Long manualAuditBy;

    private LocalDateTime manualAuditTime;

    private String rejectReason;

    private String offlineReason;
}
