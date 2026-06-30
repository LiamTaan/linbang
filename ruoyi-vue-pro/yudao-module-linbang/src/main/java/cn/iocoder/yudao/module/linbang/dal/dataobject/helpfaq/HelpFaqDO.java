package cn.iocoder.yudao.module.linbang.dal.dataobject.helpfaq;

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

@TableName("lb_help_faq")
@KeySequence("lb_help_faq_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpFaqDO extends BaseDO {

    @TableId
    private Long id;

    private String categoryCode;

    private String categoryName;

    private String title;

    private String content;

    private String icon;

    private Integer sortNo;

    private String status;
}
