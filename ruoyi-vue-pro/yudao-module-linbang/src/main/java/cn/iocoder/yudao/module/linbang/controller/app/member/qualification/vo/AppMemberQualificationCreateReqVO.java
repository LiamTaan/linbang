package cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "用户 App - 提交资质认证 Request VO")
@Data
public class AppMemberQualificationCreateReqVO {

    @Schema(description = "资质类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "ELECTRICIAN")
    @NotBlank(message = "资质类型不能为空")
    private String qualificationType;

    @Schema(description = "资质名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "电工证")
    @NotBlank(message = "资质名称不能为空")
    private String qualificationName;

    @Schema(description = "资质编号", example = "CERT-2026-001")
    private String qualificationNo;

    @Schema(description = "资质附件文件 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    @NotNull(message = "资质附件不能为空")
    private Long fileId;

    @Schema(description = "有效开始日期", example = "2026-01-01")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate validStartDate;

    @Schema(description = "有效结束日期", example = "2028-12-31")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate validEndDate;

}
