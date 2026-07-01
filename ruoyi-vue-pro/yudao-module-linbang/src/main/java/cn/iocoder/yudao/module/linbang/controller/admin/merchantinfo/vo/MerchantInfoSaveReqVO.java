package cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 服务商信息表新增/修改 Request VO")
@Data
public class MerchantInfoSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "966")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20968")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "服务商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @NotEmpty(message = "服务商名称不能为空")
    private String merchantName;

    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @NotEmpty(message = "联系人不能为空")
    private String contactName;

    @Schema(description = "联系人手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "联系人手机号不能为空")
    private String contactMobile;

    @Schema(description = "服务范围说明")
    private String serviceScopeDesc;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotEmpty(message = "接单状态不能为空")
    private String acceptStatus;

    @Schema(description = "是否参与系统自动派单", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "自动派单参与开关不能为空")
    private Boolean dispatchEnabled;

    @Schema(description = "信用分", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "信用分不能为空")
    private Integer creditScore;

    @Schema(description = "信用等级")
    private String creditLevel;

}
