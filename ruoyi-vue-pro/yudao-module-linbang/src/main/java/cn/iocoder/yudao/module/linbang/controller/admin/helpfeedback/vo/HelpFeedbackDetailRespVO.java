package cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 帮助反馈详情 Response VO")
@Data
public class HelpFeedbackDetailRespVO {

    private Long id;
    private Long userId;
    private String feedbackType;
    private String content;
    private String contactMobile;
    private String attachmentUrls;
    private String status;
    private Long handleBy;
    private String handleRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private UserRespVO user;
    private SummaryRespVO summary;
    private List<RelatedFeedbackRespVO> relatedFeedbacks;

    @Data
    public static class UserRespVO {
        private Long id;
        private String userNo;
        private String mobile;
        private String nickname;
        private String currentRoleCode;
        private String status;
        private LocalDateTime lastLoginTime;
    }

    @Data
    public static class SummaryRespVO {
        private Integer sameUserFeedbackCount;
        private Integer sameTypeFeedbackCount;
        private Integer pendingCount;
        private Integer processingCount;
        private Integer finishedCount;
    }

    @Data
    public static class RelatedFeedbackRespVO {
        private Long id;
        private Long userId;
        private String feedbackType;
        private String contactMobile;
        private String status;
        private String handleRemark;
        private LocalDateTime createTime;
    }
}
