package cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Schema(description = "管理后台 - 平台配置 新增或修改 Request VO")
public class PlatformConfigSaveReqVO {

    @Schema(description = "主键 ID")
    private Long id;

    @NotBlank(message = "配置分类不能为空")
    @Size(max = 64, message = "配置分类不能超过 64 个字符")
    @Schema(description = "平台配置分类编码，用于管理端按业务域分组展示")
    private String category;

    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称不能超过 100 个字符")
    @Schema(description = "配置项名称")
    private String name;

    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键不能超过 100 个字符")
    @Schema(description = "平台配置键；同一租户内唯一")
    private String key;

    @NotBlank(message = "配置值不能为空")
    @Size(max = 4000, message = "配置值不能超过 4000 个字符")
    @Schema(description = "平台配置值；敏感配置不会通过普通业务接口明文返回")
    private String value;

    @NotNull(message = "是否可见不能为空")
    @Schema(description = "是否对前端业务侧可见：true 可见、false 仅管理端可见")
    private Boolean visible;

    @Schema(description = "业务备注")
    private String remark;
}
