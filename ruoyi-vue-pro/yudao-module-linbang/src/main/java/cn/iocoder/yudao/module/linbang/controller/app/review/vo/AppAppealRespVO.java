package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 申诉 Response VO")
@Data
public class AppAppealRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "申诉单号")
    private String appealNo;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "申诉人用户 ID")
    private Long userId;

    @Schema(description = "申诉类型，由前端按业务字典展示")
    private String appealType;

    @Schema(description = "申诉内容")
    private String content;

    @Schema(description = "申诉状态：PENDING 待审核、PROCESSING 处理中、APPROVED 通过、REJECTED 驳回、FINISHED 已完结")
    private String status;

    @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String auditStatus;

    @Schema(description = "审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "申诉时效截止时间")
    private LocalDateTime appealExpireTime;

    @Schema(description = "附件文件 ID 列表")
    private List<Long> fileIds;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
