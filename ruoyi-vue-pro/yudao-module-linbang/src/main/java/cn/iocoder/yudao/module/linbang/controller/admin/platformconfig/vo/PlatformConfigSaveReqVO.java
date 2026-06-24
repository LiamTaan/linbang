package cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PlatformConfigSaveReqVO {

    private Long id;

    @NotBlank(message = "配置分类不能为空")
    @Size(max = 64, message = "配置分类不能超过 64 个字符")
    private String category;

    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称不能超过 100 个字符")
    private String name;

    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键不能超过 100 个字符")
    private String key;

    @NotBlank(message = "配置值不能为空")
    @Size(max = 4000, message = "配置值不能超过 4000 个字符")
    private String value;

    @NotNull(message = "是否可见不能为空")
    private Boolean visible;

    private String remark;
}
