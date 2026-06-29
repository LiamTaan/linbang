package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户 App - 钱包账单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppWalletBillPageReqVO extends PageParam {

    @Schema(description = OpenApiSchemaConstants.WALLET_BILL_TYPE, example = "WITHDRAW")
    private String billType;

    @Schema(description = "账单业务状态，例如 SUCCESS、PENDING、FAILED", example = "SUCCESS")
    private String bizStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "账单创建时间范围")
    private LocalDateTime[] createTime;
}
