package cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 证件豁免申请 Response VO")
@Data
public class AppCertExemptionRespVO {

    @Schema(description = "豁免申请 ID", example = "1")
    private Long id;

    @Schema(description = "豁免类型", example = "INSURANCE_POLICY")
    private String exemptionType;

    @Schema(description = "关联资质 ID", example = "1001")
    private Long qualificationId;

    @Schema(description = "申请原因")
    private String reason;

    @Schema(description = "附件文件 ID 列表 JSON", example = "[11,12]")
    private String attachmentFileIdsJson;

    @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "PENDING")
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
