package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.module.linbang.dal.dataobject.matchstrategy.MatchStrategyDO;

import java.math.BigDecimal;
import java.util.List;

public interface MatchStrategyService {

    MatchStrategyDO getEnabledStrategy();

    List<StageRule> getStageRules();

    String getFlowAdviceTemplate();

    boolean isAutoRefundEnabled();

    int getAutoRefundRetryTimes();

    void updateEnabledStrategy(MatchStrategyDO strategy);

    class StageRule {

        private Integer stageNo;

        private BigDecimal radiusStartKm;

        private BigDecimal radiusEndKm;

        private Integer durationSeconds;

        public StageRule() {
        }

        public StageRule(Integer stageNo, BigDecimal radiusStartKm, BigDecimal radiusEndKm, Integer durationSeconds) {
            this.stageNo = stageNo;
            this.radiusStartKm = radiusStartKm;
            this.radiusEndKm = radiusEndKm;
            this.durationSeconds = durationSeconds;
        }

        public Integer getStageNo() {
            return stageNo;
        }

        public void setStageNo(Integer stageNo) {
            this.stageNo = stageNo;
        }

        public BigDecimal getRadiusStartKm() {
            return radiusStartKm;
        }

        public void setRadiusStartKm(BigDecimal radiusStartKm) {
            this.radiusStartKm = radiusStartKm;
        }

        public BigDecimal getRadiusEndKm() {
            return radiusEndKm;
        }

        public void setRadiusEndKm(BigDecimal radiusEndKm) {
            this.radiusEndKm = radiusEndKm;
        }

        public Integer getDurationSeconds() {
            return durationSeconds;
        }

        public void setDurationSeconds(Integer durationSeconds) {
            this.durationSeconds = durationSeconds;
        }
    }
}
