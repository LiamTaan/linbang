package cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 分账规则分页 Request VO")
@Data
public class DivideRulePageReqVO extends PageParam {

    @Schema(description = "规则名称", example = "赵六")
    private String ruleName;

    @Schema(description = "城市等级")
    private String cityLevel;

    @Schema(description = "类目ID", example = "17167")
    private Long categoryId;

    @Schema(description = "服务商比例")
    private BigDecimal merchantRate;

    @Schema(description = "平台比例")
    private BigDecimal platformRate;

    @Schema(description = "合作商比例")
    private BigDecimal partnerRate;

    @Schema(description = "推广员比例")
    private BigDecimal promoterRate;

    @Schema(description = "个税代扣比例")
    private BigDecimal taxWithholdRate;

    @Schema(description = "最低提现金额")
    private BigDecimal minWithdrawAmount;

    @Schema(description = "状态", example = "1")
    private String status;

    @Schema(description = "生效时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] effectiveTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}