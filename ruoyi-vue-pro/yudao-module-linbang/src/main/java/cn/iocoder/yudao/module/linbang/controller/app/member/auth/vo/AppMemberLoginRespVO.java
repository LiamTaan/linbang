package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 登录结果 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppMemberLoginRespVO {

    @Schema(description = "用户编号。仅在已获得正式登录态时返回；当 bindRequired=true 时允许为空。", example = "1024")
    private Long userId;

    @Schema(description = "访问令牌。仅在已获得正式登录态时返回；当 bindRequired=true 时允许为空。", example = "happy")
    private String accessToken;

    @Schema(description = "刷新令牌。仅在已获得正式登录态时返回；当 bindRequired=true 时允许为空。", example = "nice")
    private String refreshToken;

    @Schema(description = "访问令牌过期时间。仅在已获得正式登录态时返回；当 bindRequired=true 时允许为空。")
    private LocalDateTime expiresTime;

    @Schema(description = "是否必须先完成手机号绑定才能获得正式登录态。true 表示当前仅完成第三方授权，前端需继续调用绑定手机号接口。", example = "false")
    private Boolean bindRequired;

    @Schema(description = "第三方平台类型，仅在第三方登录链路返回。32=微信开放平台授权，40=支付宝授权。", example = "32")
    private Integer socialType;

    @Schema(description = "第三方用户 openid，仅在第三方登录链路返回。")
    private String socialOpenid;

    @Schema(description = "第三方用户昵称，仅在第三方登录链路返回。")
    private String socialNickname;

    @Schema(description = "第三方用户头像，仅在第三方登录链路返回。")
    private String socialAvatar;

    @Schema(description = "未注册提醒，仅在需要先完成注册的场景返回。")
    private AppRegisterReminderRespVO registerReminder;
}
