package cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
public class PromoterRespVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("用户ID")
    private Long userId;

    @ExcelProperty("用户编号")
    private String userNo;

    @ExcelProperty("用户昵称")
    private String userNickname;

    @ExcelProperty("用户手机号")
    private String userMobile;

    @ExcelProperty("等级")
    private String levelCode;

    @ExcelProperty("邀请码")
    private String inviteCode;

    @ExcelProperty("绑定人数")
    private Integer bindUserCount;

    @ExcelProperty("转化人数")
    private Integer convertCount;

    @ExcelProperty("累计佣金")
    private BigDecimal totalCommissionAmount;

    @ExcelProperty("可提现佣金")
    private BigDecimal availableCommissionAmount;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
