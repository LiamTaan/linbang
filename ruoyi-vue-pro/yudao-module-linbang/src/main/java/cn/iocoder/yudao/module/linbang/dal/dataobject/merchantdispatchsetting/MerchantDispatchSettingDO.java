package cn.iocoder.yudao.module.linbang.dal.dataobject.merchantdispatchsetting;

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

/**
 * 服务商派单设置 DO
 */
@TableName("lb_merchant_dispatch_setting")
@KeySequence("lb_merchant_dispatch_setting_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDispatchSettingDO extends BaseDO {

    @TableId
    private Long id;

    private Long merchantId;

    private Boolean dispatchEnabled;

    private BigDecimal maxAcceptRadiusKm;

    private Boolean voiceRemindEnabled;
}
