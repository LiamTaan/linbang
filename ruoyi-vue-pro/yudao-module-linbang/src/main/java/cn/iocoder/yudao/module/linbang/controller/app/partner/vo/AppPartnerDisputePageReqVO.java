package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 合作商辖区纠纷分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppPartnerDisputePageReqVO extends PageParam {

    @Schema(description = "纠纷类型：COMPLAINT 投诉、APPEAL 申诉", example = "COMPLAINT")
    private String disputeType;

    @Schema(description = "纠纷状态", example = "PENDING")
    private String status;
}
