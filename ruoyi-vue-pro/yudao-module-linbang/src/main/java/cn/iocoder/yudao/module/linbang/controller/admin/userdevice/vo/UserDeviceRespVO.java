package cn.iocoder.yudao.module.linbang.controller.admin.userdevice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户设备 Response VO")
@Data
public class UserDeviceRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1001")
    private Long userId;

    @Schema(description = "用户编号")
    private String userNo;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号")
    private String userMobile;

    @Schema(description = "设备指纹摘要")
    private String deviceFingerprint;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "最近登录 IP")
    private String lastIp;

    @Schema(description = "最近登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
