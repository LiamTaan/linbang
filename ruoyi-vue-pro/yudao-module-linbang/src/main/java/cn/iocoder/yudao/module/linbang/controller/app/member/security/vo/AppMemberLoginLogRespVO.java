package cn.iocoder.yudao.module.linbang.controller.app.member.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 登录记录 Response VO")
@Data
public class AppMemberLoginLogRespVO {

    @Schema(description = "日志 ID", example = "1")
    private Long id;

    @Schema(description = "登录类型", example = "103")
    private Integer logType;

    @Schema(description = "登录账号", example = "13800138000")
    private String username;

    @Schema(description = "登录结果，0 成功，其余为失败码", example = "0")
    private Integer result;

    @Schema(description = "登录 IP", example = "127.0.0.1")
    private String userIp;

    @Schema(description = "用户代理")
    private String userAgent;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
