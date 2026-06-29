package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppMemberSceneTicketRespVO {

    @Schema(description = "场景票据")
    private String token;

    @Schema(description = "过期秒数")
    private Integer expiresInSeconds;

}
