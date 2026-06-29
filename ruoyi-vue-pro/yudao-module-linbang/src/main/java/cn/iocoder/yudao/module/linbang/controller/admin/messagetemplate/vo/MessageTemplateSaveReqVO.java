package cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 消息模板新增/修改 Request VO")
@Data
public class MessageTemplateSaveReqVO {

    private Long id;

    @NotBlank(message = "模板编码不能为空")
    private String templateCode;

    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @NotBlank(message = "场景编码不能为空")
    private String sceneCode;

    @NotBlank(message = "消息分类不能为空")
    private String messageCategory;

    @NotBlank(message = "模板类型不能为空")
    private String templateType;

    @NotBlank(message = "渠道类型不能为空")
    private String channelType;

    private String titleTemplate;

    @NotBlank(message = "模板内容不能为空")
    private String contentTemplate;

    private String routeType;

    private String routeValue;

    private String mpTemplateId;

    private String smsTemplateCode;

    private String voiceTextTemplate;

    private Integer sort;

    @NotNull(message = "状态不能为空")
    private String status;
}
