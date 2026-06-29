package cn.iocoder.yudao.module.linbang.dal.dataobject.sensitivehitlog;

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

@TableName("lb_sensitive_hit_log")
@KeySequence("lb_sensitive_hit_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveHitLogDO extends BaseDO {

    @TableId
    private Long id;

    private String sceneType;

    private Long userId;

    private String bizType;

    private Long bizId;

    private Long wordId;

    private String hitWord;

    private String blockLevel;

    private String strategy;

    private String contentType;

    private Long fileId;

    private String ocrTextSnapshot;

    private String qrContentSnapshot;

    private String manualAuditResult;

    private String contentSnapshot;
}
