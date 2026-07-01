package cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Schema(description = "管理后台 - 手动发送消息推送任务 Request VO")
@Data
public class MessagePushTaskManualSendReqVO {

    @Schema(description = "接收范围 SINGLE_USER 指定用户、ALL_USERS 全部用户", example = "SINGLE_USER")
    @Size(max = 32, message = "接收范围不能超过 32 个字符")
    private String receiverScope;

    @Schema(description = "接收用户 ID；receiverScope=SINGLE_USER 时必填", example = "10001")
    private Long receiverUserId;

    @Schema(description = "消息标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统通知")
    @Size(max = 255, message = "消息标题不能超过 255 个字符")
    private String title;

    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "您的资料已审核通过，请及时查看。")
    @Size(max = 5000, message = "消息内容不能超过 5000 个字符")
    private String content;

    @Schema(description = "业务类型，默认 ADMIN_MANUAL_NOTICE", example = "ADMIN_MANUAL_NOTICE")
    @Size(max = 64, message = "业务类型不能超过 64 个字符")
    private String bizType;

    @Schema(description = "路由类型", example = "APP_PAGE")
    @Size(max = 32, message = "路由类型不能超过 32 个字符")
    private String routeType;

    @Schema(description = "路由值", example = "/pages/message/detail")
    @Size(max = 255, message = "路由值不能超过 255 个字符")
    private String routeValue;
}
