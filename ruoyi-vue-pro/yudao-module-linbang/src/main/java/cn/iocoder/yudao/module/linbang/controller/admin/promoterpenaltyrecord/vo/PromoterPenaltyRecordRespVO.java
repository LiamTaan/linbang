package cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 推广员处罚记录 Response VO")
@Data
public class PromoterPenaltyRecordRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;
    @Schema(description = "推广员 ID，关联推广员档案")
    private Long promoterId;
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;
    @Schema(description = "平台用户业务编号")
    private String userNo;
    @Schema(description = "用户昵称")
    private String userNickname;
    @Schema(description = "用户手机号")
    private String userMobile;
    @Schema(description = "推广内容 ID，关联推广内容")
    private Long contentId;
    @Schema(description = "推广内容标题")
    private String contentTitle;
    @Schema(description = "处罚动作：DEMOTE 降级、RESTRICT_PROMOTE 限制推广、DISABLE_PROMOTER 停用推广员")
    private String penaltyAction;
    @Schema(description = "信用分变动值；正数加分、负数扣分")
    private Integer scoreChange;
    @Schema(description = "业务原因说明")
    private String reason;
    @Schema(description = "推广员处罚状态：ACTIVE 生效中、RELEASED 已解除")
    private String status;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
