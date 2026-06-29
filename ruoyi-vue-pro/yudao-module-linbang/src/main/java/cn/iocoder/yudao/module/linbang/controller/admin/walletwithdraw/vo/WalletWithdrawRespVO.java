package cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 提现申请 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WalletWithdrawRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4287")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "提现单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("提现单号")
    private String withdrawNo;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24266")
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

    @Schema(description = "钱包账户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19469")
    @ExcelProperty("钱包账户ID")
    private Long walletAccountId;

    @Schema(description = "钱包角色编码")
    @ExcelProperty("钱包角色编码")
    private String walletRoleCode;

    @Schema(description = "钱包状态")
    @ExcelProperty("钱包状态")
    private String walletStatus;

    @Schema(description = "银行卡ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8562")
    @ExcelProperty("银行卡ID")
    private Long bankCardId;

    @Schema(description = "银行名称")
    @ExcelProperty("银行名称")
    private String bankName;

    @Schema(description = "脱敏卡号")
    @ExcelProperty("脱敏卡号")
    private String cardNoMask;

    @Schema(description = "开户名")
    @ExcelProperty("开户名")
    private String bankAccountName;

    @Schema(description = "申请金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("申请金额")
    private BigDecimal applyAmount;

    @Schema(description = "手续费", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("手续费")
    private BigDecimal feeAmount;

    @Schema(description = "实际到账金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("实际到账金额")
    private BigDecimal realAmount;

    @Schema(description = OpenApiSchemaConstants.WITHDRAW_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("lb_withdraw_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String status;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @ExcelProperty(value = "审核状态", converter = DictConvert.class)
    @DictFormat("lb_withdraw_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String auditStatus;

    @Schema(description = "审核备注", example = "你猜")
    @ExcelProperty("审核备注")
    private String auditRemark;

    @Schema(description = "审核人")
    @ExcelProperty("审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    @ExcelProperty("审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "驳回原因", example = "不对")
    @ExcelProperty("驳回原因")
    private String rejectReason;

    @Schema(description = "打款时间")
    @ExcelProperty("打款时间")
    private LocalDateTime payTime;

    @Schema(description = "支付转账单 ID", example = "7001")
    @ExcelProperty("支付转账单ID")
    private Long payTransferId;

    @Schema(description = "支付转账单号")
    @ExcelProperty("支付转账单号")
    private String payTransferNo;

    @Schema(description = "出款失败原因")
    @ExcelProperty("出款失败原因")
    private String transferErrorMsg;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
