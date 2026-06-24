package cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 推广员详情 Response VO")
@Data
public class PromoterDetailRespVO {

    private Long id;
    private Long userId;
    private String levelCode;
    private String inviteCode;
    private String inviteUrl;
    private Integer bindUserCount;
    private Integer convertCount;
    private BigDecimal totalCommissionAmount;
    private BigDecimal availableCommissionAmount;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private UserRespVO user;
    private SummaryRespVO summary;
    private List<RelationRespVO> recentRelations;
    private List<CommissionRespVO> recentCommissionOrders;

    @Data
    public static class UserRespVO {
        private Long id;
        private String userNo;
        private String mobile;
        private String nickname;
        private String currentRoleCode;
        private String status;
        private LocalDateTime lastLoginTime;
    }

    @Data
    public static class SummaryRespVO {
        private Integer relationCount;
        private Integer convertedRelationCount;
        private Integer pendingCommissionCount;
        private Integer settledCommissionCount;
        private Integer invalidCommissionCount;
        private BigDecimal pendingCommissionAmount;
        private BigDecimal settledCommissionAmount;
    }

    @Data
    public static class RelationRespVO {
        private Long id;
        private Long userId;
        private String userNo;
        private String userNickname;
        private String userMobile;
        private LocalDateTime bindTime;
        private Long firstOrderId;
        private String firstOrderNo;
        private String convertStatus;
        private LocalDateTime createTime;
    }

    @Data
    public static class CommissionRespVO {
        private Long id;
        private String commissionNo;
        private Long userId;
        private String userNo;
        private String userNickname;
        private String userMobile;
        private Long sourceOrderId;
        private String sourceOrderNo;
        private Long sourceUnitId;
        private String sourceUnitNo;
        private String commissionType;
        private BigDecimal commissionAmount;
        private String status;
        private LocalDateTime settleTime;
        private LocalDateTime createTime;
    }
}
