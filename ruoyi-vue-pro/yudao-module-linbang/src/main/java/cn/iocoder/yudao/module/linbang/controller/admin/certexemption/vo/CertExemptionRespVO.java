package cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 证件豁免申请 Response VO")
@Data
public class CertExemptionRespVO {

    @Schema(description = "豁免申请 ID", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "用户编号")
    private String userNo;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号")
    private String userMobile;

    @Schema(description = "服务商 ID", example = "1001")
    private Long merchantId;

    @Schema(description = "服务商名称")
    private String merchantName;

    @Schema(description = "关联资质 ID", example = "2001")
    private Long qualificationId;

    @Schema(description = "资质名称")
    private String qualificationName;

    @Schema(description = "豁免类型", example = "INSURANCE_POLICY")
    private String exemptionType;

    @Schema(description = "申请原因")
    private String reason;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "生效开始时间")
    private LocalDateTime effectiveStartTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime effectiveEndTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
