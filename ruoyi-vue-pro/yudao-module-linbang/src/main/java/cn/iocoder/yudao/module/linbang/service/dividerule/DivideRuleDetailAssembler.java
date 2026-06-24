package cn.iocoder.yudao.module.linbang.service.dividerule;

import cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo.DivideRuleDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.dividerule.DivideRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;

import java.math.BigDecimal;

final class DivideRuleDetailAssembler {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private DivideRuleDetailAssembler() {
    }

    static DivideRuleDetailRespVO buildDetail(DivideRuleDO divideRule, MerchantServiceCategoryDO category) {
        if (divideRule == null) {
            return null;
        }
        DivideRuleDetailRespVO respVO = new DivideRuleDetailRespVO();
        respVO.setId(divideRule.getId());
        respVO.setRuleName(divideRule.getRuleName());
        respVO.setCityLevel(divideRule.getCityLevel());
        respVO.setCategoryId(divideRule.getCategoryId());
        respVO.setCategoryName(category != null ? category.getCategoryName() : null);
        respVO.setMerchantRate(divideRule.getMerchantRate());
        respVO.setPlatformRate(divideRule.getPlatformRate());
        respVO.setPartnerRate(divideRule.getPartnerRate());
        respVO.setPromoterRate(divideRule.getPromoterRate());
        respVO.setTaxWithholdRate(divideRule.getTaxWithholdRate());
        respVO.setMinWithdrawAmount(divideRule.getMinWithdrawAmount());
        respVO.setStatus(divideRule.getStatus());
        respVO.setEffectiveTime(divideRule.getEffectiveTime());
        respVO.setCreateTime(divideRule.getCreateTime());
        respVO.setUpdateTime(divideRule.getUpdateTime());

        BigDecimal merchantRate = defaultZero(divideRule.getMerchantRate());
        BigDecimal platformRate = defaultZero(divideRule.getPlatformRate());
        BigDecimal partnerRate = defaultZero(divideRule.getPartnerRate());
        BigDecimal promoterRate = defaultZero(divideRule.getPromoterRate());
        BigDecimal taxWithholdRate = defaultZero(divideRule.getTaxWithholdRate());
        BigDecimal totalRate = merchantRate.add(platformRate).add(partnerRate).add(promoterRate).add(taxWithholdRate);
        respVO.setTotalRate(totalRate);
        respVO.setIncomeRate(merchantRate.add(partnerRate).add(promoterRate));
        respVO.setRateBalanced(totalRate.compareTo(ONE_HUNDRED) == 0);
        return respVO;
    }

    private static BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

}
