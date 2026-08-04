package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "管理后台 - 敏感图片扫描 分页查询 Request VO")
public class SensitiveImageScanResultPageReqVO extends PageParam {

    @Schema(description = "内容检测场景编码，例如 MESSAGE 消息、COMMENT 评价、PROMOTE 推广内容")
    private String sceneType;
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;
    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;
    @Schema(description = "关联业务对象 ID；由 bizType 指明对象类型")
    private Long bizId;
    @Schema(description = "图片扫描状态：SUCCESS 扫描成功、HIT 命中敏感内容、FAILED 扫描失败")
    private String scanStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
