package cn.iocoder.yudao.framework.swagger.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

/**
 * Swagger 配置属性
 *
 * @author 芋道源码
 */
@ConfigurationProperties("yudao.swagger")
@Data
public class SwaggerProperties {

    /**
     * 标题
     */
    @NotEmpty(message = "标题不能为空")
    private String title;
    /**
     * 描述
     */
    @NotEmpty(message = "描述不能为空")
    private String description;
    /**
     * 作者
     */
    @NotEmpty(message = "作者不能为空")
    private String author;
    /**
     * 版本
     */
    @NotEmpty(message = "版本不能为空")
    private String version;
    /**
     * url
     */
    @NotEmpty(message = "扫描的 package 不能为空")
    private String url;
    /**
     * email
     */
    @NotEmpty(message = "扫描的 email 不能为空")
    private String email;

    /**
     * license
     */
    @NotEmpty(message = "扫描的 license 不能为空")
    private String license;

    /**
     * license-url
     */
    @NotEmpty(message = "扫描的 license-url 不能为空")
    private String licenseUrl;

    /**
     * 是否在 OpenAPI 中导出全局安全方案。
     *
     * 关闭后，不再向文档写入 Authorization 的 security scheme，
     * 可避免 Apifox 导入后为每个接口生成 Auth 配置。
     */
    private boolean enableSecurityScheme = true;

    /**
     * 是否为每个接口自动注入全局 Header 参数。
     *
     * 关闭后，不再为每个接口追加 tenant-id / Authorization 请求头参数，
     * 可避免 Apifox 导入后重复生成局部 Header 配置。
     */
    private boolean enableGlobalHeaderParameters = true;

}
