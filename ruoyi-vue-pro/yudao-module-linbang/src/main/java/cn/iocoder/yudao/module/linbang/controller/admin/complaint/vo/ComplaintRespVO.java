package cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 投诉 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ComplaintRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1039")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "投诉单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("投诉单号")
    private String complaintNo;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "14645")
    private Long orderId;

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "单元ID", example = "14192")
    private Long unitId;

    @Schema(description = "单元号")
    @ExcelProperty("单元号")
    private String unitNo;

    @Schema(description = "投诉人用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20578")
    @ExcelProperty("投诉人用户ID")
    private Long complainantUserId;

    @Schema(description = "投诉人编号", example = "LBU123456")
    @ExcelProperty("投诉人编号")
    private String complainantUserNo;

    @Schema(description = "投诉人昵称", example = "投诉人")
    @ExcelProperty("投诉人昵称")
    private String complainantUserNickname;

    @Schema(description = "投诉人手机号", example = "13800138000")
    @ExcelProperty("投诉人手机号")
    private String complainantUserMobile;

    @Schema(description = "被投诉人用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17431")
    @ExcelProperty("被投诉人用户ID")
    private Long respondentUserId;

    @Schema(description = "被投诉人编号", example = "LBU654321")
    @ExcelProperty("被投诉人编号")
    private String respondentUserNo;

    @Schema(description = "被投诉人昵称", example = "被投诉人")
    @ExcelProperty("被投诉人昵称")
    private String respondentUserNickname;

    @Schema(description = "被投诉人手机号", example = "13800138001")
    @ExcelProperty("被投诉人手机号")
    private String respondentUserMobile;

    @Schema(description = "投诉类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("投诉类型")
    private String complaintType;

    @Schema(description = "投诉内容")
    @ExcelProperty("投诉内容")
    private String content;

    @Schema(description = OpenApiSchemaConstants.COMPLAINT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("lb_complaint_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String status;

    @Schema(description = "处理人")
    @ExcelProperty("处理人")
    private Long handleBy;

    @Schema(description = "处理时间")
    @ExcelProperty("处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理结果")
    @ExcelProperty("处理结果")
    private String resultDesc;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
