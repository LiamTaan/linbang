package cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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

    @Schema(description = "活体结果", example = "PASS")
    private String livenessResult;

    @Schema(description = "人脸核验结果", example = "PASS")
    private String faceVerifyResult;

    @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "提交时间")
    private LocalDateTime createTime;
}
