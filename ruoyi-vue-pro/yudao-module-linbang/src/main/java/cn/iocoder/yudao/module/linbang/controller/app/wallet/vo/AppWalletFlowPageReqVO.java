package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户 App - 钱包流水分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppWalletFlowPageReqVO extends PageParam {

    @Schema(description = "业务类型", example = "WITHDRAW_APPLY")
    private String bizType;

    @Schema(description = "流水类型", example = "OUT")
    private String flowType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
