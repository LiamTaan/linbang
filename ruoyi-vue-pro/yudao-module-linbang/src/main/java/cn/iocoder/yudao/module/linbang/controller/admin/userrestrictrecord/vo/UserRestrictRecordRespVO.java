package cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户限制记录 Response VO")
@Data
public class UserRestrictRecordRespVO {

    @Schema(description = "主键 ID")
    private Long id;
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;
    @Schema(description = "平台用户业务编号")
    private String userNo;
    @Schema(description = "用户昵称")
    private String userNickname;
    @Schema(description = "用户手机号")
    private String userMobile;
    @Schema(description = "用户限制类型：RESTRICT_PUBLISH 限制发单、RESTRICT_ACCEPT 限制接单；其他值按风控限制字典扩展")
    private String restrictType;
    @Schema(description = "用户限制状态：ACTIVE 生效中、RELEASED 已解除、EXPIRED 已过期")
    private String status;
    @Schema(description = "业务生效开始时间")
    private LocalDateTime startTime;
    @Schema(description = "业务结束时间；为空表示尚未结束或长期有效")
    private LocalDateTime endTime;
    @Schema(description = "产生本次信用变动的规则编码")
    private String sourceRuleCode;
    @Schema(description = "来源业务类型，例如 ORDER 订单、ORDER_UNIT 订单单元、RISK_EVENT 风险事件")
    private String sourceBizType;
    @Schema(description = "来源业务对象 ID；由 sourceBizType 指明对象类型")
    private Long sourceBizId;
    @Schema(description = "业务原因说明")
    private String reason;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
