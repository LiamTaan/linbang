package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 银行卡分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppBankCardPageReqVO extends PageParam {

    @Schema(description = "银行卡状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String status;
}
