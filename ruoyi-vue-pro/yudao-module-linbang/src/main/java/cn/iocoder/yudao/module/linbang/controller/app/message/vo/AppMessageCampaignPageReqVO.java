package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户 App - 消息中心 分页查询 Request VO")
public class AppMessageCampaignPageReqVO extends PageParam {

    @Schema(description = "投放审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回、CANCELLED 已取消")
    private String auditStatus;

    @Schema(description = "执行状态：PENDING 待执行、PROCESSING 处理中、SUCCESS 全部成功、PARTIAL_FAILED 部分失败、FAILED 全部失败、CANCELLED 已取消")
    private String executeStatus;
}
