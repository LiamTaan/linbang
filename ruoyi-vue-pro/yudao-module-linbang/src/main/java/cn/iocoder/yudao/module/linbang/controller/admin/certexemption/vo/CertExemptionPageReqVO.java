package cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 证件豁免申请分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CertExemptionPageReqVO extends PageParam {

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "用户关键词（用户编号 / 昵称 / 手机号）", example = "13800138000")
    private String userKeyword;

    @Schema(description = "服务商 ID", example = "1001")
    private Long merchantId;

    @Schema(description = "豁免类型", example = "INSURANCE_POLICY")
    private String exemptionType;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime[] createTime;
}
