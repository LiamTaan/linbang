package cn.iocoder.yudao.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthSceneTicketRespVO {

    @Schema(description = "场景票据")
    private String token;

    @Schema(description = "过期秒数")
    private Integer expiresInSeconds;

}
