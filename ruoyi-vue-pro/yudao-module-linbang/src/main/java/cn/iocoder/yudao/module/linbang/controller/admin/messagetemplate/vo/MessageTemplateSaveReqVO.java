package cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 消息模板新增/修改 Request VO")
@Data
public class MessageTemplateSaveReqVO {

    @Schema(description = "主键 ID")
    private Long id;

    @NotBlank(message = "模板编码不能为空")
    @Schema(description = "消息模板唯一编码")
    private String templateCode;

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "消息模板名称")
    private String templateName;

    @NotBlank(message = "场景编码不能为空")
    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @NotBlank(message = "消息分类不能为空")
    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @NotBlank(message = "模板类型不能为空")
    @Schema(description = "模板类型：BIZ 业务消息模板、MARKETING 营销消息模板")
    private String templateType;

    @NotBlank(message = "渠道类型不能为空")
    @Schema(description = "消息渠道：APP_POPUP 站内消息、WECHAT_MP_TEMPLATE 微信模板消息、SMS 短信、APP_VOICE App 语音朗读")
    private String channelType;

    @Schema(description = "消息标题模板；变量使用消息模板约定的占位符")
    private String titleTemplate;

    @NotBlank(message = "模板内容不能为空")
    @Schema(description = "消息正文模板；变量使用消息模板约定的占位符")
    private String contentTemplate;

    @Schema(description = "消息跳转类型：APP_PAGE App 内页面；其他类型按消息路由字典扩展")
    private String routeType;

    @Schema(description = "消息跳转目标；APP_PAGE 类型时为站内页面路径")
    private String routeValue;

    @Schema(description = "微信公众平台或小程序模板 ID")
    private String mpTemplateId;

    @Schema(description = "短信服务商模板编码")
    private String smsTemplateCode;

    @Schema(description = "语音朗读文本模板；变量使用消息模板约定的占位符")
    private String voiceTextTemplate;

    @Schema(description = "显示排序值；数值越小越靠前")
    private Integer sort;

    @NotNull(message = "状态不能为空")
    @Schema(description = "消息模板状态：ENABLE 启用、DISABLE 停用")
    private String status;
}
