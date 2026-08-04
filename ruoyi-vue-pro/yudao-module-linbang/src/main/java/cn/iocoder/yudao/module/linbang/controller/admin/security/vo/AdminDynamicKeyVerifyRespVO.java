package cn.iocoder.yudao.module.linbang.controller.admin.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 动态密钥校验 Response VO")
public class AdminDynamicKeyVerifyRespVO {

    @Schema(description = "动态密钥校验通过后签发的一次性短期令牌")
    private String verifyToken;

    @Schema(description = "失效时间")
    private LocalDateTime expireTime;
}
