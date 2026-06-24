package cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 订单主 DO
 *
 * @author dawn
 */
@TableName("lb_order_info")
@KeySequence("lb_order_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 下单用户ID
     */
    private Long userId;
    /**
     * 服务商ID
     */
    private Long merchantId;
    /**
     * 类目ID
     */
    private Long categoryId;
    /**
     * 订单标题
     */
    private String title;
    /**
     * 计价方式
     *
     * 枚举 {@link TODO lb_pricing_mode 对应的类}
     */
    private String pricingMode;
    /**
     * 预算金额
     */
    private BigDecimal budgetAmount;
    /**
     * 订单金额
     */
    private BigDecimal orderAmount;
    /**
     * 工期描述
     */
    private String serviceDurationDesc;
    /**
     * 数量
     */
    private BigDecimal quantity;
    /**
     * 需求描述
     */
    private String requireDesc;
    /**
     * 地址ID
     */
    private Long addressId;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String district;
    /**
     * 街道
     */
    private String street;
    /**
     * 详细地址
     */
    private String detailAddress;
    /**
     * 经度
     */
    private BigDecimal longitude;
    /**
     * 纬度
     */
    private BigDecimal latitude;
    /**
     * 是否开票
     */
    private Boolean needInvoice;
    /**
     * 是否拆单
     */
    private Boolean needSplit;
    /**
     * 拆单状态
     */
    private String splitStatus;
    /**
     * 协议是否确认
     */
    private Boolean agreementConfirmed;
    /**
     * 支付订单ID
     */
    private Long payOrderId;
    /**
     * 订单状态
     *
     * 枚举 {@link TODO lb_order_status 对应的类}
     */
    private String status;


}