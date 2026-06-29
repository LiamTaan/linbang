package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 晒单悬赏分页 Request VO")
@Data
public class ShowcaseRewardPageReqVO extends PageParam {

    @Schema(description = "服务商 ID")
    private Long merchantId;

    @Schema(description = "审核状态")
    private String auditStatus;
}
