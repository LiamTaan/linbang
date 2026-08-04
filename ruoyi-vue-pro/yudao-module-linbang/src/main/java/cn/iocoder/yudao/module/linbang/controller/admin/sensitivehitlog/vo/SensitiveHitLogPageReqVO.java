package cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "管理后台 - 敏感内容命中 分页查询 Request VO")
public class SensitiveHitLogPageReqVO extends PageParam {

    @Schema(description = "内容检测场景编码，例如 MESSAGE 消息、COMMENT 评价、PROMOTE 推广内容")
    private String sceneType;
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;
    @Schema(description = "用户筛选关键词，可匹配用户编号、昵称或手机号")
    private String userKeyword;
    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;
    @Schema(description = "关联业务对象 ID；由 bizType 指明对象类型")
    private Long bizId;
    @Schema(description = "内容处理策略：BLOCK 直接拦截、REPLACE 替换后放行、REVIEW 转人工审核、ALLOW_LOG 仅记录日志")
    private String strategy;
    @Schema(description = "内容类型：TEXT 文本、IMAGE 图片 OCR、QRCODE 二维码")
    private String contentType;
    @Schema(description = "文件 ID，关联文件中心文件")
    private Long fileId;
    @Schema(description = "人工审核结果：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String manualAuditResult;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
