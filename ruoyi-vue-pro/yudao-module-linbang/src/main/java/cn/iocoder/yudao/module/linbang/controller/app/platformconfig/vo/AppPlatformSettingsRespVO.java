package cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 平台设置 Response VO")
@Data
public class AppPlatformSettingsRespVO {

    @Schema(description = "客服电话")
    private String serviceHotline;

    @Schema(description = "客服微信")
    private String serviceWechat;

    @Schema(description = "关于我们")
    private String aboutUs;

    @Schema(description = "iOS 下载地址")
    private String iosDownloadUrl;

    @Schema(description = "Android 下载地址")
    private String androidDownloadUrl;

    @Schema(description = "消息提示语")
    private String messageNotice;

    @Schema(description = "反馈分类")
    private List<String> feedbackTypes;
}
