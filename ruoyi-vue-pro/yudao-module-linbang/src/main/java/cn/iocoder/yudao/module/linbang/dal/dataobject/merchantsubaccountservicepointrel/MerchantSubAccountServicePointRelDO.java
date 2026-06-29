package cn.iocoder.yudao.module.linbang.dal.dataobject.merchantsubaccountservicepointrel;

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

@TableName("lb_merchant_sub_account_service_point_rel")
@KeySequence("lb_merchant_sub_account_service_point_rel_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSubAccountServicePointRelDO extends BaseDO {

    @TableId
    private Long id;

    private Long subAccountId;

    private Long servicePointId;
}
