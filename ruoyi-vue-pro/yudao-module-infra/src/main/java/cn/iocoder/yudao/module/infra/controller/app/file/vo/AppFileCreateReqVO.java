package cn.iocoder.yudao.module.infra.controller.app.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 创建文件 Request VO")
@Data
public class AppFileCreateReqVO {

    @NotNull(message = "文件配置编号不能为空")
    @Schema(description = "文件配置编号。通常取 presigned-url 接口返回结果中的 configId", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    @NotNull(message = "文件路径不能为空")
    @Schema(description = "文件路径。通常取 presigned-url 接口返回结果中的 path", requiredMode = Schema.RequiredMode.REQUIRED, example = "linbang/order/20260628/test.jpg")
    private String path;

    @NotNull(message = "原文件名不能为空")
    @Schema(description = "原文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "test.jpg")
    private String name;

    @NotNull(message = "文件 URL不能为空")
    @Schema(description = "文件访问地址。通常取前端直传成功后的最终访问 URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://cdn.example.com/linbang/order/20260628/test.jpg")
    private String url;

    @Schema(description = "文件 MIME 类型", example = "image/jpeg")
    private String type;

    @Schema(description = "文件大小，单位字节", example = "2048")
    private Long size;
}
