package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 信用记录详情 Response VO")
@Data
public class AppCreditRecordDetailRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "规则编码", example = "ORDER_FINISHED_POSITIVE")
    private String ruleCode;

    @Schema(description = "规则名称", example = "订单完结好评加分")
    private String ruleName;

    @Schema(description = "分值变动", example = "5")
    private Integer scoreChange;

    @Schema(description = "变动前分值", example = "100")
    private Integer beforeScore;

    @Schema(description = "变动后分值", example = "105")
    private Integer afterScore;

    @Schema(description = "当前信用分", example = "110")
    private Integer currentScore;

    @Schema(description = "当前信用等级，按信用等级业务字典展示，例如 WARNING、NORMAL、EXCELLENT、DISABLED", example = "NORMAL")
    private String creditLevel;

    @Schema(description = "触发类型，例如 AUTO 系统自动触发、MANUAL 人工调整", example = "AUTO")
    private String triggerType;

    @Schema(description = "业务类型，例如 ORDER 订单、REVIEW 评价、APPEAL 申诉、COMPLAINT 投诉", example = "REVIEW")
    private String bizType;

    @Schema(description = "业务 ID，关联触发信用变动的订单、评价或申诉记录", example = "1024")
    private Long bizId;

    @Schema(description = "备注，记录加减分原因说明")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
