package cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 平台配置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PlatformConfigRespVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("配置分类")
    private String category;

    @ExcelProperty("配置名称")
    private String name;

    @ExcelProperty("配置键")
    private String key;

    @ExcelProperty("配置值")
    private String value;

    @ExcelProperty("是否可见")
    private Boolean visible;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
