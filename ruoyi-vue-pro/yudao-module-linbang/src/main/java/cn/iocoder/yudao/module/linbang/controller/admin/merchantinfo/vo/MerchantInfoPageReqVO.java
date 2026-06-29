package cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 服务商信息表分页 Request VO")
@Data
public class MerchantInfoPageReqVO extends PageParam {

    @Schema(description = "用户关键词（用户编号 / 昵称 / 手机号）", example = "18888888888")
    private String userKeyword;

    @Schema(description = "服务商名称", example = "??")
    private String merchantName;

    @Schema(description = "联系人", example = "??")
    private String contactName;

    @Schema(description = "联系人手机号")
    private String contactMobile;

    @Schema(description = "服务范围说明")
    private String serviceScopeDesc;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
    private String acceptStatus;

    @Schema(description = "信用分")
    private Integer creditScore;

    @Schema(description = "信用等级")
    private String creditLevel;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
