package cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 我的悬赏分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppRewardOrderPageReqVO extends PageParam {

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;
}
