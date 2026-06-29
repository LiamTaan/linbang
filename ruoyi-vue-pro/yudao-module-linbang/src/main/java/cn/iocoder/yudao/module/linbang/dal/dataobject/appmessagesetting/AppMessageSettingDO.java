package cn.iocoder.yudao.module.linbang.dal.dataobject.appmessagesetting;

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

@TableName("lb_app_message_setting")
@KeySequence("lb_app_message_setting_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppMessageSettingDO extends BaseDO {

    @TableId
    private Long id;

    private Long userId;

    private Boolean voiceReadEnabled;

    private Boolean popupEnabled;

    private Boolean marketingEnabled;
}
