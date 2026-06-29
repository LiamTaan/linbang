package cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户风险关联 Response VO")
@Data
public class UserRiskRelationRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "主用户 ID")
    private Long userId;

    @Schema(description = "主用户编号")
    private String userNo;

    @Schema(description = "主用户昵称")
    private String userNickname;

    @Schema(description = "主用户手机号")
    private String userMobile;

    @Schema(description = "关联用户 ID")
    private Long relatedUserId;

    @Schema(description = "关联用户编号")
    private String relatedUserNo;

    @Schema(description = "关联用户昵称")
    private String relatedUserNickname;

    @Schema(description = "关联用户手机号")
    private String relatedUserMobile;

    @Schema(description = "关联类型")
    private String relationType;

    @Schema(description = "关联值摘要")
    private String relationValue;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
