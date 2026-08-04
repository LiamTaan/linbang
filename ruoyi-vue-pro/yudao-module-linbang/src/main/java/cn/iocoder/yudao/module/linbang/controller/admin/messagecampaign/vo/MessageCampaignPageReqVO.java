package cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理后台 - 消息投放 分页查询 Request VO")
public class MessageCampaignPageReqVO extends PageParam {

    @Schema(description = "消息投放活动名称")
    private String campaignName;

    @Schema(description = "投放来源：USER_DIRECTED 用户定向申请、ADMIN_DIRECTED 管理后台定向投放、SYSTEM_TRIGGER 系统触发、AD 广告投放")
    private String sourceType;

    @Schema(description = "投放审核状态筛选：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回、CANCELLED 已取消")
    private String auditStatus;

    @Schema(description = "执行状态：PENDING 待执行、PROCESSING 处理中、SUCCESS 全部成功、PARTIAL_FAILED 部分失败、FAILED 全部失败、CANCELLED 已取消")
    private String executeStatus;

    @Schema(description = "目标模式：FULL_PLATFORM 全平台、JURISDICTION 辖区、CUSTOM_FILTER 自定义筛选")
    private String targetMode;

    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
