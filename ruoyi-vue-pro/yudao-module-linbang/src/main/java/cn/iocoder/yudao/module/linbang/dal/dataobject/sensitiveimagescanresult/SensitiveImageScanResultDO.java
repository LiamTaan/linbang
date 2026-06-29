package cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveimagescanresult;

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

@TableName("lb_sensitive_image_scan_result")
@KeySequence("lb_sensitive_image_scan_result_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveImageScanResultDO extends BaseDO {

    @TableId
    private Long id;

    private String sceneType;

    private Long userId;

    private String bizType;

    private Long bizId;

    private Long fileId;

    private String sourceFileUrl;

    private String maskedFileUrl;

    private String ocrText;

    private String qrContent;

    private String hitWords;

    private String scanStatus;

    private String failureReason;
}
