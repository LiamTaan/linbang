package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 银行卡 Response VO")
@Data
public class AppBankCardRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "银行名称", example = "中国工商银行")
    private String bankName;

    @Schema(description = "银行编码", example = "ICBC")
    private String bankCode;

    @Schema(description = "脱敏卡号", example = "6222 **** **** 1234")
    private String cardNoMask;

    @Schema(description = "开户名", example = "张三")
    private String accountName;

    @Schema(description = "预留手机号", example = "13800138000")
    private String reservedMobile;

    @Schema(description = "银行卡状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String status;

    @Schema(description = "是否默认卡", example = "true")
    private Boolean isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
