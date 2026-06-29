package cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 托管凭证 Response VO")
@Data
public class EscrowProofRespVO {

    private Long id;
    private String proofNo;
    private Long orderId;
    private Long unitId;
    private Long userId;
    private Long merchantId;
    private BigDecimal escrowAmount;
    private String proofStatus;
    private String lockReason;
    private String unlockReason;
    private LocalDateTime createTime;
}
