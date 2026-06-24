package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 消息记录详情 Response VO")
@Data
public class AppMessageRecordDetailRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "模板 ID", example = "10")
    private Long templateId;

    @Schema(description = "接收用户 ID", example = "1001")
    private Long receiverUserId;

    @Schema(description = "渠道类型", example = "SITE")
    private String channelType;

    @Schema(description = "业务类型", example = "ORDER")
    private String bizType;

    @Schema(description = "业务 ID", example = "2001")
    private Long bizId;

    @Schema(description = "发送状态，按消息发送结果字典展示，常见值如 SUCCESS 发送成功、FAILED 发送失败", example = "SUCCESS")
    private String sendStatus;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "失败原因")
    private String failReason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "模板摘要")
    private TemplateRespVO template;

    @Data
    public static class TemplateRespVO {

        @Schema(description = "模板 ID", example = "10")
        private Long id;

        @Schema(description = "模板编码", example = "ORDER_ACCEPT_NOTICE")
        private String templateCode;

        @Schema(description = "模板名称", example = "接单通知")
        private String templateName;

        @Schema(description = "模板类型", example = "BIZ")
        private String templateType;

        @Schema(description = "渠道类型", example = "SITE")
        private String channelType;

        @Schema(description = "模板内容")
        private String content;

        @Schema(description = "模板状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
        private String status;
    }
}
