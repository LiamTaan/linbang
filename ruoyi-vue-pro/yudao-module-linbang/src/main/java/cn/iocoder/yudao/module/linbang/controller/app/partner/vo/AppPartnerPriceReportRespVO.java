package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 合作商价格申报 Response VO")
@Data
public class AppPartnerPriceReportRespVO {

    @Schema(description = "价格申报 ID", example = "1")
    private Long id;

    @Schema(description = "服务商 ID", example = "1001")
    private Long merchantId;

    @Schema(description = "服务商名称", example = "南山保洁服务站")
    private String merchantName;

    @Schema(description = "合作商 ID", example = "2001")
    private Long partnerId;

    @Schema(description = "类目 ID", example = "3001")
    private Long categoryId;

    @Schema(description = "类目名称", example = "家庭保洁")
    private String categoryName;

    @Schema(description = "区域编码", example = "440305")
    private String regionCode;

    @Schema(description = "建议价格，单位元", example = "128.00")
    private BigDecimal suggestedPrice;

    @Schema(description = "申报备注")
    private String remark;

    @Schema(description = "申报状态：PENDING 待平台审核、APPROVED 已通过、REJECTED 已驳回、WITHDRAWN 已撤回", example = "PENDING")
    private String status;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
