package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 推送批次分页 Request VO")
@Data
public class MatchPushBatchPageReqVO extends PageParam {

    @Schema(description = "订单号，支持模糊搜索", example = "LBO202607040001")
    private String orderNo;

    @Schema(description = "单元号，支持模糊搜索", example = "LBU202607040001-1")
    private String unitNo;

    @Schema(description = "下单人关键字，支持用户编号/昵称/手机号模糊搜索", example = "13800138000")
    private String userKeyword;

    @Schema(description = "状态")
    private String status;
}
