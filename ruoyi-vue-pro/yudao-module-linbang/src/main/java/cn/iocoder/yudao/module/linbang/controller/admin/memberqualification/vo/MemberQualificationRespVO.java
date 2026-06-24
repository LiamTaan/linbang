package cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户资质 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MemberQualificationRespVO {

    @Schema(description = "资质 ID", example = "1")
    @ExcelProperty("资质 ID")
    private Long id;

    @Schema(description = "用户 ID", example = "1")
    @ExcelProperty("用户 ID")
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

    @Schema(description = "资质类型", example = "ELECTRICIAN")
    @ExcelProperty("资质类型")
    private String qualificationType;

    @Schema(description = "资质名称", example = "电工证")
    @ExcelProperty("资质名称")
    private String qualificationName;

    @Schema(description = "资质编号", example = "CERT-2026-001")
    @ExcelProperty("资质编号")
    private String qualificationNo;

    @Schema(description = "文件 ID", example = "11")
    @ExcelProperty("文件 ID")
    private Long fileId;

    @Schema(description = "有效开始日期", example = "2026-01-01")
    @ExcelProperty("有效开始日期")
    private LocalDate validStartDate;

    @Schema(description = "有效结束日期", example = "2028-12-31")
    @ExcelProperty("有效结束日期")
    private LocalDate validEndDate;

    @Schema(description = "审核状态", example = "PENDING")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("lb_audit_status")
    private String auditStatus;

    @Schema(description = "审核备注")
    @ExcelProperty("审核备注")
    private String auditRemark;

    @Schema(description = "审核人", example = "1")
    @ExcelProperty("审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    @ExcelProperty("审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "驳回原因")
    @ExcelProperty("驳回原因")
    private String rejectReason;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
