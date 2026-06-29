package cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户 App - 证件豁免申请 Request VO")
@Data
public class AppCertExemptionCreateReqVO {

    @Schema(description = "豁免类型：ID_CARD 身份证、BUSINESS_LICENSE 营业执照、INDUSTRY_CERT 行业资质、INSURANCE_POLICY 保险保单", requiredMode = Schema.RequiredMode.REQUIRED, example = "INSURANCE_POLICY")
    @NotBlank(message = "豁免类型不能为空")
    private String exemptionType;

    @Schema(description = "关联资质 ID", example = "1001")
    private Long qualificationId;

    @Schema(description = "申请原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "保险保单续保中，请先临时放行")
    @NotBlank(message = "申请原因不能为空")
    private String reason;

    @Schema(description = "附件文件 ID 列表 JSON", example = "[11,12]")
    private String attachmentFileIdsJson;

    @Schema(description = "申请生效开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-06-28 10:00:00")
    @NotNull(message = "申请生效开始时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime effectiveStartTime;

    @Schema(description = "申请生效结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-05 23:59:59")
    @NotNull(message = "申请生效结束时间不能为空")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime effectiveEndTime;
}
