package cn.iocoder.yudao.module.linbang.dal.mysql.prioritypoolrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.prioritypoolrecord.PriorityPoolRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PriorityPoolRecordMapper extends BaseMapperX<PriorityPoolRecordDO> {

    default PriorityPoolRecordDO selectCurrentByMerchantId(Long merchantId) {
        return selectOne(new LambdaQueryWrapperX<PriorityPoolRecordDO>()
                .eq(PriorityPoolRecordDO::getMerchantId, merchantId)
                .eq(PriorityPoolRecordDO::getCurrentFlag, true)
                .orderByDesc(PriorityPoolRecordDO::getId)
                .last("LIMIT 1"));
    }

    default List<PriorityPoolRecordDO> selectCurrentList() {
        return selectList(new LambdaQueryWrapperX<PriorityPoolRecordDO>()
                .eq(PriorityPoolRecordDO::getCurrentFlag, true)
                .orderByDesc(PriorityPoolRecordDO::getId));
    }

    default PageResult<PriorityPoolRecordDO> selectPage(cn.iocoder.yudao.framework.common.pojo.PageParam pageParam,
                                                        Long merchantId, String status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<PriorityPoolRecordDO>()
                .eqIfPresent(PriorityPoolRecordDO::getMerchantId, merchantId)
                .eqIfPresent(PriorityPoolRecordDO::getStatus, status)
                .orderByDesc(PriorityPoolRecordDO::getId));
    }
}
