package cn.iocoder.yudao.module.linbang.controller.admin.escrowproof.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 托管凭证分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EscrowProofPageReqVO extends PageParam {

    private String proofNo;
    private Long orderId;
    private Long unitId;
    private Long userId;
    private String proofStatus;
}
