package cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 拆分单元 Response VO")
@Data
@ExcelIgnoreUnannotated
public class OrderUnitRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12141")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "主订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10031")
    @ExcelProperty("主订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "单元订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单元订单号")
    private String unitNo;

    @Schema(description = "单元序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单元序号")
    private Integer unitSeq;

    @Schema(description = "单元标题")
    @ExcelProperty("单元标题")
    private String unitTitle;

    @Schema(description = "单元金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("单元金额")
    private BigDecimal unitAmount;

    @Schema(description = "拆分模式")
    @ExcelProperty("拆分模式")
    private String splitMode;

    @Schema(description = "前置单元ID", example = "31280")
    @ExcelProperty("前置单元ID")
    private Long prevUnitId;

    @Schema(description = "前置单元号")
    @ExcelProperty("前置单元号")
    private String prevUnitNo;

    @Schema(description = "是否锁定", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否锁定")
    private Boolean isLocked;

    @Schema(description = "锁定原因", example = "不对")
    @ExcelProperty("锁定原因")
    private String lockReason;

    @Schema(description = "服务商ID", example = "12215")
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

    @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING_ACCEPT")
    @ExcelProperty(value = "单元状态", converter = DictConvert.class)
    @DictFormat("lb_order_unit_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String status;

    @Schema(description = "接单截止时间")
    @ExcelProperty("接单截止时间")
    private LocalDateTime acceptDeadlineTime;

    @Schema(description = "完成时间")
    @ExcelProperty("完成时间")
    private LocalDateTime finishTime;

    @Schema(description = OpenApiSchemaConstants.ORDER_VERIFY_STATUS, example = "PENDING")
    @ExcelProperty("核销状态")
    private String verifyStatus;

    @Schema(description = "核销码")
    @ExcelProperty("核销码")
    private String verifyCode;

    @Schema(description = "核销时间")
    @ExcelProperty("核销时间")
    private LocalDateTime verifyTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
