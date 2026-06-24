package cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveword;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 敏感词表 DO
 *
 * @author dawn
 */
@TableName("lb_sensitive_word")
@KeySequence("lb_sensitive_word_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveWordDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 关键词
     */
    private String word;
    /**
     * 词库类型
     */
    private String wordType;
    /**
     * 匹配方式
     */
    private String matchType;
    /**
     * 拦截级别
     */
    private String blockLevel;
    /**
     * 状态
     */
    private String status;


}