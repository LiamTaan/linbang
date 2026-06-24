package cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 订单主分页 Request VO")
@Data
public class OrderInfoPageReqVO extends PageParam {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "下单用户关键词（用户编号 / 昵称 / 手机号）", example = "18888888888")
    private String userKeyword;

    @Schema(description = "服务商ID", example = "22978")
    private Long merchantId;

    @Schema(description = "类目ID", example = "851")
    private Long categoryId;

    @Schema(description = "订单标题")
    private String title;

    @Schema(description = "计价方式")
    private String pricingMode;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "工期描述")
    private String serviceDurationDesc;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "需求描述")
    private String requireDesc;

    @Schema(description = "地址ID", example = "30674")
    private Long addressId;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String district;

    @Schema(description = "街道")
    private String street;

    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "是否开票")
    private Boolean needInvoice;

    @Schema(description = "是否拆单")
    private Boolean needSplit;

    @Schema(description = "拆单状态", example = "2")
    private String splitStatus;

    @Schema(description = "协议是否确认")
    private Boolean agreementConfirmed;

    @Schema(description = "支付订单ID", example = "7048")
    private Long payOrderId;

    @Schema(description = "订单状态", example = "1")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
