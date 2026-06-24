package cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 抢单记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class OrderAcceptRecordRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "24409")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13466")
    private Long orderId;

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "单元ID", example = "9636")
    private Long unitId;

    @Schema(description = "单元号")
    @ExcelProperty("单元号")
    private String unitNo;

    @Schema(description = "服务商ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2654")
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

    @Schema(description = "接单时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("接单时间")
    private LocalDateTime acceptTime;

    @Schema(description = "距离公里")
    @ExcelProperty("距离公里")
    private BigDecimal distanceKm;

    @Schema(description = "接单结果", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("接单结果")
    private String acceptResult;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

}
