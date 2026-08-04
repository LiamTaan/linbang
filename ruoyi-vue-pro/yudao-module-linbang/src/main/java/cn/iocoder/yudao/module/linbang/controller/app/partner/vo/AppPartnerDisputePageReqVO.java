package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "用户 App - 合作商辖区纠纷分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppPartnerDisputePageReqVO extends PageParam {

    @Schema(description = OpenApiSchemaConstants.PARTNER_DISPUTE_TYPE, example = "COMPLAINT")
    @Pattern(regexp = "(?i)^(COMPLAINT|APPEAL)$", message = "纠纷类型仅支持 COMPLAINT 或 APPEAL")
    private String disputeType;

    @Schema(description = "订单号/纠纷单号关键词", example = "LB202606280001")
    @Size(max = 64, message = "关键词长度不能超过 64 个字符")
    private String keyword;

    @Schema(description = "辖区编码", example = "440305")
    @Size(max = 16, message = "辖区编码长度不能超过 16 个字符")
    private String regionCode;

    @Schema(description = "纠纷状态", example = "PENDING")
    @Size(max = 32, message = "纠纷状态长度不能超过 32 个字符")
    private String status;
}
