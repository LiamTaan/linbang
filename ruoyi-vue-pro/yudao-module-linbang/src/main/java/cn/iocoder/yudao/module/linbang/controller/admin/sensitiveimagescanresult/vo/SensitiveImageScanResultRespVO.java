package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 敏感图片扫描 Response VO")
public class SensitiveImageScanResultRespVO {

    @Schema(description = "主键 ID")
    private Long id;
    @Schema(description = "内容检测场景编码，例如 MESSAGE 消息、COMMENT 评价、PROMOTE 推广内容")
    private String sceneType;
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;
    @Schema(description = "平台用户业务编号")
    private String userNo;
    @Schema(description = "用户昵称")
    private String userNickname;
    @Schema(description = "用户手机号")
    private String userMobile;
    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;
    @Schema(description = "关联业务对象 ID；由 bizType 指明对象类型")
    private Long bizId;
    @Schema(description = "文件 ID，关联文件中心文件")
    private Long fileId;
    @Schema(description = "原始图片文件地址；仅限有权限的管理端查看")
    private String sourceFileUrl;
    @Schema(description = "脱敏后的图片访问地址")
    private String maskedFileUrl;
    @Schema(description = "图片 OCR 识别文本")
    private String ocrText;
    @Schema(description = "图片中识别出的二维码内容")
    private String qrContent;
    @Schema(description = "命中的敏感词集合，多个词以英文逗号分隔")
    private String hitWords;
    @Schema(description = "图片扫描状态：SUCCESS 扫描成功、HIT 命中敏感内容、FAILED 扫描失败")
    private String scanStatus;
    @Schema(description = "处理失败原因")
    private String failureReason;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
