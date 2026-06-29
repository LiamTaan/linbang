package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 推广内容 Response VO")
@Data
public class AppPromoteContentRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;
    private String title;
    private String content;
    private String imageUrls;
    private String status;
    private String systemAuditResult;
    private String systemAuditRemark;
    private String manualAuditResult;
    private String manualAuditRemark;
    private String rejectReason;
    private String offlineReason;
    private LocalDateTime createTime;
}
