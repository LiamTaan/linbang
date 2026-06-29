package cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 我参与的悬赏分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppRewardOrderParticipationPageReqVO extends PageParam {

    @Schema(description = "参与状态 PENDING/ACCEPTED/REJECTED/CANCELLED", example = "PENDING")
    private String status;
}
