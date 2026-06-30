package cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 身份申请 Response VO")
@Data
public class AppMemberRoleApplyRespVO {

    @Schema(description = "申请编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "申请角色编码", example = "PROMOTER")
    private String applyRoleCode;

    @Schema(description = "申请说明", example = "我有本地社群推广资源")
    private String applyReason;

    @Schema(description = "资源说明", example = "覆盖 3 个社区群和线下地推点位")
    private String resourceDesc;

    @Schema(description = "预期转化说明", example = "预计首月完成 20 个线索转化")
    private String expectedConversionDesc;

    @Schema(description = "能力说明", example = "有本地生活平台管理和活动策划经验")
    private String abilityDesc;

    @Schema(description = "可投入时间说明", example = "每周可投入 5 天")
    private String availableTimeDesc;

    @Schema(description = OpenApiSchemaConstants.ROLE_APPLY_AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "申请单号", example = "RA202606280001")
    private String applyNo;

    @Schema(description = "当前处理节点名称", example = "平台终审")
    private String currentNodeName;

    @Schema(description = "流程节点")
    private java.util.List<ProcessNode> processNodes;

    @Schema(description = "审核备注", example = "资料完整")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "资源说明不足")
    private String rejectReason;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Data
    public static class ProcessNode {

        @Schema(description = "节点编码", example = "SUBMIT")
        private String nodeCode;

        @Schema(description = "节点名称", example = "提交申请")
        private String nodeName;

        @Schema(description = "节点状态，DONE 已完成、CURRENT 当前节点、PENDING 待处理、REJECTED 已驳回", example = "DONE")
        private String nodeStatus;

        @Schema(description = "节点时间")
        private LocalDateTime nodeTime;

        @Schema(description = "节点备注")
        private String nodeRemark;
    }
}
