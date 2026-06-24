package cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 异常订单分页 Request VO")
@Data
public class OrderAbnormalPageReqVO extends PageParam {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "单元号")
    private String unitNo;

    @Schema(description = "异常单号")
    private String abnormalNo;

    @Schema(description = "异常类型", example = "2")
    private String abnormalType;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "命中规则编码")
    private String hitRuleCode;

    @Schema(description = "处理状态", example = "1")
    private String handleStatus;

    @Schema(description = "处理人")
    private Long handleBy;

    @Schema(description = "处理时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] handleTime;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
