package cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 评价详情 Response VO")
@Data
public class ReviewCommentDetailRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;
    @Schema(description = "订单 ID", example = "1001")
    private Long orderId;
    @Schema(description = "单元 ID", example = "2001")
    private Long unitId;
    @Schema(description = "评价发起人 ID", example = "5001")
    private Long fromUserId;
    @Schema(description = "评价目标用户 ID", example = "5002")
    private Long toUserId;
    @Schema(description = "星级", example = "5")
    private Integer starLevel;
    @Schema(description = "评价内容", example = "服务及时，态度很好")
    private String content;
    @Schema(description = "是否自动评价")
    private Boolean isAutoReview;
    @Schema(description = "自动评价文字是否已补充")
    private Boolean isContentSupplemented;
    @Schema(description = "评价可编辑截止时间")
    private LocalDateTime editDeadlineTime;
    @Schema(description = "最后编辑时间")
    private LocalDateTime lastEditTime;
    @Schema(description = "编辑次数")
    private Integer editCount;
    @Schema(description = OpenApiSchemaConstants.REVIEW_STATUS, example = "ENABLE")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "订单摘要")
    private OrderRespVO order;
    @Schema(description = "单元摘要")
    private UnitRespVO unit;
    @Schema(description = "评价发起人摘要")
    private UserRespVO fromUser;
    @Schema(description = "评价目标用户摘要")
    private UserRespVO toUser;
    @Schema(description = "目标服务商摘要")
    private MerchantRespVO toMerchant;
    @Schema(description = "统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "同单关联评价")
    private List<RelatedReviewRespVO> relatedReviews;
    @Schema(description = "由本次评价触发的信用记录")
    private List<CreditRecordRespVO> creditRecords;

    @Data
    public static class OrderRespVO {
        @Schema(description = "订单 ID", example = "1001")
        private Long id;
        @Schema(description = "订单号", example = "LBO202606260001")
        private String orderNo;
        @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "FINISHED")
        private String status;
        @Schema(description = "下单用户 ID", example = "5001")
        private Long userId;
        @Schema(description = "承接服务商 ID", example = "3001")
        private Long merchantId;
        @Schema(description = "订单金额")
        private BigDecimal orderAmount;
    }

    @Data
    public static class UnitRespVO {
        @Schema(description = "单元 ID", example = "2001")
        private Long id;
        @Schema(description = "订单 ID", example = "1001")
        private Long orderId;
        @Schema(description = "单元号", example = "LBO202606260001-1")
        private String unitNo;
        @Schema(description = "单元序号", example = "1")
        private Integer unitSeq;
        @Schema(description = "单元标题", example = "厨房油烟机清洗")
        private String unitTitle;
        @Schema(description = "单元金额")
        private BigDecimal unitAmount;
        @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "FINISHED")
        private String status;
        @Schema(description = "是否锁单")
        private Boolean isLocked;
        @Schema(description = "锁单原因", example = "申诉处理中")
        private String lockReason;
        @Schema(description = "承接服务商 ID", example = "3001")
        private Long merchantId;
        @Schema(description = "接单截止时间")
        private LocalDateTime acceptDeadlineTime;
        @Schema(description = "完成时间")
        private LocalDateTime finishTime;
    }

    @Data
    public static class UserRespVO {
        @Schema(description = "用户 ID", example = "5001")
        private Long id;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "手机号", example = "13800138000")
        private String mobile;
        @Schema(description = "昵称", example = "邻里用户")
        private String nickname;
        @Schema(description = "当前角色编码", example = "USER")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "最近登录时间")
        private LocalDateTime lastLoginTime;
    }

    @Data
    public static class MerchantRespVO {
        @Schema(description = "服务商 ID", example = "3001")
        private Long id;
        @Schema(description = "关联用户 ID", example = "5002")
        private Long userId;
        @Schema(description = "服务商名称", example = "安心家政")
        private String merchantName;
        @Schema(description = "联系人", example = "李师傅")
        private String contactName;
        @Schema(description = "联系手机", example = "13800138001")
        private String contactMobile;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
        private String acceptStatus;
        @Schema(description = "信用分")
        private Integer creditScore;
        @Schema(description = "信用等级", example = "NORMAL")
        private String creditLevel;
    }

    @Data
    public static class SummaryRespVO {
        @Schema(description = "同订单评价数")
        private Integer sameOrderReviewCount;
        @Schema(description = "同目标用户评价数")
        private Integer sameTargetReviewCount;
        @Schema(description = "好评数（4-5 星）")
        private Integer positiveReviewCount;
        @Schema(description = "中评数（3 星）")
        private Integer neutralReviewCount;
        @Schema(description = "差评数（1-2 星）")
        private Integer negativeReviewCount;
        @Schema(description = "自动评价数")
        private Integer autoReviewCount;
        @Schema(description = "关联信用记录数")
        private Integer creditRecordCount;
    }

    @Data
    public static class RelatedReviewRespVO {
        @Schema(description = "评价 ID", example = "1")
        private Long id;
        @Schema(description = "发起人 ID", example = "5001")
        private Long fromUserId;
        @Schema(description = "目标用户 ID", example = "5002")
        private Long toUserId;
        @Schema(description = "星级", example = "5")
        private Integer starLevel;
        @Schema(description = "是否自动评价")
        private Boolean isAutoReview;
        @Schema(description = OpenApiSchemaConstants.REVIEW_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "评价内容", example = "服务及时，态度很好")
        private String content;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class CreditRecordRespVO {
        @Schema(description = "信用记录 ID", example = "1")
        private Long id;
        @Schema(description = "规则编码", example = "ORDER_FINISHED_POSITIVE")
        private String ruleCode;
        @Schema(description = "规则名称", example = "订单完结且评价良好")
        private String ruleName;
        @Schema(description = "分值变动", example = "5")
        private Integer scoreChange;
        @Schema(description = "变动前信用分", example = "95")
        private Integer beforeScore;
        @Schema(description = "变动后信用分", example = "100")
        private Integer afterScore;
        @Schema(description = OpenApiSchemaConstants.CREDIT_TRIGGER_TYPE, example = "AUTO")
        private String triggerType;
        @Schema(description = OpenApiSchemaConstants.CREDIT_BIZ_TYPE, example = "REVIEW")
        private String bizType;
        @Schema(description = "业务 ID", example = "1")
        private Long bizId;
        @Schema(description = "备注", example = "订单完结且评价良好")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
