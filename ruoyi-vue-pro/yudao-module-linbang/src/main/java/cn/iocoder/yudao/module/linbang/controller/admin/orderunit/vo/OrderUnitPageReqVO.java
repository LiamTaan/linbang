package cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 拆分单元分页 Request VO")
@Data
public class OrderUnitPageReqVO extends PageParam {

    @Schema(description = "主订单ID", example = "10031")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "单元订单号")
    private String unitNo;

    @Schema(description = "单元序号")
    private Integer unitSeq;

    @Schema(description = "单元标题")
    private String unitTitle;

    @Schema(description = "单元金额")
    private BigDecimal unitAmount;

    @Schema(description = "拆分模式")
    private String splitMode;

    @Schema(description = "前置单元ID", example = "31280")
    private Long prevUnitId;

    @Schema(description = "前置单元号")
    private String prevUnitNo;

    @Schema(description = "是否锁定")
    private Boolean isLocked;

    @Schema(description = "锁定原因", example = "不对")
    private String lockReason;

    @Schema(description = "服务商ID", example = "12215")
    private Long merchantId;

    @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "PENDING_ACCEPT")
    private String status;

    @Schema(description = "接单截止时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] acceptDeadlineTime;

    @Schema(description = "完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] finishTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getUnitNo() {
        return unitNo;
    }

    public Integer getUnitSeq() {
        return unitSeq;
    }

    public String getUnitTitle() {
        return unitTitle;
    }

    public BigDecimal getUnitAmount() {
        return unitAmount;
    }

    public String getSplitMode() {
        return splitMode;
    }

    public Long getPrevUnitId() {
        return prevUnitId;
    }

    public String getPrevUnitNo() {
        return prevUnitNo;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public String getLockReason() {
        return lockReason;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime[] getAcceptDeadlineTime() {
        return acceptDeadlineTime;
    }

    public LocalDateTime[] getFinishTime() {
        return finishTime;
    }

    public LocalDateTime[] getCreateTime() {
        return createTime;
    }

}
