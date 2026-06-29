package cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 服务商晒单悬赏 Response VO")
@Data
public class AppShowcaseRewardRespVO {

    @Schema(description = "申请 ID", example = "1")
    private Long id;

    @Schema(description = "服务商 ID", example = "1")
    private Long merchantId;

    @Schema(description = "案例标题")
    private String title;

    @Schema(description = "案例说明")
    private String description;

    @Schema(description = "凭证文件 ID 列表 JSON")
    private String evidenceFileIdsJson;

    @Schema(description = "审核状态", example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "是否已生效优先权")
    private Boolean priorityEnabled;

    @Schema(description = "优先权开始时间")
    private LocalDateTime effectiveStartTime;

    @Schema(description = "优先权结束时间")
    private LocalDateTime effectiveEndTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
