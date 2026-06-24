package cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 用户资质分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberQualificationPageReqVO extends PageParam {

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "用户关键词（用户编号 / 昵称 / 手机号）", example = "13800138000")
    private String userKeyword;

    @Schema(description = "资质类型", example = "ELECTRICIAN")
    private String qualificationType;

    @Schema(description = "资质名称", example = "电工证")
    private String qualificationName;

    @Schema(description = "审核状态", example = "PENDING")
    private String auditStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime[] createTime;

}
