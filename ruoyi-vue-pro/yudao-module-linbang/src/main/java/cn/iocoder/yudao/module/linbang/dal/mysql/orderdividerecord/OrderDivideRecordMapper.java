package cn.iocoder.yudao.module.linbang.dal.mysql.orderdividerecord;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderdividerecord.OrderDivideRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDivideRecordMapper extends BaseMapperX<OrderDivideRecordDO> {

    default List<OrderDivideRecordDO> selectListByOrderId(Long orderId) {
        return selectList(new LambdaQueryWrapperX<OrderDivideRecordDO>()
                .eq(OrderDivideRecordDO::getOrderId, orderId)
                .orderByAsc(OrderDivideRecordDO::getId));
    }

    default List<OrderDivideRecordDO> selectListByUnitId(Long unitId) {
        return selectList(new LambdaQueryWrapperX<OrderDivideRecordDO>()
                .eq(OrderDivideRecordDO::getUnitId, unitId)
                .orderByAsc(OrderDivideRecordDO::getId));
    }
}
