package cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 实名认证发起核验 Response VO")
@Data
public class AppMemberRealNameStartVerifyRespVO {

    @Schema(description = "实名认证记录 ID", example = "1")
    private Long realNameId;

    @Schema(description = "核验流水号", example = "VERIFY_202606280001")
    private String verifyFlowNo;

    @Schema(description = "核验供应商", example = "LOCAL_STUB")
    private String verifyProvider;

    @Schema(description = "核验阶段", example = "SUBMITTED")
    private String verifyStage;

    @Schema(description = "核验状态：PENDING 待核验、PASS 通过、FAIL 失败", example = "PENDING")
    private String verifyStatus;

    @Schema(description = "核验发起时间")
    private LocalDateTime verifyStartedTime;
}
