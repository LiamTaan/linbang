package cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 佣金详情 Response VO")
@Data
public class CommissionOrderDetailRespVO {

    @Schema(description = "佣金单 ID", example = "1")
    private Long id;
    @Schema(description = "佣金单号", example = "CM202606260001")
    private String commissionNo;
    @Schema(description = "推广员 ID", example = "1001")
    private Long promoterId;
    @Schema(description = "被推广用户 ID", example = "5001")
    private Long userId;
    @Schema(description = "来源主订单 ID", example = "2001")
    private Long sourceOrderId;
    @Schema(description = "来源单元订单 ID", example = "3001")
    private Long sourceUnitId;
    @Schema(description = OpenApiSchemaConstants.COMMISSION_TYPE, example = "ORDER")
    private String commissionType;
    @Schema(description = "佣金金额", example = "38.50")
    private BigDecimal commissionAmount;
    @Schema(description = OpenApiSchemaConstants.COMMISSION_STATUS, example = "SETTLED")
    private String status;
    @Schema(description = "结算时间；未结算时为空")
    private LocalDateTime settleTime;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "推广员摘要")
    private PromoterRespVO promoter;
    @Schema(description = "被推广用户摘要")
    private UserRespVO user;
    @Schema(description = "来源主订单摘要")
    private OrderRespVO sourceOrder;
    @Schema(description = "来源单元订单摘要")
    private UnitRespVO sourceUnit;

    @Data
    public static class PromoterRespVO {
        @Schema(description = "推广员 ID", example = "1001")
        private Long id;
        @Schema(description = "关联用户 ID", example = "5001")
        private Long userId;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "用户昵称", example = "邻里推广员")
        private String userNickname;
        @Schema(description = "用户手机号", example = "13800138000")
        private String userMobile;
        @Schema(description = "推广等级编码", example = "P1")
        private String levelCode;
        @Schema(description = "邀请码", example = "PROMO1234")
        private String inviteCode;
        @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "累计佣金金额", example = "680.00")
        private BigDecimal totalCommissionAmount;
        @Schema(description = "可提现佣金金额", example = "320.00")
        private BigDecimal availableCommissionAmount;
    }

    @Data
    public static class UserRespVO {
        @Schema(description = "用户 ID", example = "5002")
        private Long id;
        @Schema(description = "用户编号", example = "LBU223456")
        private String userNo;
        @Schema(description = "手机号", example = "13900139000")
        private String mobile;
        @Schema(description = "昵称", example = "新注册用户")
        private String nickname;
        @Schema(description = "当前角色编码", example = "USER")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
    }

    @Data
    public static class OrderRespVO {
        @Schema(description = "主订单 ID", example = "2001")
        private Long id;
        @Schema(description = "订单编号", example = "LB202606260001")
        private String orderNo;
        @Schema(description = "下单用户 ID", example = "5002")
        private Long userId;
        @Schema(description = "接单服务商 ID", example = "8001")
        private Long merchantId;
        @Schema(description = "服务类目 ID", example = "101")
        private Long categoryId;
        @Schema(description = "订单总金额", example = "268.00")
        private BigDecimal orderAmount;
        @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
        private String pricingMode;
        @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_STATUS, example = "SPLIT")
        private String splitStatus;
        @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "FINISHED")
        private String status;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class UnitRespVO {
        @Schema(description = "单元订单 ID", example = "3001")
        private Long id;
        @Schema(description = "主订单 ID", example = "2001")
        private Long orderId;
        @Schema(description = "单元编号", example = "UNIT202606260001")
        private String unitNo;
        @Schema(description = "单元序号，从 1 开始", example = "1")
        private Integer unitSeq;
        @Schema(description = "单元标题", example = "上门清洗第一单元")
        private String unitTitle;
        @Schema(description = "单元金额", example = "268.00")
        private BigDecimal unitAmount;
        @Schema(description = "拆分方式，按拆单业务字典展示", example = "MANUAL")
        private String splitMode;
        @Schema(description = "是否已锁单", example = "false")
        private Boolean isLocked;
        @Schema(description = "锁单原因")
        private String lockReason;
        @Schema(description = "接单服务商 ID", example = "8001")
        private Long merchantId;
        @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "FINISHED")
        private String status;
        @Schema(description = "完结时间")
        private LocalDateTime finishTime;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
