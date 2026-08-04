package cn.iocoder.yudao.module.infra.controller.app.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Schema(description = "用户 App - 创建文件 Request VO")
@Data
public class AppFileCreateReqVO {

    @NotNull(message = "文件配置编号不能为空")
    @Schema(description = "文件配置编号。通常取 presigned-url 接口返回结果中的 configId", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    @NotBlank(message = "文件路径不能为空")
    @Schema(description = "文件路径。通常取 presigned-url 接口返回结果中的 path", requiredMode = Schema.RequiredMode.REQUIRED, example = "linbang/order/20260628/test.jpg")
    private String path;

    @Schema(description = "兼容字段，服务端以预签名预约中的原文件名为准", example = "test.jpg")
    private String name;

    @Schema(description = "兼容字段，服务端忽略客户端 URL 并生成最终访问地址", example = "https://cdn.example.com/linbang/order/20260628/test.jpg")
    private String url;

    @Schema(description = "兼容字段，服务端按真实文件字节重新识别 MIME 类型", example = "image/jpeg")
    private String type;

    @Schema(description = "兼容字段，服务端按对象存储中的真实字节数校验", example = "2048")
    private Long size;
}
