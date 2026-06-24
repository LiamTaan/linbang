package cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel;

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

/**
 * 服务商类目关联表 DO
 *
 * @author Codex
 */
@TenantIgnore
@TableName("lb_merchant_category_rel")
@KeySequence("lb_merchant_category_rel_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCategoryRelDO extends BaseDO {

    @TableId
    private Long id;

    private Long merchantId;

    private Long categoryId;

    private String status;

}
