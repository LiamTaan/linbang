package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 晒单悬赏 Response VO")
@Data
public class ShowcaseRewardRespVO {

    @Schema(description = "申请 ID")
    private Long id;

    @Schema(description = "服务商 ID")
    private Long merchantId;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "凭证文件 JSON")
    private String evidenceFileIdsJson;

    @Schema(description = "审核状态")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "是否已生效优先权")
    private Boolean priorityEnabled;

    @Schema(description = "生效开始时间")
    private LocalDateTime effectiveStartTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime effectiveEndTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
