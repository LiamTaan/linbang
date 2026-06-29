package cn.iocoder.yudao.module.linbang.dal.mysql.creditlevelbenefit;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditlevelbenefit.CreditLevelBenefitDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CreditLevelBenefitMapper extends BaseMapperX<CreditLevelBenefitDO> {

    default List<CreditLevelBenefitDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<CreditLevelBenefitDO>()
                .eq(CreditLevelBenefitDO::getStatus, "ENABLE")
                .orderByAsc(CreditLevelBenefitDO::getLevelCode)
                .orderByAsc(CreditLevelBenefitDO::getSortNo)
                .orderByAsc(CreditLevelBenefitDO::getId));
    }
}
