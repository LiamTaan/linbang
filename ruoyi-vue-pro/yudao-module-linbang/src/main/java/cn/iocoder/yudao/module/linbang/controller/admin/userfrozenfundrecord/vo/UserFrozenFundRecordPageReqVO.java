package cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 冻结资金记录分页 Request VO")
@Data
public class UserFrozenFundRecordPageReqVO extends PageParam {

    @Schema(description = "平台用户 ID，关联用户档案")
    private Long userId;

    @Schema(description = "用户筛选关键词，可匹配用户编号、昵称或手机号")
    private String userKeyword;

    @Schema(description = "资金冻结状态筛选：ACTIVE 冻结中、RELEASED 已解冻")
    private String status;

    @Schema(description = "来源业务类型，例如 ORDER 订单、ORDER_UNIT 订单单元、RISK_EVENT 风险事件")
    private String sourceBizType;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
