package cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 服务商信息表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MerchantInfoRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "966")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20968")
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

    @Schema(description = "服务商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @ExcelProperty("服务商名称")
    private String merchantName;

    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @ExcelProperty("联系人")
    private String contactName;

    @Schema(description = "联系人手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("联系人手机号")
    private String contactMobile;

    @Schema(description = "服务范围说明")
    @ExcelProperty("服务范围说明")
    private String serviceScopeDesc;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @ExcelProperty("接单状态")
    private String acceptStatus;

    @Schema(description = "信用分", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("信用分")
    private Integer creditScore;

    @Schema(description = "信用等级")
    @ExcelProperty("信用等级")
    private String creditLevel;

    @Schema(description = "综合评分，按已生效评价金额加权平均，保留 2 位小数")
    @ExcelProperty("综合评分")
    private BigDecimal compositeScore;

    @Schema(description = "好评率，取值 0-100，保留 2 位小数")
    @ExcelProperty("好评率")
    private BigDecimal positiveRate;

    @Schema(description = "是否在好评优先池中")
    @ExcelProperty("是否在好评优先池")
    private Boolean inPositivePriorityPool;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
