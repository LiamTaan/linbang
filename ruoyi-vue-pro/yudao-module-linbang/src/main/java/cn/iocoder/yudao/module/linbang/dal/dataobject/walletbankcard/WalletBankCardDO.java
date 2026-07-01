package cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 用户银行卡 DO
 *
 * @author dawn
 */
@TableName("lb_user_bank_card")
@KeySequence("lb_user_bank_card_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBankCardDO extends BaseDO {

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
     * 银行名称
     */
    private String bankName;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 加密卡号
     */
    private String cardNoEncrypt;
    /**
     * 出款收款账号
     */
    private String transferAccount;
    /**
     * 脱敏卡号
     */
    private String cardNoMask;
    /**
     * 开户名
     */
    private String accountName;
    /**
     * 开户省份
     */
    private String bankProvince;
    /**
     * 开户城市
     */
    private String bankCity;
    /**
     * 预留手机号
     */
    private String reservedMobile;
    /**
     * 状态
     */
    private String status;
    /**
     * 是否默认
     */
    private Boolean isDefault;


}
