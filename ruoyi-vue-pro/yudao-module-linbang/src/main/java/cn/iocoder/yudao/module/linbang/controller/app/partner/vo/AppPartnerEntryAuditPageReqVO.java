package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 合作商入驻初审分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppPartnerEntryAuditPageReqVO extends PageParam {

    @Schema(description = "入驻单号", example = "ME202606280001")
    private String entryNo;

    @Schema(description = "申请人关键词，支持用户编号/昵称/手机号", example = "1380000")
    private String userKeyword;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, example = "PENDING")
    private String status;

    @Schema(description = "区域编码", example = "440305")
    private String regionCode;
}
