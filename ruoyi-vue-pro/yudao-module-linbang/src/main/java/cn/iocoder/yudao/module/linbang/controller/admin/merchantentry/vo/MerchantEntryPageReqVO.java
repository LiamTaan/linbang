package cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 服务商入驻申请表分页 Request VO")
@Data
public class MerchantEntryPageReqVO extends PageParam {

    @Schema(description = "服务商ID", example = "18167")
    private Long merchantId;

    @Schema(description = "用户关键词（用户编号 / 昵称 / 手机号）", example = "18888888888")
    private String userKeyword;

    @Schema(description = "入驻单号")
    private String entryNo;

    @Schema(description = "区域编码")
    private String regionCode;

    @Schema(description = "初审状态", example = "1")
    private String firstAuditStatus;

    @Schema(description = "初审人")
    private Long firstAuditBy;

    @Schema(description = "初审时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] firstAuditTime;

    @Schema(description = "终审状态", example = "1")
    private String finalAuditStatus;

    @Schema(description = "终审人")
    private Long finalAuditBy;

    @Schema(description = "终审时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] finalAuditTime;

    @Schema(description = "状态", example = "2")
    private String status;

    @Schema(description = "备注", example = "??")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
