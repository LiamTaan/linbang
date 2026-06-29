package cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 悬赏参与记录 Response VO")
@Data
public class AppRewardOrderParticipationRespVO {

    @Schema(description = "参与记录 ID", example = "1")
    private Long id;

    @Schema(description = "悬赏 ID", example = "1001")
    private Long rewardOrderId;

    @Schema(description = "悬赏标题")
    private String rewardTitle;

    @Schema(description = "悬赏发布用户 ID", example = "2001")
    private Long rewardUserId;

    @Schema(description = "参与用户 ID", example = "3001")
    private Long participantUserId;

    @Schema(description = "参与用户昵称")
    private String participantNickname;

    @Schema(description = "参与用户手机号")
    private String participantMobile;

    @Schema(description = "参与状态 PENDING/ACCEPTED/REJECTED/CANCELLED", example = "PENDING")
    private String status;

    @Schema(description = "参与说明")
    private String participateRemark;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
