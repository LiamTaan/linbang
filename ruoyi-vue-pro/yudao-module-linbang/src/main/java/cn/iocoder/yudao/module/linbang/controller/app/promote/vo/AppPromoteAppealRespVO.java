package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 推广申诉 Response VO")
@Data
public class AppPromoteAppealRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;
    private Long contentId;
    private String contentTitle;
    private String appealReason;
    private String status;
    private String auditRemark;
    private String rejectReason;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
}
