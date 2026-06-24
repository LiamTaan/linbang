package cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 申诉 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppealRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16782")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "申诉单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申诉单号")
    private String appealNo;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32173")
    private Long orderId;

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "单元ID", example = "6416")
    private Long unitId;

    @Schema(description = "单元号")
    @ExcelProperty("单元号")
    private String unitNo;

    @Schema(description = "申诉人用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2605")
    @ExcelProperty("申诉人用户ID")
    private Long userId;

    @Schema(description = "申诉人编号", example = "LBU123456")
    @ExcelProperty("申诉人编号")
    private String userNo;

    @Schema(description = "申诉人昵称", example = "申诉人")
    @ExcelProperty("申诉人昵称")
    private String userNickname;

    @Schema(description = "申诉人手机号", example = "13800138000")
    @ExcelProperty("申诉人手机号")
    private String userMobile;

    @Schema(description = "申诉类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("申诉类型")
    private String appealType;

    @Schema(description = "申诉内容")
    @ExcelProperty("申诉内容")
    private String content;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("lb_appeal_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String status;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("lb_appeal_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String auditStatus;

    @Schema(description = "审核人")
    @ExcelProperty("审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    @ExcelProperty("审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核备注", example = "你猜")
    @ExcelProperty("审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "不喜欢")
    @ExcelProperty("驳回原因")
    private String rejectReason;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
