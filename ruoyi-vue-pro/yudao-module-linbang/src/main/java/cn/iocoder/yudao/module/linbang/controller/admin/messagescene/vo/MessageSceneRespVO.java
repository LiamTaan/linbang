package cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 消息场景 Response VO")
public class MessageSceneRespVO {

    @ExcelProperty("主键")
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("场景编码")
    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @ExcelProperty("场景名称")
    @Schema(description = "消息业务场景名称")
    private String sceneName;

    @ExcelProperty("消息分类")
    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @ExcelProperty("默认渠道")
    @Schema(description = "默认发送渠道编码列表，多个渠道以英文逗号分隔")
    private String defaultChannels;

    @ExcelProperty("强制短信")
    @Schema(description = "是否强制发送短信：true 是、false 否")
    private Boolean mandatorySms;

    @ExcelProperty("支持语音")
    @Schema(description = "该消息场景是否允许语音播报：true 允许、false 禁止")
    private Boolean voiceEnabled;

    @ExcelProperty("状态")
    @Schema(description = "消息场景状态：ENABLE 启用、DISABLE 停用")
    private String status;

    @ExcelProperty("业务类型")
    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;

    @ExcelProperty("备注")
    @Schema(description = "业务备注")
    private String remark;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
