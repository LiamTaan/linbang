package cn.iocoder.yudao.module.infra.controller.app.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 上传文件 Response VO")
@Data
public class AppFileUploadRespVO {

    @Schema(description = "文件 ID。首页发布需求等需要 attachmentFileIds 的业务，直接使用该值", example = "1024")
    private Long fileId;

    @Schema(description = "文件访问地址", example = "https://cdn.example.com/linbang/order/20260628/test.jpg")
    private String url;
}
