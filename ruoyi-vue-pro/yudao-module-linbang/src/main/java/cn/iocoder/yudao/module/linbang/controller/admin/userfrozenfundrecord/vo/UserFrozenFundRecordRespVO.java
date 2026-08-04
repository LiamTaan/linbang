package cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 用户资金冻结 Response VO")
public class UserFrozenFundRecordRespVO {

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
    @Schema(description = "钱包账户 ID，关联用户对应角色的钱包账户")
    private Long walletAccountId;
    @Schema(description = "冻结资金金额，单位：元，保留两位小数")
    private BigDecimal frozenAmount;
    @Schema(description = "已解冻金额，单位：元，保留两位小数")
    private BigDecimal releasedAmount;
    @Schema(description = "资金冻结状态：ACTIVE 冻结中、RELEASED 已解冻")
    private String status;
    @Schema(description = "来源业务类型，例如 ORDER 订单、ORDER_UNIT 订单单元、RISK_EVENT 风险事件")
    private String sourceBizType;
    @Schema(description = "来源业务对象 ID；由 sourceBizType 指明对象类型")
    private Long sourceBizId;
    @Schema(description = "业务原因说明")
    private String reason;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
