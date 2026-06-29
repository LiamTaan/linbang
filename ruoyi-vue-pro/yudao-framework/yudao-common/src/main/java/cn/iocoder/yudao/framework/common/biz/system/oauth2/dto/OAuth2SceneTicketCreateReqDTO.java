package cn.iocoder.yudao.framework.common.biz.system.oauth2.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 场景票据创建 Request DTO
 */
@Data
public class OAuth2SceneTicketCreateReqDTO implements Serializable {

    /**
     * 场景
     */
    @NotBlank(message = "场景不能为空")
    private String scene;

    /**
     * 用户编号
     */
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    /**
     * 用户类型
     */
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    /**
     * 租户编号
     */
    private Long tenantId;

}
