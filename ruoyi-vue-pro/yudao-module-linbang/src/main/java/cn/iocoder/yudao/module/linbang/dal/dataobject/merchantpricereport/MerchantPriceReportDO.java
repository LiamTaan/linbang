package cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

@TableName("lb_merchant_price_report")
@KeySequence("lb_merchant_price_report_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantPriceReportDO extends BaseDO {

    @TableId
    private Long id;

    private Long merchantId;

    private Long partnerId;

    private Long categoryId;

    private String regionCode;

    private BigDecimal suggestedPrice;

    private String remark;

    private String status;

    private String auditStatus;

    private String auditRemark;

    private String rejectReason;

    private Long auditBy;

    private java.time.LocalDateTime auditTime;
}
