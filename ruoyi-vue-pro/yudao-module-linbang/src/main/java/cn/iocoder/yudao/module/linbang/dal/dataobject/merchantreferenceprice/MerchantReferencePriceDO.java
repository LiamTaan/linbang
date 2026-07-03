package cn.iocoder.yudao.module.linbang.dal.dataobject.merchantreferenceprice;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@TableName("lb_merchant_reference_price")
@KeySequence("lb_merchant_reference_price_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantReferencePriceDO extends BaseDO {

    @TableId
    private Long id;

    private Long merchantId;

    private Long categoryId;

    private String priceUnitLabel;

    private BigDecimal referencePriceMin;

    private BigDecimal referencePriceMax;

    private String referencePriceDesc;

    private String status;
}

