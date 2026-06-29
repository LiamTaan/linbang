package cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户风险关联分页 Request VO")
@Data
public class UserRiskRelationPageReqVO extends PageParam {

    @Schema(description = "用户 ID", example = "1001")
    private Long userId;

    @Schema(description = "关联用户 ID", example = "2002")
    private Long relatedUserId;

    @Schema(description = "关联类型", example = "DEVICE")
    private String relationType;

    @Schema(description = "状态", example = "ENABLE")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
