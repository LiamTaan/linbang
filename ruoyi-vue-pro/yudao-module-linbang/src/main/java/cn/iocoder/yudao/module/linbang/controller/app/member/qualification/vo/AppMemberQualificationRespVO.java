package cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "用户 App - 资质认证 Response VO")
@Data
public class AppMemberQualificationRespVO {

    @Schema(description = "资质 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = OpenApiSchemaConstants.QUALIFICATION_TYPE, example = "ELECTRICIAN")
    private String qualificationType;

    @Schema(description = "资质名称", example = "电工证")
    private String qualificationName;

    @Schema(description = "资质编号", example = "CERT-2026-001")
    private String qualificationNo;

    @Schema(description = "资质附件文件 ID", example = "11")
    private Long fileId;

    @Schema(description = "补充图片凭证文件 ID 列表 JSON", example = "[11,12]")
    private String evidenceFileIdsJson;

    @Schema(description = "补充视频凭证文件 ID", example = "13")
    private Long videoFileId;

    @Schema(description = "有效开始日期", example = "2026-01-01")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate validStartDate;

    @Schema(description = "有效结束日期", example = "2028-12-31")
    @JsonFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate validEndDate;

    @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注", example = "资料齐全")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "证件照片不清晰")
    private String rejectReason;

    @Schema(description = "是否已生效优先权益")
    private Boolean priorityEnabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
