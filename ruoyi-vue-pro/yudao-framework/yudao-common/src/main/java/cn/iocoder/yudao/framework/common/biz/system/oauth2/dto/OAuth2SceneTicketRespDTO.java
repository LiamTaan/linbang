package cn.iocoder.yudao.framework.common.biz.system.oauth2.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 场景票据 Response DTO
 */
@Data
public class OAuth2SceneTicketRespDTO implements Serializable {

    /**
     * 场景票据
     */
    private String token;

    /**
     * 过期秒数
     */
    private Integer expiresInSeconds;

}
