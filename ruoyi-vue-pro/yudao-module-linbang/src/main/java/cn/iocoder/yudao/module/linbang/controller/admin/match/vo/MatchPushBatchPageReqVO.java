package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 推送批次分页 Request VO")
@Data
public class MatchPushBatchPageReqVO extends PageParam {

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "状态")
    private String status;
}
