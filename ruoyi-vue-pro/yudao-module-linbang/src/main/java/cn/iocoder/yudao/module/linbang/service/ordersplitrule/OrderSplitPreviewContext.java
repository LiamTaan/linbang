package cn.iocoder.yudao.module.linbang.service.ordersplitrule;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderSplitPreviewContext {

    private Long categoryId;

    private String pricingMode;

    private BigDecimal orderAmount;

    private BigDecimal quantity;

    private Integer workerCount;

    private String requireDesc;

}
