package cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 托管凭证分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EscrowProofPageReqVO extends PageParam {

    @Schema(description = "托管凭证业务编号")
    private String proofNo;
    @Schema(description = "主订单 ID，关联主订单")
    private Long orderId;
    @Schema(description = "订单单元 ID，关联拆分后的订单单元")
    private Long unitId;
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;
    @Schema(description = "托管凭证状态：LOCKED 已锁定；后续解锁或退款状态按资金状态机扩展")
    private String proofStatus;
}
