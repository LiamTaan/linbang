package cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 拆分单元新增/修改 Request VO")
@Data
public class OrderUnitSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "12141")
    private Long id;

    @Schema(description = "主订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10031")
    @NotNull(message = "主订单ID不能为空")
    private Long orderId;

    @Schema(description = "单元订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "单元订单号不能为空")
    private String unitNo;

    @Schema(description = "单元序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单元序号不能为空")
    private Integer unitSeq;

    @Schema(description = "单元标题")
    private String unitTitle;

    @Schema(description = "单元金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单元金额不能为空")
    private BigDecimal unitAmount;

    @Schema(description = "拆分模式")
    private String splitMode;

    @Schema(description = "前置单元ID", example = "31280")
    private Long prevUnitId;

    @Schema(description = "是否锁定", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否锁定不能为空")
    private Boolean isLocked;

    @Schema(description = "锁定原因", example = "不对")
    private String lockReason;

    @Schema(description = "服务商ID", example = "12215")
    private Long merchantId;

    @Schema(description = "单元状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "单元状态不能为空")
    private String status;

    @Schema(description = "接单截止时间")
    private LocalDateTime acceptDeadlineTime;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

}