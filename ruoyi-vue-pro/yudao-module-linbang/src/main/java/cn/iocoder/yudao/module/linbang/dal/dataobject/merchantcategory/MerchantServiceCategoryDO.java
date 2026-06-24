package cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 服务类目表 DO
 *
 * @author dawn
 */
@TableName("lb_service_category")
@KeySequence("lb_service_category_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantServiceCategoryDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 父级ID
     */
    private Long parentId;
    /**
     * 类目名称
     */
    private String categoryName;
    /**
     * 层级
     */
    private Integer categoryLevel;
    /**
     * 排序
     */
    private Integer sortNo;
    /**
     * 图标
     */
    private String icon;
    /**
     * 默认计价方式
     */
    private String defaultPricingMode;
    /**
     * 是否支持拆单
     */
    private Boolean supportSplit;
    /**
     * 是否支持开票
     */
    private Boolean supportInvoice;
    /**
     * 风险等级
     */
    private String riskLevel;
    /**
     * 状态
     */
    private String status;


}