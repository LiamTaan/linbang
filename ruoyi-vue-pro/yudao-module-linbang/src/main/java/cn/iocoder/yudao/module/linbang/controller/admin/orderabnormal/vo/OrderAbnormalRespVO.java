package cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 异常订单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class OrderAbnormalRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "2949")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6899")
    @ExcelProperty("订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "单元ID", example = "17812")
    @ExcelProperty("单元ID")
    private Long unitId;

    @Schema(description = "单元号")
    @ExcelProperty("单元号")
    private String unitNo;

    @Schema(description = "异常单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常单号")
    private String abnormalNo;

    @Schema(description = "异常类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("异常类型")
    private String abnormalType;

    @Schema(description = "风险等级", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("风险等级")
    private String riskLevel;

    @Schema(description = "命中规则编码")
    @ExcelProperty("命中规则编码")
    private String hitRuleCode;

    @Schema(description = "处理状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("处理状态")
    private String handleStatus;

    @Schema(description = "处理人")
    @ExcelProperty("处理人")
    private Long handleBy;

    @Schema(description = "处理时间")
    @ExcelProperty("处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "终审状态", example = "APPROVED")
    @ExcelProperty("终审状态")
    private String finalAuditStatus;

    @Schema(description = "终审人")
    @ExcelProperty("终审人")
    private Long finalAuditBy;

    @Schema(description = "终审时间")
    @ExcelProperty("终审时间")
    private LocalDateTime finalAuditTime;

    @Schema(description = "终审意见")
    @ExcelProperty("终审意见")
    private String finalAuditRemark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
