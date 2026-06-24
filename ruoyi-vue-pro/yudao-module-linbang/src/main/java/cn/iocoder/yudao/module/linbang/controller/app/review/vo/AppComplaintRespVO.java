package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 投诉 Response VO")
@Data
public class AppComplaintRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "投诉单号")
    private String complaintNo;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "投诉人用户 ID")
    private Long complainantUserId;

    @Schema(description = "被投诉人用户 ID")
    private Long respondentUserId;

    @Schema(description = "投诉类型，由前端按业务字典展示")
    private String complaintType;

    @Schema(description = "投诉内容")
    private String content;

    @Schema(description = "投诉状态：PENDING 待受理、PROCESSING 处理中、FINISHED 已完结、REJECTED 已驳回")
    private String status;

    @Schema(description = "处理人")
    private Long handleBy;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理结果")
    private String resultDesc;

    @Schema(description = "附件文件 ID 列表")
    private List<Long> fileIds;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
