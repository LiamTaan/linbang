package cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 消息模板 Response VO")
public class MessageTemplateRespVO {

    @ExcelProperty("主键")
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("模板编码")
    @Schema(description = "消息模板唯一编码")
    private String templateCode;

    @ExcelProperty("模板名称")
    @Schema(description = "消息模板名称")
    private String templateName;

    @ExcelProperty("场景编码")
    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @ExcelProperty("消息分类")
    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @ExcelProperty("模板类型")
    @Schema(description = "模板类型：BIZ 业务消息模板、MARKETING 营销消息模板")
    private String templateType;

    @ExcelProperty("渠道类型")
    @Schema(description = "消息渠道：APP_POPUP 站内消息、WECHAT_MP_TEMPLATE 微信模板消息、SMS 短信、APP_VOICE App 语音朗读")
    private String channelType;

    @ExcelProperty("标题模板")
    @Schema(description = "消息标题模板；变量使用消息模板约定的占位符")
    private String titleTemplate;

    @ExcelProperty("模板内容")
    @Schema(description = "消息正文模板；变量使用消息模板约定的占位符")
    private String contentTemplate;

    @ExcelProperty("状态")
    @Schema(description = "消息模板状态：ENABLE 启用、DISABLE 停用")
    private String status;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
