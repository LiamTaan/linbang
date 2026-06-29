package cn.iocoder.yudao.module.linbang.controller.admin.orderdividerecord.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 分账明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDivideRecordPageReqVO extends PageParam {

    @Schema(description = "订单 ID", example = "1001")
    private Long orderId;

    @Schema(description = "单元 ID", example = "2001")
    private Long unitId;

    @Schema(description = "分账去向类型", example = "MERCHANT")
    private String targetType;

    @Schema(description = "结算状态", example = "PENDING")
    private String settleStatus;

    @Schema(description = "城市等级", example = "FIRST_TIER")
    private String cityLevel;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
