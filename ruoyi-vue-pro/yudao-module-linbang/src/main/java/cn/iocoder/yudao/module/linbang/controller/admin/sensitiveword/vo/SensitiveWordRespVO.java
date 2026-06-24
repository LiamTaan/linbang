package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 敏感词表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SensitiveWordRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "18533")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "关键词", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("关键词")
    private String word;

    @Schema(description = "词库类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("词库类型")
    private String wordType;

    @Schema(description = "匹配方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("匹配方式")
    private String matchType;

    @Schema(description = "拦截级别", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("拦截级别")
    private String blockLevel;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}