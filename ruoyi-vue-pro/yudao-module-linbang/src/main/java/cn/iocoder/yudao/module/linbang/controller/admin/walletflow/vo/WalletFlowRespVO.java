package cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 钱包流水 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WalletFlowRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "6790")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "流水号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("流水号")
    private String flowNo;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8346")
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

    @Schema(description = "钱包账户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16689")
    @ExcelProperty("钱包账户ID")
    private Long walletAccountId;

    @Schema(description = "钱包角色编码")
    @ExcelProperty("钱包角色编码")
    private String walletRoleCode;

    @Schema(description = "钱包状态")
    @ExcelProperty("钱包状态")
    private String walletStatus;

    @Schema(description = "可提现金额")
    @ExcelProperty("可提现金额")
    private BigDecimal walletAvailableAmount;

    @Schema(description = "业务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("业务类型")
    private String bizType;

    @Schema(description = "流水类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("流水类型")
    private String flowType;

    @Schema(description = "变动金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("变动金额")
    private BigDecimal changeAmount;

    @Schema(description = "变动前金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("变动前金额")
    private BigDecimal beforeAmount;

    @Schema(description = "变动后金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("变动后金额")
    private BigDecimal afterAmount;

    @Schema(description = "关联订单ID", example = "25614")
    @ExcelProperty("关联订单ID")
    private Long relatedOrderId;

    @Schema(description = "关联订单号")
    @ExcelProperty("关联订单号")
    private String relatedOrderNo;

    @Schema(description = "关联单元ID", example = "28489")
    @ExcelProperty("关联单元ID")
    private Long relatedUnitId;

    @Schema(description = "关联单元号")
    @ExcelProperty("关联单元号")
    private String relatedUnitNo;

    @Schema(description = "关联支付订单ID", example = "16116")
    @ExcelProperty("关联支付订单ID")
    private Long relatedPayOrderId;

    @Schema(description = "关联退款ID", example = "12674")
    @ExcelProperty("关联退款ID")
    private Long relatedRefundId;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
