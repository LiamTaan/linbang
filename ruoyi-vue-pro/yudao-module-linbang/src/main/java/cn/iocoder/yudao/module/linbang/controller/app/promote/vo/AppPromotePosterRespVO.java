package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "用户 App - 推广小程序码与海报 Response VO")
@Data
@AllArgsConstructor
public class AppPromotePosterRespVO {

    @Schema(description = "微信小程序码文件 URL")
    private String qrcodeUrl;

    @Schema(description = "包含邀请码和小程序码的推广海报文件 URL")
    private String posterUrl;
}
