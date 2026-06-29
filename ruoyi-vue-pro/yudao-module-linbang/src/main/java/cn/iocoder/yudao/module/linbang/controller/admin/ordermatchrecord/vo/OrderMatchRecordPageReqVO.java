package cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 订单匹配记录分页 Request VO")
@Data
public class OrderMatchRecordPageReqVO extends PageParam {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "单元号")
    private String unitNo;

    @Schema(description = "服务商ID", example = "16471")
    private Long merchantId;

    @Schema(description = "命中规则编码")
    private String matchRuleCode;

    @Schema(description = "匹配分值")
    private BigDecimal matchScore;

    @Schema(description = "距离公里")
    private BigDecimal distanceKm;

    @Schema(description = "阶段号", example = "1")
    private Integer stageNo;

    @Schema(description = "推送批次号", example = "1")
    private Integer pushBatchNo;

    @Schema(description = "优先层", example = "PLATFORM_CLOTHING")
    private String priorityLayer;

    @Schema(description = "是否命中优先池")
    private Boolean priorityPoolFlag;

    @Schema(description = "品类命中等级", example = "EXACT")
    private String categoryMatchLevel;

    @Schema(description = "推送时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] pushTime;

    @Schema(description = "接单截止时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] acceptDeadlineTime;

    @Schema(description = "过期时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] expiredTime;

    @Schema(description = OpenApiSchemaConstants.MATCH_RECORD_STATUS, example = "PUSHED")
    private String status;

    @Schema(description = "最终结果", example = "ACCEPTED")
    private String finalResult;

}
