package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 敏感词表新增/修改 Request VO")
@Data
public class SensitiveWordSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "18533")
    private Long id;

    @Schema(description = "关键词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "关键词不能为空")
    private String word;

    @Schema(description = "词库类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "词库类型不能为空")
    private String wordType;

    @Schema(description = "匹配方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "匹配方式不能为空")
    private String matchType;

    @Schema(description = "拦截级别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "拦截级别不能为空")
    private String blockLevel;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "状态不能为空")
    private String status;

}