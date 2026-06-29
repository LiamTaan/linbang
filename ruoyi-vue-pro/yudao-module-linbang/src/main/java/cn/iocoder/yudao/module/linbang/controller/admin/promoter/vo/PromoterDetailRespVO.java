package cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 推广员详情 Response VO")
@Data
public class PromoterDetailRespVO {

    @Schema(description = "推广员 ID", example = "1001")
    private Long id;
    @Schema(description = "关联用户 ID", example = "5001")
    private Long userId;
    @Schema(description = "推广等级编码", example = "P1")
    private String levelCode;
    @Schema(description = "邀请码", example = "PROMO1234")
    private String inviteCode;
    @Schema(description = "邀请注册链接", example = "https://app.linbang.cn/invite/PROMO1234")
    private String inviteUrl;
    @Schema(description = "已绑定用户数", example = "28")
    private Integer bindUserCount;
    @Schema(description = "已转化下单用户数", example = "12")
    private Integer convertCount;
    @Schema(description = "累计佣金金额", example = "680.00")
    private BigDecimal totalCommissionAmount;
    @Schema(description = "可提现佣金金额", example = "320.00")
    private BigDecimal availableCommissionAmount;
    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "推广员关联用户摘要")
    private UserRespVO user;
    @Schema(description = "推广数据汇总")
    private SummaryRespVO summary;
    @Schema(description = "最近绑定关系")
    private List<RelationRespVO> recentRelations;
    @Schema(description = "最近佣金单")
    private List<CommissionRespVO> recentCommissionOrders;

    @Data
    public static class UserRespVO {
        @Schema(description = "用户 ID", example = "5001")
        private Long id;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "手机号", example = "13800138000")
        private String mobile;
        @Schema(description = "昵称", example = "邻里推广员")
        private String nickname;
        @Schema(description = "当前角色编码", example = "PROMOTER")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "最近登录时间")
        private LocalDateTime lastLoginTime;
    }

    @Data
    public static class SummaryRespVO {
        @Schema(description = "绑定关系总数", example = "28")
        private Integer relationCount;
        @Schema(description = "已转化关系数", example = "12")
        private Integer convertedRelationCount;
        @Schema(description = "待结算佣金单数", example = "3")
        private Integer pendingCommissionCount;
        @Schema(description = "已结算佣金单数", example = "9")
        private Integer settledCommissionCount;
        @Schema(description = "已失效佣金单数", example = "1")
        private Integer invalidCommissionCount;
        @Schema(description = "待结算佣金金额", example = "120.00")
        private BigDecimal pendingCommissionAmount;
        @Schema(description = "已结算佣金金额", example = "560.00")
        private BigDecimal settledCommissionAmount;
    }

    @Data
    public static class RelationRespVO {
        @Schema(description = "绑定关系 ID", example = "1")
        private Long id;
        @Schema(description = "被邀请用户 ID", example = "5002")
        private Long userId;
        @Schema(description = "被邀请用户编号", example = "LBU223456")
        private String userNo;
        @Schema(description = "被邀请用户昵称", example = "新注册用户")
        private String userNickname;
        @Schema(description = "被邀请用户手机号", example = "13900139000")
        private String userMobile;
        @Schema(description = "绑定时间")
        private LocalDateTime bindTime;
        @Schema(description = "首单订单 ID", example = "2001")
        private Long firstOrderId;
        @Schema(description = "首单订单编号", example = "LB202606260001")
        private String firstOrderNo;
        @Schema(description = "转化状态，按推广关系字典展示，常见值如 CONVERTED 已转化、PENDING 未转化", example = "CONVERTED")
        private String convertStatus;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class CommissionRespVO {
        @Schema(description = "佣金单 ID", example = "1")
        private Long id;
        @Schema(description = "佣金单号", example = "CM202606260001")
        private String commissionNo;
        @Schema(description = "被推广用户 ID", example = "5002")
        private Long userId;
        @Schema(description = "被推广用户编号", example = "LBU223456")
        private String userNo;
        @Schema(description = "被推广用户昵称", example = "新注册用户")
        private String userNickname;
        @Schema(description = "被推广用户手机号", example = "13900139000")
        private String userMobile;
        @Schema(description = "来源主订单 ID", example = "2001")
        private Long sourceOrderId;
        @Schema(description = "来源主订单编号", example = "LB202606260001")
        private String sourceOrderNo;
        @Schema(description = "来源单元订单 ID", example = "3001")
        private Long sourceUnitId;
        @Schema(description = "来源单元订单编号", example = "UNIT202606260001")
        private String sourceUnitNo;
        @Schema(description = OpenApiSchemaConstants.COMMISSION_TYPE, example = "ORDER")
        private String commissionType;
        @Schema(description = "佣金金额", example = "38.50")
        private BigDecimal commissionAmount;
        @Schema(description = OpenApiSchemaConstants.COMMISSION_STATUS, example = "SETTLED")
        private String status;
        @Schema(description = "结算时间")
        private LocalDateTime settleTime;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
