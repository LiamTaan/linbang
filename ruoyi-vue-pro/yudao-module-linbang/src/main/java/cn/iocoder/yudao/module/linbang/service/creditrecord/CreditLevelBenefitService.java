package cn.iocoder.yudao.module.linbang.service.creditrecord;

import cn.iocoder.yudao.module.linbang.dal.dataobject.creditlevelbenefit.CreditLevelBenefitDO;

import java.util.List;
import java.util.Map;

public interface CreditLevelBenefitService {

    List<CreditLevelBenefitDO> getEnabledBenefits();

    Map<String, List<CreditLevelBenefitDO>> getEnabledBenefitsByLevel();
}
