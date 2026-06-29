package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 合作商辖区纠纷 Response VO")
@Data
public class AppPartnerDisputeRespVO {

    @Schema(description = "纠纷类型：COMPLAINT 投诉、APPEAL 申诉", example = "COMPLAINT")
    private String disputeType;

    @Schema(description = "纠纷主键 ID", example = "1")
    private Long disputeId;

    @Schema(description = "纠纷单号", example = "CP202606280001")
    private String disputeNo;

    @Schema(description = "主订单 ID", example = "1001")
    private Long orderId;

    @Schema(description = "订单号", example = "LB202606280001")
    private String orderNo;

    @Schema(description = "单元 ID", example = "2001")
    private Long unitId;

    @Schema(description = "单元号", example = "UNIT202606280001")
    private String unitNo;

    @Schema(description = "纠纷状态", example = "PENDING")
    private String status;

    @Schema(description = "投诉内容或申诉内容")
    private String content;

    @Schema(description = "处理结果摘要")
    private String resultDesc;

    @Schema(description = "合作商协调记录")
    private List<CoordinationItem> coordinationRecords;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Data
    public static class CoordinationItem {

        @Schema(description = "协调记录 ID", example = "1")
        private Long id;

        @Schema(description = OpenApiSchemaConstants.PARTNER_COORDINATION_STATUS, example = "PROCESSING")
        private String status;

        @Schema(description = "协调意见")
        private String coordinationRemark;

        @Schema(description = "升级平台终审意见")
        private String escalateRemark;

        @Schema(description = "发起人 ID", example = "1")
        private Long initiatedBy;

        @Schema(description = "发起时间")
        private LocalDateTime initiatedTime;

        @Schema(description = "结束人 ID", example = "1")
        private Long finishedBy;

        @Schema(description = "结束时间")
        private LocalDateTime finishedTime;
    }
}
