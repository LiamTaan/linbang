package cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 风险事件 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RiskEventRespVO {

    @ExcelProperty("主键")
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("业务类型")
    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;

    @ExcelProperty("业务ID")
    @Schema(description = "关联业务对象 ID；由 bizType 指明对象类型")
    private Long bizId;

    @ExcelProperty("业务对象")
    @Schema(description = "关联业务对象的可读摘要")
    private String bizDisplay;

    @ExcelProperty("风险类型")
    @Schema(description = "风险类型，按平台风控类型字典展示")
    private String riskType;

    @ExcelProperty("风险等级")
    @Schema(description = "风险等级：LOW 低风险、MEDIUM 中风险、HIGH 高风险")
    private String riskLevel;

    @ExcelProperty("命中规则")
    @Schema(description = "命中的风控规则编码")
    private String hitRuleCode;

    @ExcelProperty("状态")
    @Schema(description = "风险事件状态：PENDING 待处理、PROCESSING 处理中、FINISHED 已完结")
    private String status;

    @ExcelProperty("处置状态")
    @Schema(description = "风险处置状态：PENDING 待复核、PROCESSING 处理中、FINISHED 已完成、RELEASED 已解除")
    private String disposeStatus;

    @ExcelProperty("处置动作")
    @Schema(description = "风险处置动作：CONFIRM_VIOLATION 确认违规、RELEASE_FALSE_POSITIVE 解除误判、FREEZE_FUNDS 冻结资金、UNFREEZE_FUNDS 解冻资金、RESTRICT_PUBLISH 限制发单、RESTRICT_ACCEPT 限制接单")
    private String disposeAction;

    @ExcelProperty("处理人")
    @Schema(description = "处理人后台用户 ID")
    private Long handleBy;

    @ExcelProperty("处理时间")
    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @ExcelProperty("备注")
    @Schema(description = "业务备注")
    private String remark;

    @ExcelProperty("处置备注")
    @Schema(description = "风险处置备注")
    private String disposeRemark;

    @ExcelProperty("关联用户")
    @Schema(description = "风险事件关联用户 ID 列表，多个 ID 以英文逗号分隔")
    private String relatedUserIds;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
