package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 合作商价格申报分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppPartnerPriceReportPageReqVO extends PageParam {

    @Schema(description = "区域编码", example = "440305")
    private String regionCode;

    @Schema(description = "申报状态", example = "PENDING")
    private String status;
}
