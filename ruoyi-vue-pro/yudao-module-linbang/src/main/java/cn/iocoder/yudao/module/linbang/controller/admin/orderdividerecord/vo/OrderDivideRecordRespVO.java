package cn.iocoder.yudao.module.linbang.controller.admin.orderdividerecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分账明细 Response VO")
@Data
public class OrderDivideRecordRespVO {

    @Schema(description = "分账明细 ID", example = "1")
    private Long id;

    @Schema(description = "分账编号")
    private String divideNo;

    @Schema(description = "订单 ID", example = "1001")
    private Long orderId;

    @Schema(description = "单元 ID", example = "2001")
    private Long unitId;

    @Schema(description = "分账规则 ID", example = "3001")
    private Long divideRuleId;

    @Schema(description = "分账去向类型：MERCHANT 服务商、PLATFORM 平台、PARTNER 合作商、PROMOTER 推广员、TAX 税务代扣")
    private String targetType;

    @Schema(description = "分账目标业务 ID")
    private Long targetBizId;

    @Schema(description = "分账比例，单位百分比", example = "8.00")
    private BigDecimal divideRate;

    @Schema(description = "分账金额，单位元", example = "80.00")
    private BigDecimal divideAmount;

    @Schema(description = "税务扣减金额，单位元", example = "20.00")
    private BigDecimal taxAmount;

    @Schema(description = "结算状态", example = "PENDING")
    private String settleStatus;

    @Schema(description = "城市等级")
    private String cityLevel;

    @Schema(description = "类目 ID")
    private Long categoryId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
