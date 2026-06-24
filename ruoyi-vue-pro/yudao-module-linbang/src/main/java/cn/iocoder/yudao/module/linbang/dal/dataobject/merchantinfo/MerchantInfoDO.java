package cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 服务商信息表 DO
 *
 * @author dawn
 */
@TableName("lb_merchant_info")
@KeySequence("lb_merchant_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantInfoDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 服务商名称
     */
    private String merchantName;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系人手机号
     */
    private String contactMobile;
    /**
     * 服务范围说明
     */
    private String serviceScopeDesc;
    /**
     * 状态
     */
    private String status;
    /**
     * 接单状态
     */
    private String acceptStatus;
    /**
     * 信用分
     */
    private Integer creditScore;
    /**
     * 信用等级
     */
    private String creditLevel;


}