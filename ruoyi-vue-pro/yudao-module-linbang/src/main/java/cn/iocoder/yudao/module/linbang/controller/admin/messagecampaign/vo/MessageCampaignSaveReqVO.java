package cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 消息投放新增/修改 Request VO")
@Data
public class MessageCampaignSaveReqVO {

    @Schema(description = "主键 ID")
    private Long id;

    @NotBlank(message = "投放名称不能为空")
    @Schema(description = "消息投放活动名称")
    private String campaignName;

    @NotBlank(message = "来源类型不能为空")
    @Schema(description = "投放来源：USER_DIRECTED 用户定向申请、ADMIN_DIRECTED 管理后台定向投放、SYSTEM_TRIGGER 系统触发、AD 广告投放")
    private String sourceType;

    @NotBlank(message = "目标模式不能为空")
    @Schema(description = "目标模式：FULL_PLATFORM 全平台、JURISDICTION 辖区、CUSTOM_FILTER 自定义筛选")
    private String targetMode;

    @Schema(description = "目标高德行政区划编码列表；为空表示不按区域限制")
    private String targetRegionCodes;

    @Schema(description = "目标服务分类 ID 列表；为空表示不按分类限制")
    private String targetCategoryIds;

    @Schema(description = "目标用户角色编码列表；为空表示不按角色限制")
    private String targetRoleCodes;

    @Schema(description = "允许投放的时间窗口列表，例如 09:00-12:00；为空表示不限制")
    private String deliveryTimeWindows;

    @Schema(description = "计划执行时间；为空表示审核通过后尽快执行")
    private LocalDateTime scheduleTime;

    @NotBlank(message = "场景编码不能为空")
    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @NotBlank(message = "消息分类不能为空")
    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;

    @Schema(description = "关联业务对象 ID；由 bizType 指明对象类型")
    private Long bizId;

    @NotNull(message = "内容快照不能为空")
    @Schema(description = "发送时固化的消息正文快照")
    private String contentSnapshot;
}
