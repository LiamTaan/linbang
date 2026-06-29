package cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户自定义脱敏词 Response VO")
@Data
public class UserSensitiveCustomWordRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "10001")
    private Long userId;

    @Schema(description = "用户编号")
    private String userNo;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号")
    private String userMobile;

    @Schema(description = "脱敏词")
    private String word;

    @Schema(description = "适用场景")
    private String sceneType;

    @Schema(description = "状态", example = "ENABLE")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
