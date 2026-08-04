package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "用户 App - 推广中心 Response VO")
public class AppInviteCodeRespVO {

    @Schema(description = "推广邀请码")
    private String inviteCode;

    @Schema(description = "带邀请码参数的推广页面地址")
    private String inviteUrl;

    @Schema(description = "邀请码短链接")
    private String inviteShortLink;

    @Schema(description = "邀请码海报图片地址")
    private String invitePosterUrl;
}
