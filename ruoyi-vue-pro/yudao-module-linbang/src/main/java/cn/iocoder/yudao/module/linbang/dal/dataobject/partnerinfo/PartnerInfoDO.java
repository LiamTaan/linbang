package cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("lb_partner_info")
@KeySequence("lb_partner_info_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerInfoDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private String partnerName;

    private String contactName;

    private String contactMobile;

    private String status;
}
