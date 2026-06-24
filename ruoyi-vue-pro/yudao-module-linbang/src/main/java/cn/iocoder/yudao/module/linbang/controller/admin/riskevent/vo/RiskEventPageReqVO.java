package cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 风险事件分页 Request VO")
@Data
public class RiskEventPageReqVO extends PageParam {

    private String bizType;

    @Schema(description = "业务标识，支持订单号/单元号/异常单号/投诉单号/申诉单号/提现单号/用户编号或昵称手机号")
    private String bizKeyword;

    private String riskType;

    private String riskLevel;

    private String hitRuleCode;

    private String status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
