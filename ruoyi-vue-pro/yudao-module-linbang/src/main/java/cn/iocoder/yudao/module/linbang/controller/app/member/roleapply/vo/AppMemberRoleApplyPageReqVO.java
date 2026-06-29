package cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户 App - 身份申请分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppMemberRoleApplyPageReqVO extends PageParam {

    @Schema(description = "申请角色编码", example = "PROMOTER")
    private String applyRoleCode;

    @Schema(description = OpenApiSchemaConstants.ROLE_APPLY_AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
