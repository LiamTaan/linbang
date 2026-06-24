package cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 订单匹配记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class OrderMatchRecordRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "27554")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6003")
    private Long orderId;

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "单元ID", example = "22484")
    private Long unitId;

    @Schema(description = "单元号")
    @ExcelProperty("单元号")
    private String unitNo;

    @Schema(description = "服务商ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16471")
    @ExcelProperty("服务商ID")
    private Long merchantId;

    @Schema(description = "服务商名称")
    @ExcelProperty("服务商名称")
    private String merchantName;

    @Schema(description = "联系人")
    @ExcelProperty("联系人")
    private String merchantContactName;

    @Schema(description = "联系手机")
    @ExcelProperty("联系手机")
    private String merchantContactMobile;

    @Schema(description = "命中规则编码")
    @ExcelProperty("命中规则编码")
    private String matchRuleCode;

    @Schema(description = "匹配分值")
    @ExcelProperty("匹配分值")
    private BigDecimal matchScore;

    @Schema(description = "距离公里")
    @ExcelProperty("距离公里")
    private BigDecimal distanceKm;

    @Schema(description = "推送时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("推送时间")
    private LocalDateTime pushTime;

    @Schema(description = "接单截止时间")
    @ExcelProperty("接单截止时间")
    private LocalDateTime acceptDeadlineTime;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态")
    private String status;

}
