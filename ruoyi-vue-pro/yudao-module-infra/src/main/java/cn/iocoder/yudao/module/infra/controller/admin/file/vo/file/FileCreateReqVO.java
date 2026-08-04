package cn.iocoder.yudao.module.infra.controller.admin.file.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Schema(description = "管理后台 - 文件创建 Request VO")
@Data
public class FileCreateReqVO {

    @NotNull(message = "文件配置编号不能为空")
    @Schema(description = "文件配置编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long configId;

    @NotBlank(message = "文件路径不能为空")
    @Schema(description = "文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao.jpg")
    private String path;

    @Schema(description = "兼容字段，服务端以预签名预约中的原文件名为准", example = "yudao.jpg")
    private String name;

    @Schema(description = "兼容字段，服务端忽略客户端 URL 并生成最终访问地址", example = "https://www.iocoder.cn/yudao.jpg")
    private String url;

    @Schema(description = "兼容字段，服务端按真实文件字节重新识别 MIME 类型", example = "application/octet-stream")
    private String type;

    @Schema(description = "兼容字段，服务端按对象存储中的真实字节数校验", example = "2048")
    private Long size;

}
