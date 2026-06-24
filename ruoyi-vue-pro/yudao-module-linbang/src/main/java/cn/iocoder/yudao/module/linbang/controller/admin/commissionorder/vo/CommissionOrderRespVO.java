package cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
public class CommissionOrderRespVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("佣金单号")
    private String commissionNo;

    @ExcelProperty("推广员ID")
    private Long promoterId;

    @ExcelProperty("推广员编号")
    private String promoterUserNo;

    @ExcelProperty("推广员昵称")
    private String promoterUserNickname;

    @ExcelProperty("推广员手机号")
    private String promoterUserMobile;

    @ExcelProperty("用户ID")
    private Long userId;

    @ExcelProperty("用户编号")
    private String userNo;

    @ExcelProperty("用户昵称")
    private String userNickname;

    @ExcelProperty("用户手机号")
    private String userMobile;

    @ExcelProperty("来源订单ID")
    private Long sourceOrderId;

    @ExcelProperty("来源订单号")
    private String sourceOrderNo;

    @ExcelProperty("来源单元ID")
    private Long sourceUnitId;

    @ExcelProperty("来源单元号")
    private String sourceUnitNo;

    @ExcelProperty("佣金类型")
    private String commissionType;

    @ExcelProperty("佣金金额")
    private BigDecimal commissionAmount;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("结算时间")
    private LocalDateTime settleTime;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
