package cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 推广申诉 Response VO")
@Data
public class PromoteAppealRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;
    private Long contentId;
    private String contentTitle;
    private Long promoterId;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private String appealReason;
    private String status;
    private String auditRemark;
    private String rejectReason;
    private Long auditBy;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
}
