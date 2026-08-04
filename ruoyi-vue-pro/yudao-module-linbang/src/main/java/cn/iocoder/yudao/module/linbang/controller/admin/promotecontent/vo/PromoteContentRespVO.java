package cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 推广内容 Response VO")
@Data
public class PromoteContentRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;
    @Schema(description = "推广员 ID，关联推广员档案")
    private Long promoterId;
    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;
    @Schema(description = "平台用户业务编号")
    private String userNo;
    @Schema(description = "用户昵称")
    private String userNickname;
    @Schema(description = "用户手机号")
    private String userMobile;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "业务正文内容")
    private String content;
    @Schema(description = "推广图片地址列表，多个地址以英文逗号分隔")
    private String imageUrls;
    @Schema(description = "推广内容状态：DRAFT 草稿、PENDING_SYSTEM_AUDIT 待系统审核、PENDING_MANUAL_AUDIT 待人工审核、APPROVED 已通过、REJECTED 已驳回、OFFLINE 已下架")
    private String status;
    @Schema(description = "系统审核结果：PASS 通过、BLOCK 拦截、REVIEW 转人工审核")
    private String systemAuditResult;
    @Schema(description = "系统审核备注")
    private String systemAuditRemark;
    @Schema(description = "系统审核时间")
    private LocalDateTime systemAuditTime;
    @Schema(description = "人工审核结果：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String manualAuditResult;
    @Schema(description = "人工审核备注")
    private String manualAuditRemark;
    @Schema(description = "人工审核人后台用户 ID")
    private Long manualAuditBy;
    @Schema(description = "人工审核时间")
    private LocalDateTime manualAuditTime;
    @Schema(description = "审核驳回原因")
    private String rejectReason;
    @Schema(description = "推广内容下架原因")
    private String offlineReason;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
