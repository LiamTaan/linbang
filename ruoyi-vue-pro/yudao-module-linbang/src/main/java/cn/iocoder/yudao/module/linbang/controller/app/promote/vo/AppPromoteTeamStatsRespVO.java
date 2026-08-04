package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 两级分销统计 Response VO")
@Data
public class AppPromoteTeamStatsRespVO {

    @Schema(description = "一级人数", example = "12")
    private Integer firstLevelUserCount;

    @Schema(description = "一级转化人数", example = "5")
    private Integer firstLevelConvertCount;

    @Schema(description = "一级累计收益", example = "88.80")
    private BigDecimal firstLevelCommissionAmount;

    @Schema(description = "二级人数", example = "30")
    private Integer secondLevelUserCount;

    @Schema(description = "二级转化人数", example = "8")
    private Integer secondLevelConvertCount;

    @Schema(description = "二级佣金金额；当前二级团队仅统计不结算，固定为 0", example = "0.00")
    private BigDecimal secondLevelCommissionAmount;

    @Schema(description = "最近转化记录")
    private List<RecentConvertRespVO> recentConverts;

    @Data
    @Schema(description = "用户 App - 最近转化摘要 Response VO")
    public static class RecentConvertRespVO {
        @Schema(description = "关联用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "层级", example = "FIRST")
        private String level;
        @Schema(description = "绑定时间")
        private LocalDateTime bindTime;
        @Schema(description = "首单 ID", example = "2001")
        private Long firstOrderId;
        @Schema(description = "转化状态", example = "CONVERTED")
        private String convertStatus;
    }
}
