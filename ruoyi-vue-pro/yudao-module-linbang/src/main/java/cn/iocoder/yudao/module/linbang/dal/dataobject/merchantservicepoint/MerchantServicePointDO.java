package cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
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

/**
 * 服务点表 DO
 *
 * @author Codex
 */
@TenantIgnore
@TableName("lb_merchant_service_point")
@KeySequence("lb_merchant_service_point_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantServicePointDO extends BaseDO {

    @TableId
    private Long id;

    private Long merchantId;

    private String pointName;

    private String province;

    private String city;

    private String district;

    private String street;

    private String detailAddress;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private BigDecimal serviceRadiusKm;

    private String status;

}
