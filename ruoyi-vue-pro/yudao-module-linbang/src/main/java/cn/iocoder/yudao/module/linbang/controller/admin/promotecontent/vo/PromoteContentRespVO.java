package cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 推广内容 Response VO")
@Data
public class PromoteContentRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;
    private Long promoterId;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private String title;
    private String content;
    private String imageUrls;
    private String status;
    private String systemAuditResult;
    private String systemAuditRemark;
    private LocalDateTime systemAuditTime;
    private String manualAuditResult;
    private String manualAuditRemark;
    private Long manualAuditBy;
    private LocalDateTime manualAuditTime;
    private String rejectReason;
    private String offlineReason;
    private LocalDateTime createTime;
}
