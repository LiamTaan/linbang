package cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import org.apache.ibatis.type.Alias;

/**
 * 用户主表 DO
 *
 * @author dawn
 */
@TableName("lb_user")
@KeySequence("lb_user_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Alias("LinbangMemberUserDO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUserDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private String userNo;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码摘要
     */
    private String password;
    /**
     * 账户类型
     */
    private String accountType;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private LocalDate birthday;
    /**
     * 注册来源
     */
    private String registerSource;
    /**
     * 注册协议版本
     */
    private String registerAgreementVersion;
    /**
     * 注册协议确认时间
     */
    private LocalDateTime registerAgreementConfirmedTime;
    /**
     * 注册来源细分
     */
    private String registerSourceDetail;
    /**
     * 当前角色编码
     *
     * 对应字典类型 lb_role_code
     */
    private String currentRoleCode;
    /**
     * 状态
     */
    private String status;
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    /**
     * 当前积分余额
     */
    private Integer pointBalance;
    /**
     * 备注
     */
    private String remark;


}
