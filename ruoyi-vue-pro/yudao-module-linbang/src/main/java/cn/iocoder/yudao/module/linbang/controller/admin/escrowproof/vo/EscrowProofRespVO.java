package cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 托管凭证 Response VO")
@Data
public class EscrowProofRespVO {

    @Schema(description = "主键 ID")
    private Long id;
    @Schema(description = "托管凭证业务编号")
    private String proofNo;
    @Schema(description = "主订单 ID，关联主订单")
    private Long orderId;
    @Schema(description = "订单单元 ID，关联拆分后的订单单元")
    private Long unitId;
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;
    @Schema(description = "服务商 ID，关联服务商档案")
    private Long merchantId;
    @Schema(description = "托管金额，单位：元，保留两位小数")
    private BigDecimal escrowAmount;
    @Schema(description = "托管凭证状态：LOCKED 已锁定；后续解锁或退款状态按资金状态机扩展")
    private String proofStatus;
    @Schema(description = "订单单元资金锁定原因")
    private String lockReason;
    @Schema(description = "订单单元资金解锁原因")
    private String unlockReason;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
