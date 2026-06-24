package cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分账规则详情 Response VO")
@Data
public class DivideRuleDetailRespVO {

    private Long id;
    private String ruleName;
    private String cityLevel;
    private Long categoryId;
    private String categoryName;
    private BigDecimal merchantRate;
    private BigDecimal platformRate;
    private BigDecimal partnerRate;
    private BigDecimal promoterRate;
    private BigDecimal taxWithholdRate;
    private BigDecimal minWithdrawAmount;
    private String status;
    private LocalDateTime effectiveTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private BigDecimal totalRate;
    private BigDecimal incomeRate;
    private Boolean rateBalanced;

}
