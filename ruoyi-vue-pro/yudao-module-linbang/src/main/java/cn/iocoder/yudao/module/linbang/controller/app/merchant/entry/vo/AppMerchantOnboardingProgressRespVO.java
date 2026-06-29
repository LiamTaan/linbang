package cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 服务商入驻进度 Response VO")
@Data
public class AppMerchantOnboardingProgressRespVO {

    @Schema(description = "入驻申请 ID", example = "1")
    private Long entryId;

    @Schema(description = "初审状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "APPROVED")
    private String firstAuditStatus;

    @Schema(description = "终审状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "PENDING")
    private String finalAuditStatus;

    @Schema(description = "统一进度状态：PENDING_FIRST_AUDIT、PENDING_FINAL_AUDIT、APPROVED_WAIT_BANK_CARD、APPROVED_ENABLED、REJECTED", example = "APPROVED_WAIT_BANK_CARD")
    private String progressStatus;

    @Schema(description = "当前阶段名称", example = "终审通过，待绑定银行卡")
    private String currentStageName;

    @Schema(description = "当前阶段时间")
    private LocalDateTime currentStageTime;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "是否已开通接单", example = "false")
    private Boolean acceptEnabled;

    @Schema(description = "是否因未绑卡阻塞", example = "true")
    private Boolean blockedByBankCard;

    @Schema(description = "当前阻塞原因")
    private String blockedReason;

    @Schema(description = "下一步动作提示", example = "请先绑定有效银行卡后开通接单权限")
    private String nextAction;
}
