package cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 服务商入驻申请 Response VO")
@Data
public class AppMerchantEntryRespVO {

    @Schema(description = "入驻申请 ID", example = "1")
    private Long id;

    @Schema(description = "服务商 ID", example = "1")
    private Long merchantId;

    @Schema(description = "入驻单号", example = "LBE2069000000000000000")
    private String entryNo;

    @Schema(description = "区域编码", example = "310115")
    private String regionCode;

    @Schema(description = "服务商名称", example = "LinBang Services")
    private String merchantName;

    @Schema(description = "联系人", example = "Alice")
    private String contactName;

    @Schema(description = "联系人手机号", example = "13800138000")
    private String contactMobile;

    @Schema(description = "服务范围说明", example = "Home repair and cleaning")
    private String serviceScopeDesc;

    @Schema(description = "银行卡 ID", example = "1")
    private Long bankCardId;

    @Schema(description = "服务类目 ID 列表")
    private List<Long> serviceCategoryIds;

    @Schema(description = "资质 ID 列表")
    private List<Long> qualificationIds;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String firstAuditStatus;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String finalAuditStatus;

    @Schema(description = "入驻状态：PENDING 待审核、FIRST_APPROVED 初审通过、APPROVED 终审通过、REJECTED 已驳回", example = "PENDING")
    private String status;

    @Schema(description = "统一进度状态：PENDING_FIRST_AUDIT、PENDING_FINAL_AUDIT、APPROVED_WAIT_BANK_CARD、APPROVED_ENABLED、REJECTED", example = "PENDING_FIRST_AUDIT")
    private String progressStatus;

    @Schema(description = "当前阶段名称", example = "待平台初审")
    private String currentStageName;

    @Schema(description = "当前阶段时间")
    private LocalDateTime currentStageTime;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "当前阻塞原因")
    private String onboardingBlockedReason;

    @Schema(description = "是否已开通接单", example = "false")
    private Boolean acceptEnabled;

    @Schema(description = "终审后是否必须先绑卡", example = "true")
    private Boolean bankCardRequired;

    @Schema(description = "接单状态：ENABLE 可接单、DISABLE 暂停接单", example = "DISABLE")
    private String acceptStatus;

    @Schema(description = "备注", example = "待平台审核")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
