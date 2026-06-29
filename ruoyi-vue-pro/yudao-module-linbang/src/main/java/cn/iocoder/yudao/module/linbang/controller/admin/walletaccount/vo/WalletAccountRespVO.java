package cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo;

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

@Schema(description = "管理后台 - 钱包账户 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WalletAccountRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "6344")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6967")
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

    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "角色编码", converter = DictConvert.class)
    @DictFormat("lb_role_code") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String roleCode;

    @Schema(description = "总资产", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("总资产")
    private BigDecimal totalAmount;

    @Schema(description = "可提现金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("可提现金额")
    private BigDecimal availableAmount;

    @Schema(description = "冻结金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("冻结金额")
    private BigDecimal frozenAmount;

    @Schema(description = "托管金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("托管金额")
    private BigDecimal escrowAmount;

    @Schema(description = "佣金金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("佣金金额")
    private BigDecimal commissionAmount;

    @Schema(description = OpenApiSchemaConstants.WALLET_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
