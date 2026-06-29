package cn.iocoder.yudao.module.linbang.controller.app.member.security.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 用户自定义脱敏词分页 Request VO")
@Data
public class AppUserSensitiveCustomWordPageReqVO extends PageParam {

    @Schema(description = "关键词")
    private String word;

    @Schema(description = "状态", example = "ENABLE")
    private String status;
}
