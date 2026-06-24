package cn.iocoder.yudao.module.linbang.dal.dataobject.partnerregionrel;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("lb_partner_region_rel")
@KeySequence("lb_partner_region_rel_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerRegionRelDO extends BaseDO {

    @TableId
    private Long id;

    private Long partnerId;

    private String province;

    private String city;

    private String district;

    private String adcode;

    private String status;
}
