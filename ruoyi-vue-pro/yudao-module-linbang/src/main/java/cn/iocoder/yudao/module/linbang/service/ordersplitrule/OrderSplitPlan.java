package cn.iocoder.yudao.module.linbang.service.ordersplitrule;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderSplitPlan {

    private boolean matched;

    private boolean splitRequired;

    private Long ruleId;

    private String ruleName;

    private String ruleCode;

    private String matchMode;

    private String splitMode;

    private BigDecimal unitAmountLimit;

    private Integer unitCount;

    private String ruleSnapshot;

    private List<OrderSplitUnitPlan> units;

    @Data
    @Builder
    public static class OrderSplitUnitPlan {
        private Integer unitSeq;
        private String unitTitle;
        private String unitType;
        private String unitContent;
        private String unitProgress;
        private BigDecimal unitAmount;
        private Integer workerCount;
        private BigDecimal maxAmountLimit;
        private Boolean locked;
        private String lockReason;
        private String initStatus;
    }

}
