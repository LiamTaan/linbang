package cn.iocoder.yudao.module.linbang.controller.app.benefit.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 我的权益总览 Response VO")
@Data
public class AppBenefitOverviewRespVO {

    @Schema(description = "优惠券数量；当前仓库未接入营销模块时固定返回 0", example = "0")
    private Integer couponCount;

    @Schema(description = "积分余额", example = "120")
    private Integer pointBalance;

    @Schema(description = "是否具备优先展示权益", example = "true")
    private Boolean priorityDisplayEnabled;

    @Schema(description = "是否具备优先派单权益", example = "false")
    private Boolean priorityDispatchEnabled;

    @Schema(description = "是否具备推广身份权益", example = "true")
    private Boolean promoterBenefitEnabled;

    @Schema(description = "权益摘要列表")
    private List<BenefitItemRespVO> benefits;

    @Schema(description = "最近积分记录摘要")
    private List<PointRecordSimpleRespVO> recentPointRecords;

    @Data
    public static class BenefitItemRespVO {
        @Schema(description = OpenApiSchemaConstants.BENEFIT_TYPE, example = "CREDIT_LEVEL")
        private String benefitType;

        @Schema(description = "权益标题", example = "高信用优先展示")
        private String benefitTitle;

        @Schema(description = "权益说明")
        private String benefitDesc;
    }

    @Data
    public static class PointRecordSimpleRespVO {
        @Schema(description = "积分记录 ID", example = "1")
        private Long id;

        @Schema(description = "记录标题", example = "签到")
        private String title;

        @Schema(description = "记录描述", example = "签到获得 10 积分")
        private String description;

        @Schema(description = "变动积分", example = "10")
        private Integer point;

        @Schema(description = "创建时间")
        private java.time.LocalDateTime createTime;
    }
}
