package cn.iocoder.yudao.module.linbang.service.creditrecord;

import cn.iocoder.yudao.module.linbang.dal.dataobject.creditlevelbenefit.CreditLevelBenefitDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditlevelbenefit.CreditLevelBenefitMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CreditLevelBenefitServiceImpl implements CreditLevelBenefitService {

    @Resource
    private CreditLevelBenefitMapper creditLevelBenefitMapper;

    @Override
    public List<CreditLevelBenefitDO> getEnabledBenefits() {
        return creditLevelBenefitMapper.selectEnabledList();
    }

    @Override
    public Map<String, List<CreditLevelBenefitDO>> getEnabledBenefitsByLevel() {
        List<CreditLevelBenefitDO> benefits = getEnabledBenefits();
        if (benefits.isEmpty()) {
            return Collections.emptyMap();
        }
        return benefits.stream().collect(Collectors.groupingBy(CreditLevelBenefitDO::getLevelCode));
    }
}
