package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 合作商入驻初审 Response VO")
@Data
public class AppPartnerEntryAuditRespVO {

    @Schema(description = "入驻申请 ID", example = "1")
    private Long id;

    @Schema(description = "服务商 ID", example = "1001")
    private Long merchantId;

    @Schema(description = "申请用户 ID", example = "2001")
    private Long userId;

    @Schema(description = "申请用户编号", example = "LBU202606280001")
    private String userNo;

    @Schema(description = "申请用户昵称", example = "张三")
    private String userNickname;

    @Schema(description = "申请用户手机号", example = "13800138000")
    private String userMobile;

    @Schema(description = "服务商名称", example = "南山保洁服务站")
    private String merchantName;

    @Schema(description = "入驻单号", example = "ME202606280001")
    private String entryNo;

    @Schema(description = "区域编码", example = "440305")
    private String regionCode;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
    private String firstAuditStatus;

    @Schema(description = "初审人 ID", example = "1")
    private Long firstAuditBy;

    @Schema(description = "初审时间")
    private LocalDateTime firstAuditTime;

    @Schema(description = "初审意见/备注")
    private String remark;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, example = "PENDING")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
