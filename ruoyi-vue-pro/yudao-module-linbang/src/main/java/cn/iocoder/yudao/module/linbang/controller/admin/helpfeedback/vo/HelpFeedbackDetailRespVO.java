package cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 帮助反馈详情 Response VO")
@Data
public class HelpFeedbackDetailRespVO {

    @Schema(description = "反馈 ID", example = "1")
    private Long id;
    @Schema(description = "提交用户 ID", example = "5001")
    private Long userId;
    @Schema(description = OpenApiSchemaConstants.HELP_FEEDBACK_TYPE, example = "异常反馈")
    private String feedbackType;
    @Schema(description = "反馈内容")
    private String content;
    @Schema(description = "联系电话，便于客服回访", example = "13800138000")
    private String contactMobile;
    @Schema(description = "附件地址列表，多个地址通常以英文逗号分隔")
    private String attachmentUrls;
    @Schema(description = OpenApiSchemaConstants.HELP_FEEDBACK_STATUS, example = "PENDING")
    private String status;
    @Schema(description = "处理人用户 ID", example = "1")
    private Long handleBy;
    @Schema(description = "处理备注")
    private String handleRemark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "反馈用户摘要")
    private UserRespVO user;
    @Schema(description = "同用户/同类型反馈统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "关联反馈记录")
    private List<RelatedFeedbackRespVO> relatedFeedbacks;

    @Data
    public static class UserRespVO {
        @Schema(description = "用户 ID", example = "5001")
        private Long id;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "手机号", example = "13800138000")
        private String mobile;
        @Schema(description = "昵称", example = "邻里用户")
        private String nickname;
        @Schema(description = "当前角色编码", example = "USER")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "最近登录时间")
        private LocalDateTime lastLoginTime;
    }

    @Data
    public static class SummaryRespVO {
        @Schema(description = "该用户累计反馈数", example = "5")
        private Integer sameUserFeedbackCount;
        @Schema(description = "同反馈分类累计数", example = "12")
        private Integer sameTypeFeedbackCount;
        @Schema(description = "待处理数量", example = "3")
        private Integer pendingCount;
        @Schema(description = "处理中数量", example = "1")
        private Integer processingCount;
        @Schema(description = "已完结数量", example = "8")
        private Integer finishedCount;
    }

    @Data
    public static class RelatedFeedbackRespVO {
        @Schema(description = "反馈 ID", example = "2")
        private Long id;
        @Schema(description = "用户 ID", example = "5001")
        private Long userId;
        @Schema(description = OpenApiSchemaConstants.HELP_FEEDBACK_TYPE, example = "功能建议")
        private String feedbackType;
        @Schema(description = "联系电话", example = "13800138000")
        private String contactMobile;
        @Schema(description = OpenApiSchemaConstants.HELP_FEEDBACK_STATUS, example = "FINISHED")
        private String status;
        @Schema(description = "处理备注")
        private String handleRemark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
