package cn.iocoder.yudao.module.linbang.dal.dataobject.orderpriceitem;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@TableName("lb_order_price_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPriceItemDO {

    @TableId
    private Long id;

    private Long orderId;

    private String itemType;

    private String itemName;

    private BigDecimal itemAmount;

    private Integer sortNo;

}
