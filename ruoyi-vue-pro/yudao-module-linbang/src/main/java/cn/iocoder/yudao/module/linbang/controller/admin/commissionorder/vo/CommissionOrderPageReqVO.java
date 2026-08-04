package cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "管理后台 - 佣金订单 分页查询 Request VO")
public class CommissionOrderPageReqVO extends PageParam {

    @Schema(description = "推广员筛选关键词，可匹配用户编号、昵称或手机号")
    private String promoterKeyword;

    @Schema(description = "用户筛选关键词，可匹配用户编号、昵称或手机号")
    private String userKeyword;

    @Schema(description = "佣金类型，按推广佣金字典展示，当前常见值 ORDER 表示订单佣金")
    private String commissionType;

    @Schema(description = "佣金状态筛选：PENDING 待结算、SETTLED 已结算、REFUNDED 已退款冲正")
    private String status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
