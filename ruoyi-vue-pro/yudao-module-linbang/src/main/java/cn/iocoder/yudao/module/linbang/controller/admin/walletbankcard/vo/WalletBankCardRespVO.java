package cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 用户银行卡 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WalletBankCardRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21704")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3809")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "用户编号", example = "LBU123456")
    @ExcelProperty("用户编号")
    private String userNo;

    @Schema(description = "用户昵称", example = "邻里用户")
    @ExcelProperty("用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号", example = "13800138000")
    @ExcelProperty("用户手机号")
    private String userMobile;

    @Schema(description = "银行名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("银行名称")
    private String bankName;

    @Schema(description = "银行编码")
    @ExcelProperty("银行编码")
    private String bankCode;

    @Schema(description = "加密卡号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("加密卡号")
    private String cardNoEncrypt;

    @Schema(description = "脱敏卡号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("脱敏卡号")
    private String cardNoMask;

    @Schema(description = "开户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("开户名")
    private String accountName;

    @Schema(description = "预留手机号")
    @ExcelProperty("预留手机号")
    private String reservedMobile;

    @Schema(description = OpenApiSchemaConstants.BANK_CARD_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "是否默认", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否默认")
    private Boolean isDefault;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
