package cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "管理后台 - 服务商价格申报 分页查询 Request VO")
public class MerchantPriceReportPageReqVO extends PageParam {

    @Schema(description = "服务商 ID，关联服务商档案")
    private Long merchantId;

    @Schema(description = "区域合作商 ID，关联合作商档案")
    private Long partnerId;

    @Schema(description = "服务分类 ID，关联服务分类")
    private Long categoryId;

    @Schema(description = "业务所属高德行政区划编码")
    private String regionCode;

    @Schema(description = "价格申报状态筛选：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String status;

    @Schema(description = "价格申报审核状态筛选：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String auditStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
