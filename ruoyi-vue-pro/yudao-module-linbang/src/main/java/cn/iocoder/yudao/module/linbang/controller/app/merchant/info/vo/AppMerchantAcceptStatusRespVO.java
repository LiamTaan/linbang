package cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "用户 App - 服务商接单状态 Response VO")
@Data
public class AppMerchantAcceptStatusRespVO {

    @Schema(description = "服务商 ID", example = "1")
    private Long merchantId;

    @Schema(description = "服务商状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String merchantStatus;

    @Schema(description = "接单状态：ENABLE 可接单、DISABLE 暂停接单", example = "DISABLE")
    private String acceptStatus;

    @Schema(description = "当前是否允许接单", example = "false")
    private Boolean acceptEligible;

    @Schema(description = "当前阻塞原因", example = "终审已通过，但尚未绑定有效银行卡")
    private String blockedReason;

    @Schema(description = "下一步动作提示", example = "请先绑定有效银行卡")
    private String nextAction;

    @Schema(description = "是否参与系统派单")
    private Boolean dispatchEnabled;

    @Schema(description = "最大接单半径，单位公里")
    private BigDecimal maxAcceptRadiusKm;

    @Schema(description = "是否开启语音提醒")
    private Boolean voiceRemindEnabled;

    @Schema(description = "是否在优先池")
    private Boolean priorityPoolFlag;

    @Schema(description = "平台服装认证是否生效")
    private Boolean platformClothingCertified;

    @Schema(description = "工具箱认证是否生效")
    private Boolean toolboxCertified;

    @Schema(description = "当前有效的晒单优先是否生效")
    private Boolean showcaseRewardActive;

    @Schema(description = "当前操作者是否主账号", example = "true")
    private Boolean mainAccountOperator;

    @Schema(description = "当前操作者权限编码列表，仅子账号有值")
    private List<String> operatorPermissionCodes;

    @Schema(description = "当前操作者可见服务点范围，仅子账号有值")
    private List<Long> visibleServicePointIds;

    @Schema(description = "最近推送记录")
    private List<RecentPushItem> recentPushes;

    @Data
    public static class RecentPushItem {
        @Schema(description = "单元ID")
        private Long unitId;

        @Schema(description = "阶段号")
        private Integer stageNo;

        @Schema(description = "批次号")
        private Integer pushBatchNo;

        @Schema(description = "剩余倒计时秒数")
        private Integer countdownSeconds;

        @Schema(description = "推送时间")
        private java.time.LocalDateTime pushTime;
    }

}
