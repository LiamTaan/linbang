package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 信用信息 Response VO")
@Data
public class AppReviewCreditRespVO {

    @Schema(description = "当前信用分", example = "100")
    private Integer creditScore;

    @Schema(description = "当前信用等级，按信用等级业务字典展示，例如 NORMAL、GOOD、EXCELLENT", example = "NORMAL")
    private String creditLevel;

    @Schema(description = "下一等级编码，为空表示已是最高等级", example = "EXCELLENT")
    private String nextLevelCode;

    @Schema(description = "到下一等级还需补足的分值", example = "20")
    private Integer nextLevelNeedScore;

    @Schema(description = "信用记录总数", example = "12")
    private Long recordCount;

    @Schema(description = "最近信用记录")
    private List<RecentRecordItem> recentRecords;

    @Schema(description = "信用规则列表")
    private List<CreditRuleItem> rules;

    @Schema(description = "信用等级权益列表")
    private List<CreditBenefitItem> benefits;

    @Data
    public static class CreditRuleItem {

        @Schema(description = "规则编码")
        private String ruleCode;

        @Schema(description = "规则名称")
        private String ruleName;

        @Schema(description = "分值变动")
        private Integer scoreChange;

        @Schema(description = OpenApiSchemaConstants.CREDIT_TRIGGER_TYPE, example = "AUTO")
        private String triggerType;

        @Schema(description = "信用规则状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
        private String status;
    }

    @Data
    public static class RecentRecordItem {

        @Schema(description = "主键", example = "1")
        private Long id;

        @Schema(description = "规则编码", example = "ORDER_FINISHED_POSITIVE")
        private String ruleCode;

        @Schema(description = "规则名称", example = "订单完结好评加分")
        private String ruleName;

        @Schema(description = "分值变动", example = "5")
        private Integer scoreChange;

        @Schema(description = "变动后分值", example = "105")
        private Integer afterScore;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class CreditBenefitItem {

        @Schema(description = "等级编码", example = "NORMAL")
        private String levelCode;

        @Schema(description = "等级名称", example = "正常")
        private String levelName;

        @Schema(description = "权益标题", example = "常规排序")
        private String benefitTitle;

        @Schema(description = "权益说明", example = "可参与平台标准排序")
        private String benefitDesc;

        @Schema(description = "排序号", example = "1")
        private Integer sortNo;
    }
}
