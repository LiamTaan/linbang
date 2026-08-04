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

    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;

    @Schema(description = "业务标识，支持订单号/单元号/异常单号/投诉单号/申诉单号/提现单号/用户编号或昵称手机号")
    private String bizKeyword;

    @Schema(description = "风险类型，按平台风控类型字典展示")
    private String riskType;

    @Schema(description = "风险等级：LOW 低风险、MEDIUM 中风险、HIGH 高风险")
    private String riskLevel;

    @Schema(description = "命中的风控规则编码")
    private String hitRuleCode;

    @Schema(description = "风险事件状态筛选：PENDING 待处理、PROCESSING 处理中、FINISHED 已完结")
    private String status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
