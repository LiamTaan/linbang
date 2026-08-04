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
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("配置分类")
    @Schema(description = "平台配置分类编码，用于管理端按业务域分组展示")
    private String category;

    @ExcelProperty("配置名称")
    @Schema(description = "配置项名称")
    private String name;

    @ExcelProperty("配置键")
    @Schema(description = "平台配置键；同一租户内唯一")
    private String key;

    @ExcelProperty("配置值")
    @Schema(description = "平台配置值；敏感配置不会通过普通业务接口明文返回")
    private String value;

    @ExcelProperty("是否可见")
    @Schema(description = "是否对前端业务侧可见：true 可见、false 仅管理端可见")
    private Boolean visible;

    @ExcelProperty("备注")
    @Schema(description = "业务备注")
    private String remark;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
