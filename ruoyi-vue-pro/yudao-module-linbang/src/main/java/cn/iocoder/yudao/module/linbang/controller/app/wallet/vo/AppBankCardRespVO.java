package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 银行卡 Response VO；仅表示系统内提现收款账户，不对外宣称已完成第三方鉴权绑卡")
@Data
public class AppBankCardRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "银行名称", example = "中国工商银行")
    private String bankName;

    @Schema(description = "银行编码；提现出款时传给第三方通道识别收款银行", example = "ICBC")
    private String bankCode;

    @Schema(description = "脱敏卡号", example = "6222 **** **** 1234")
    private String cardNoMask;

    @Schema(description = "是否可用于提现出款", example = "true")
    private Boolean transferEnabled;

    @Schema(description = "开户名", example = "张三")
    private String accountName;

    @Schema(description = "开户省份，用于第三方提现出款", example = "广东省")
    private String bankProvince;

    @Schema(description = "开户城市，用于第三方提现出款", example = "深圳市")
    private String bankCity;

    @Schema(description = "银行预留手机号；第三方出款校验使用", example = "13800138000")
    private String reservedMobile;

    @Schema(description = "银行卡状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String status;

    @Schema(description = "是否默认卡", example = "true")
    private Boolean isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
