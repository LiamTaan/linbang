package cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 抢单记录分页 Request VO")
@Data
public class OrderAcceptRecordPageReqVO extends PageParam {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "单元号")
    private String unitNo;

    @Schema(description = "服务商ID", example = "2654")
    private Long merchantId;

    @Schema(description = "接单时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] acceptTime;

    @Schema(description = "距离公里")
    private BigDecimal distanceKm;

    @Schema(description = "接单结果")
    private String acceptResult;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}
