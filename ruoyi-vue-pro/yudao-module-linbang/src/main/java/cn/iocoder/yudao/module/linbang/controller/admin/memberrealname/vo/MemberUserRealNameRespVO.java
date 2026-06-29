package cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 实名认证表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MemberUserRealNameRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4138")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "21056")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "用户编号", example = "LBU123456")
    @ExcelProperty("用户编号")
    private String userNo;

    @Schema(description = "用户昵称", example = "邻里用户")
    @ExcelProperty("用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号", example = "13800138000")
    @ExcelProperty("用户手机号")
    private String userMobile;

    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @ExcelProperty("真实姓名")
    private String realName;

    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("身份证号")
    private String idCardNo;

    @Schema(description = "身份证正面文件ID", example = "5399")
    @ExcelProperty("身份证正面文件ID")
    private Long idCardFrontFileId;

    @Schema(description = "身份证反面文件ID", example = "28105")
    @ExcelProperty("身份证反面文件ID")
    private Long idCardBackFileId;

    @Schema(description = "手持证件文件ID", example = "8498")
    @ExcelProperty("手持证件文件ID")
    private Long holdCardFileId;

    @Schema(description = "活体结果")
    @ExcelProperty("活体结果")
    private String livenessResult;

    @Schema(description = "人脸核验结果")
    @ExcelProperty("人脸核验结果")
    private String faceVerifyResult;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("lb_audit_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String auditStatus;

    @Schema(description = "审核备注", example = "??")
    @ExcelProperty("审核备注")
    private String auditRemark;

    @Schema(description = "审核人")
    @ExcelProperty("审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    @ExcelProperty("审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "驳回原因", example = "??")
    @ExcelProperty("驳回原因")
    private String rejectReason;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
