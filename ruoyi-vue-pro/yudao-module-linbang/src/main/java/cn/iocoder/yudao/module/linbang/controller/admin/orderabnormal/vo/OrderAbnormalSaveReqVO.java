package cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 异常订单新增/修改 Request VO")
@Data
public class OrderAbnormalSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "2949")
    private Long id;

    @Schema(description = "订单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6899")
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @Schema(description = "单元ID", example = "17812")
    private Long unitId;

    @Schema(description = "异常单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "异常单号不能为空")
    private String abnormalNo;

    @Schema(description = "异常类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "异常类型不能为空")
    private String abnormalType;

    @Schema(description = "风险等级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "风险等级不能为空")
    private String riskLevel;

    @Schema(description = "命中规则编码")
    private String hitRuleCode;

    @Schema(description = "处理状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "处理状态不能为空")
    private String handleStatus;

    @Schema(description = "处理人")
    private Long handleBy;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

}