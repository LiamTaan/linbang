package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 推广申诉 Response VO")
@Data
public class AppPromoteAppealRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;
    @Schema(description = "推广内容 ID，关联推广内容")
    private Long contentId;
    @Schema(description = "推广内容标题")
    private String contentTitle;
    @Schema(description = "申诉原因")
    private String appealReason;
    @Schema(description = "推广申诉状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String status;
    @Schema(description = "审核备注")
    private String auditRemark;
    @Schema(description = "审核驳回原因")
    private String rejectReason;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
