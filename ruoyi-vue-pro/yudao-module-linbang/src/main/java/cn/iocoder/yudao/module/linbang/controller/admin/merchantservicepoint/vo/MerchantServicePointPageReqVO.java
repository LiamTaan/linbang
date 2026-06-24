package cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 服务点分页 Request VO")
@Data
public class MerchantServicePointPageReqVO extends PageParam {

    @Schema(description = "服务商ID", example = "1024")
    private Long merchantId;

    @Schema(description = "服务点名称", example = "浦东服务点")
    private String pointName;

    @Schema(description = "省", example = "上海市")
    private String province;

    @Schema(description = "市", example = "上海市")
    private String city;

    @Schema(description = "区", example = "浦东新区")
    private String district;

    @Schema(description = "状态", example = "ENABLE")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
