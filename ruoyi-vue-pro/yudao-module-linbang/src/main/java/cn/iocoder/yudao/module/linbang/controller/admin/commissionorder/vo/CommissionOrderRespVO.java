package cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 佣金订单 Response VO")
public class CommissionOrderRespVO {

    @ExcelProperty("主键")
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("佣金单号")
    @Schema(description = "佣金订单业务编号")
    private String commissionNo;

    @ExcelProperty("推广员ID")
    @Schema(description = "推广员 ID，关联推广员档案")
    private Long promoterId;

    @ExcelProperty("推广员编号")
    @Schema(description = "推广员关联用户编号")
    private String promoterUserNo;

    @ExcelProperty("推广员昵称")
    @Schema(description = "推广员关联用户昵称")
    private String promoterUserNickname;

    @ExcelProperty("推广员手机号")
    @Schema(description = "推广员关联用户手机号")
    private String promoterUserMobile;

    @ExcelProperty("用户ID")
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;

    @ExcelProperty("用户编号")
    @Schema(description = "平台用户业务编号")
    private String userNo;

    @ExcelProperty("用户昵称")
    @Schema(description = "用户昵称")
    private String userNickname;

    @ExcelProperty("用户手机号")
    @Schema(description = "用户手机号")
    private String userMobile;

    @ExcelProperty("来源订单ID")
    @Schema(description = "佣金来源主订单 ID")
    private Long sourceOrderId;

    @ExcelProperty("来源订单号")
    @Schema(description = "佣金来源主订单业务编号")
    private String sourceOrderNo;

    @ExcelProperty("来源单元ID")
    @Schema(description = "佣金来源订单单元 ID")
    private Long sourceUnitId;

    @ExcelProperty("来源单元号")
    @Schema(description = "佣金来源订单单元业务编号")
    private String sourceUnitNo;

    @ExcelProperty("佣金类型")
    @Schema(description = "佣金类型，按推广佣金字典展示，当前常见值 ORDER 表示订单佣金")
    private String commissionType;

    @ExcelProperty("佣金金额")
    @Schema(description = "佣金金额，单位：元，保留两位小数")
    private BigDecimal commissionAmount;

    @ExcelProperty("状态")
    @Schema(description = "佣金状态：PENDING 待结算、SETTLED 已结算、REFUNDED 已退款冲正")
    private String status;

    @ExcelProperty("结算时间")
    @Schema(description = "佣金结算时间")
    private LocalDateTime settleTime;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
