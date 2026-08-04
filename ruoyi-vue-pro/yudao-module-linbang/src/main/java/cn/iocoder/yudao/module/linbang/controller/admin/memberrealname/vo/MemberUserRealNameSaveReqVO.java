package cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 实名认证表新增/修改 Request VO")
@Data
public class MemberUserRealNameSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4138")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21056")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "真实姓名不能为空")
    private String realName;

    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "身份证号不能为空")
    private String idCardNo;

    @Schema(description = "身份证正面文件ID", example = "5399")
    private Long idCardFrontFileId;

    @Schema(description = "身份证反面文件ID", example = "28105")
    private Long idCardBackFileId;

    @Schema(description = "手持证件文件ID", example = "8498")
    private Long holdCardFileId;

    @Schema(description = "活体结果")
    private String livenessResult;

    @Schema(description = "人脸核验结果")
    private String faceVerifyResult;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotEmpty(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "审核备注", example = "材料清晰，信息一致")
    private String auditRemark;

    @Schema(description = "审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "驳回原因", example = "身份证照片不清晰")
    private String rejectReason;

}
