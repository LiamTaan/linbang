package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 信用记录 Response VO")
@Data
public class AppCreditRecordRespVO {

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

    @Schema(description = "触发类型", example = "AUTO")
    private String triggerType;

    @Schema(description = "业务类型", example = "REVIEW")
    private String bizType;

    @Schema(description = "业务 ID", example = "1024")
    private Long bizId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
