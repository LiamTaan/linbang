package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 推广佣金分页 Request VO")
@Data
public class AppCommissionPageReqVO extends PageParam {

    @Schema(description = "佣金单状态筛选：PENDING 待结算、SETTLED 已结算、INVALID 已失效", example = "PENDING")
    private String status;
}
