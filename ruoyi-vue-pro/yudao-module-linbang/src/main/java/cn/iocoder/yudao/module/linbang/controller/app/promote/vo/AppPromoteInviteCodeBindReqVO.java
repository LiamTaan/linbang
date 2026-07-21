package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "用户 App - 绑定邀请码 Request VO")
@Data
public class AppPromoteInviteCodeBindReqVO {

    @Schema(description = "邀请码", requiredMode = Schema.RequiredMode.REQUIRED, example = "LB44C2C5E8")
    @NotBlank(message = "邀请码不能为空")
    private String inviteCode;

    @Schema(description = "邀请来源渠道：SHARE_CARD 微信好友分享、TIMELINE 朋友圈、QRCODE 小程序码、MANUAL 手工填写",
            example = "SHARE_CARD")
    private String sourceChannel;

    @Schema(description = "捕获邀请码的来源页面路径", example = "pages/index/index")
    private String sourcePage;
}
