package cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 实名认证进度 Response VO")
@Data
public class AppMemberRealNameProgressRespVO {

    @Schema(description = "实名认证记录 ID", example = "1")
    private Long realNameId;

    @Schema(description = "实名认证审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "PENDING")
    private String auditStatus;

    @Schema(description = "活体状态：PENDING 待校验、PASS 通过、FAIL 失败", example = "PASS")
    private String livenessStatus;

    @Schema(description = "人脸核验状态：PENDING 待校验、PASS 通过、FAIL 失败", example = "PASS")
    private String faceVerifyStatus;

    @Schema(description = "微信实名关联状态：UNBOUND 未绑定、PENDING 待校验、MATCHED 已匹配、UNMATCHED 未匹配、FAILED 校验失败", example = "MATCHED")
    private String wechatRealNameStatus;

    @Schema(description = "支付宝实名关联状态：UNBOUND 未绑定、PENDING 待校验、MATCHED 已匹配、UNMATCHED 未匹配、FAILED 校验失败", example = "UNBOUND")
    private String alipayRealNameStatus;

    @Schema(description = "当前进度标题", example = "实名认证资料已提交")
    private String currentStepTitle;

    @Schema(description = "当前进度说明", example = "平台正在校验您的身份证、人脸和活体信息")
    private String currentStepDesc;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "核验失败原因")
    private String verifyFailReason;

    @Schema(description = "是否可重新提交", example = "true")
    private Boolean canResubmit;

    @Schema(description = "核验发起时间")
    private LocalDateTime verifyStartedTime;

    @Schema(description = "核验完成时间")
    private LocalDateTime verifyCompletedTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;
}
