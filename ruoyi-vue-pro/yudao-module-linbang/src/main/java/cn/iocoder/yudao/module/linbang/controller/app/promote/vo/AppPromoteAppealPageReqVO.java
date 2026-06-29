package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 推广申诉分页 Request VO")
@Data
public class AppPromoteAppealPageReqVO extends PageParam {

    @Schema(description = "申诉状态")
    private String status;
}
