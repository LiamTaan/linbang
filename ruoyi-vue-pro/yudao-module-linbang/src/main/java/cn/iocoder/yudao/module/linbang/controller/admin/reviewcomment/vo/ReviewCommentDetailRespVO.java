package cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 评价详情 Response VO")
@Data
public class ReviewCommentDetailRespVO {

    private Long id;
    private Long orderId;
    private Long unitId;
    private Long fromUserId;
    private Long toUserId;
    private Integer starLevel;
    private String content;
    private Boolean isAutoReview;
    private String status;
    private LocalDateTime createTime;
    private OrderRespVO order;
    private UnitRespVO unit;
    private UserRespVO fromUser;
    private UserRespVO toUser;
    private MerchantRespVO toMerchant;
    private SummaryRespVO summary;
    private List<RelatedReviewRespVO> relatedReviews;
    private List<CreditRecordRespVO> creditRecords;

    @Data
    public static class OrderRespVO {
        private Long id;
        private String orderNo;
        private String title;
        private String status;
        private Long userId;
        private Long merchantId;
        private BigDecimal orderAmount;
    }

    @Data
    public static class UnitRespVO {
        private Long id;
        private Long orderId;
        private String unitNo;
        private Integer unitSeq;
        private String unitTitle;
        private BigDecimal unitAmount;
        private String status;
        private Boolean isLocked;
        private String lockReason;
        private Long merchantId;
        private LocalDateTime acceptDeadlineTime;
        private LocalDateTime finishTime;
    }

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
    public static class MerchantRespVO {
        private Long id;
        private Long userId;
        private String merchantName;
        private String contactName;
        private String contactMobile;
        private String status;
        private String acceptStatus;
        private Integer creditScore;
        private String creditLevel;
    }

    @Data
    public static class SummaryRespVO {
        private Integer sameOrderReviewCount;
        private Integer sameTargetReviewCount;
        private Integer positiveReviewCount;
        private Integer neutralReviewCount;
        private Integer negativeReviewCount;
        private Integer autoReviewCount;
        private Integer creditRecordCount;
    }

    @Data
    public static class RelatedReviewRespVO {
        private Long id;
        private Long fromUserId;
        private Long toUserId;
        private Integer starLevel;
        private Boolean isAutoReview;
        private String status;
        private String content;
        private LocalDateTime createTime;
    }

    @Data
    public static class CreditRecordRespVO {
        private Long id;
        private String ruleCode;
        private String ruleName;
        private Integer scoreChange;
        private Integer beforeScore;
        private Integer afterScore;
        private String triggerType;
        private String bizType;
        private Long bizId;
        private String remark;
        private LocalDateTime createTime;
    }
}
