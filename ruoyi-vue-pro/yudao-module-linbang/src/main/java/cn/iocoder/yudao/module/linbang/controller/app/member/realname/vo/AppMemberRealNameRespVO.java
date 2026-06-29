package cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 实名认证 Response VO")
@Data
public class AppMemberRealNameRespVO {

    @Schema(description = "记录 ID", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "真实姓名", example = "Alice Zhang")
    private String realName;

    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCardNo;

    @Schema(description = "身份证正面文件 ID", example = "1")
    private Long idCardFrontFileId;

    @Schema(description = "身份证反面文件 ID", example = "2")
    private Long idCardBackFileId;

    @Schema(description = "手持证件文件 ID", example = "3")
    private Long holdCardFileId;

    @Schema(description = "手持身份证视频文件 ID", example = "4")
    private Long holdCardVideoFileId;

    @Schema(description = "身份证有效期开始日期", example = "2020-01-01")
    private LocalDate idCardValidFrom;

    @Schema(description = "身份证有效期结束日期", example = "2030-01-01")
    private LocalDate idCardValidEnd;

    @Schema(description = "活体结果", example = "PASS")
    private String livenessResult;

    @Schema(description = "人脸核验结果", example = "PASS")
    private String faceVerifyResult;

    @Schema(description = "微信实名关联状态：UNBOUND 未绑定、PENDING 待校验、MATCHED 已匹配、UNMATCHED 未匹配、FAILED 校验失败", example = "MATCHED")
    private String wechatRealNameStatus;

    @Schema(description = "支付宝实名关联状态：UNBOUND 未绑定、PENDING 待校验、MATCHED 已匹配、UNMATCHED 未匹配、FAILED 校验失败", example = "UNBOUND")
    private String alipayRealNameStatus;

    @Schema(description = "核验供应商", example = "LOCAL_STUB")
    private String verifyProvider;

    @Schema(description = "核验流水号", example = "VERIFY_123")
    private String verifyFlowNo;

    @Schema(description = "核验发起时间")
    private LocalDateTime verifyStartedTime;

    @Schema(description = "核验完成时间")
    private LocalDateTime verifyCompletedTime;

    @Schema(description = "核验失败原因")
    private String verifyFailReason;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "提交时间")
    private LocalDateTime createTime;
}
