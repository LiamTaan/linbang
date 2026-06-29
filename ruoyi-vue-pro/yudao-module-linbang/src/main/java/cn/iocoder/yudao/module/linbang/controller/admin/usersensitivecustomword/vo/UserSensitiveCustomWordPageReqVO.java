package cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户自定义脱敏词分页 Request VO")
@Data
public class UserSensitiveCustomWordPageReqVO extends PageParam {

    @Schema(description = "用户关键词，支持用户编号/昵称/手机号")
    private String userKeyword;

    @Schema(description = "适用场景")
    private String sceneType;

    @Schema(description = "脱敏词")
    private String word;

    @Schema(description = "状态", example = "ENABLE")
    private String status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;
}
