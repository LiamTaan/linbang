package cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 推广员 Response VO")
public class PromoterRespVO {

    @ExcelProperty("主键")
    @Schema(description = "主键 ID")
    private Long id;

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

    @ExcelProperty("等级")
    @Schema(description = "推广员等级编码：L1 初级、L2 中级、L3 高级；处罚状态可按风控规则扩展")
    private String levelCode;

    @ExcelProperty("邀请码")
    @Schema(description = "推广邀请码")
    private String inviteCode;

    @ExcelProperty("绑定人数")
    @Schema(description = "已绑定邀请关系的用户数量，单位：人")
    private Integer bindUserCount;

    @ExcelProperty("转化人数")
    @Schema(description = "已完成首单转化的用户数量，单位：人")
    private Integer convertCount;

    @ExcelProperty("待转化人数")
    @Schema(description = "已绑定但尚未完成首单转化的用户数量，单位：人")
    private Integer pendingConvertCount;

    @ExcelProperty("累计佣金")
    @Schema(description = "累计佣金金额，单位：元，保留两位小数")
    private BigDecimal totalCommissionAmount;

    @ExcelProperty("可提现佣金")
    @Schema(description = "当前可用佣金金额，单位：元，保留两位小数")
    private BigDecimal availableCommissionAmount;

    @ExcelProperty("状态")
    @Schema(description = "推广员状态：ENABLE 启用、DISABLE 停用")
    private String status;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
