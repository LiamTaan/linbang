package cn.iocoder.yudao.module.linbang.dal.dataobject.orderunitproof;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("lb_order_unit_proof")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUnitProofDO {

    @TableId
    private Long id;

    private Long unitId;

    private Long orderId;

    private Long merchantId;

    private Long fileId;

    private String fileUrl;

    private String fileHash;

    private String proofType;

    private String proofDesc;

    private LocalDateTime proofTime;

    private LocalDateTime deviceTime;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String addressText;

}
