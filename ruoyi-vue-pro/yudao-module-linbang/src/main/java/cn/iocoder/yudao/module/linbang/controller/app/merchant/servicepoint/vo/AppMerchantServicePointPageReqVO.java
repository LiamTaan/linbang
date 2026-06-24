package cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 服务点分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppMerchantServicePointPageReqVO extends PageParam {

    @Schema(description = "服务点名称")
    private String pointName;

    @Schema(description = "服务点状态筛选：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String status;
}
