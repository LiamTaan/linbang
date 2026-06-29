package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 优先池分页 Request VO")
@Data
public class PriorityPoolPageReqVO extends PageParam {

    @Schema(description = "服务商 ID")
    private Long merchantId;

    @Schema(description = "状态")
    private String status;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
