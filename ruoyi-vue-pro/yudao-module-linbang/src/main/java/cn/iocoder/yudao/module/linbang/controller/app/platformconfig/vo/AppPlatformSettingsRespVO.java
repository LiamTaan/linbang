package cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 平台设置 Response VO")
@Data
public class AppPlatformSettingsRespVO {

    @Schema(description = "客服电话", example = "400-800-1234")
    private String serviceHotline;

    @Schema(description = "客服微信", example = "linbang_service")
    private String serviceWechat;

    @Schema(description = "关于我们")
    private String aboutUs;

    @Schema(description = "iOS 下载地址", example = "https://download.linbang.cn/app/ios")
    private String iosDownloadUrl;

    @Schema(description = "Android 下载地址", example = "https://download.linbang.cn/app/android.apk")
    private String androidDownloadUrl;

    @Schema(description = "消息提示语", example = "请保持电话畅通，服务商接单后将尽快与您联系")
    private String messageNotice;

    @Schema(description = "反馈分类", example = "[\"功能建议\",\"异常反馈\",\"投诉建议\"]")
    private List<String> feedbackTypes;

    @Schema(description = "税务提醒文案", example = "无个体执照将按规则代扣个税")
    private String taxReminder;

    @Schema(description = "个体执照代办入口标题", example = "个体执照代办")
    private String licenseAgentEntryTitle;

    @Schema(description = "个体执照代办入口链接", example = "https://agent.linbang.cn/license")
    private String licenseAgentEntryUrl;

    @Schema(description = "订单价格明细是否展示。`true` 表示 App 订单详情默认展示基础价、附加费、合计等明细", example = "true")
    private Boolean orderPriceDetailEnabled;

    @Schema(description = "地方商城入口是否展示。`true` 表示订单详情可展示商城入口", example = "true")
    private Boolean mallEntryEnabled;

    @Schema(description = "地方商城入口标题", example = "地方商城")
    private String mallEntryTitle;

    @Schema(description = "地方商城入口链接", example = "https://mall.linbang.cn/local")
    private String mallEntryUrl;

    @Schema(description = "提现到账说明", example = "提现审核通过后预计 T+1 到账")
    private String withdrawNotice;
}
