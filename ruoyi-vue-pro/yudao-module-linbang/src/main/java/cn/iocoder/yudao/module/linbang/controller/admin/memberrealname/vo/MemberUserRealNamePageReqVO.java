package cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 实名认证表分页 Request VO")
@Data
public class MemberUserRealNamePageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "21056")
    private Long userId;

    @Schema(description = "用户关键词（用户编号 / 昵称 / 手机号）", example = "13800138000")
    private String userKeyword;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "身份证号")
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

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注", example = "身份证与活体核验一致")
    private String auditRemark;

    @Schema(description = "审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] auditTime;

    @Schema(description = "驳回原因", example = "证件照片不清晰")
    private String rejectReason;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
