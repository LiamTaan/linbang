package cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 服务商入驻申请表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MerchantEntryRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9209")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "服务商ID", example = "18167")
    @ExcelProperty("服务商ID")
    private Long merchantId;

    @Schema(description = "服务商名称")
    @ExcelProperty("服务商名称")
    private String merchantName;

    @Schema(description = "联系人")
    @ExcelProperty("联系人")
    private String merchantContactName;

    @Schema(description = "联系手机")
    @ExcelProperty("联系手机")
    private String merchantContactMobile;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7267")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "用户编号")
    @ExcelProperty("用户编号")
    private String userNo;

    @Schema(description = "用户昵称")
    @ExcelProperty("用户昵称")
    private String userNickname;

    @Schema(description = "用户手机号")
    @ExcelProperty("用户手机号")
    private String userMobile;

    @Schema(description = "入驻单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("入驻单号")
    private String entryNo;

    @Schema(description = "区域编码")
    @ExcelProperty("区域编码")
    private String regionCode;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @ExcelProperty("初审状态")
    private String firstAuditStatus;

    @Schema(description = "初审人")
    @ExcelProperty("初审人")
    private Long firstAuditBy;

    @Schema(description = "初审时间")
    @ExcelProperty("初审时间")
    private LocalDateTime firstAuditTime;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @ExcelProperty("终审状态")
    private String finalAuditStatus;

    @Schema(description = "终审人")
    @ExcelProperty("终审人")
    private Long finalAuditBy;

    @Schema(description = "终审时间")
    @ExcelProperty("终审时间")
    private LocalDateTime finalAuditTime;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "备注", example = "??")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
