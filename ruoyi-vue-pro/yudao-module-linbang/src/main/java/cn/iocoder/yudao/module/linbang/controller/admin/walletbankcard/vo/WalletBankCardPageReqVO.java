package cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户银行卡分页 Request VO")
@Data
public class WalletBankCardPageReqVO extends PageParam {

    @Schema(description = "用户关键词，支持用户编号/昵称/手机号", example = "13800138000")
    private String userKeyword;

    @Schema(description = "银行名称", example = "芋艿")
    private String bankName;

    @Schema(description = "银行编码")
    private String bankCode;

    @Schema(description = "加密卡号")
    private String cardNoEncrypt;

    @Schema(description = "脱敏卡号")
    private String cardNoMask;

    @Schema(description = "开户名", example = "王五")
    private String accountName;

    @Schema(description = "预留手机号")
    private String reservedMobile;

    @Schema(description = OpenApiSchemaConstants.BANK_CARD_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = "是否默认")
    private Boolean isDefault;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
