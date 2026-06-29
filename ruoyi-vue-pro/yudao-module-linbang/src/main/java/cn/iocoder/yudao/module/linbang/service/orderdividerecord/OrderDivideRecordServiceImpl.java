package cn.iocoder.yudao.module.linbang.service.orderdividerecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.orderdividerecord.vo.OrderDivideRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.orderdividerecord.vo.OrderDivideRecordRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderdividerecord.OrderDivideRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderdividerecord.OrderDivideRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.WALLET_FLOW_NOT_EXISTS;

@Service
@Validated
public class OrderDivideRecordServiceImpl implements OrderDivideRecordService {

    @Resource
    private OrderDivideRecordMapper orderDivideRecordMapper;

    @Override
    public PageResult<OrderDivideRecordRespVO> getOrderDivideRecordPage(OrderDivideRecordPageReqVO reqVO) {
        PageResult<OrderDivideRecordDO> pageResult = orderDivideRecordMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<OrderDivideRecordDO>()
                        .eqIfPresent(OrderDivideRecordDO::getOrderId, reqVO.getOrderId())
                        .eqIfPresent(OrderDivideRecordDO::getUnitId, reqVO.getUnitId())
                        .eqIfPresent(OrderDivideRecordDO::getTargetType, reqVO.getTargetType())
                        .eqIfPresent(OrderDivideRecordDO::getSettleStatus, reqVO.getSettleStatus())
                        .eqIfPresent(OrderDivideRecordDO::getCityLevel, reqVO.getCityLevel())
                        .betweenIfPresent(OrderDivideRecordDO::getCreateTime, reqVO.getCreateTime())
                        .orderByDesc(OrderDivideRecordDO::getId));
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), OrderDivideRecordRespVO.class), pageResult.getTotal());
    }

    @Override
    public OrderDivideRecordRespVO getOrderDivideRecord(Long id) {
        OrderDivideRecordDO record = orderDivideRecordMapper.selectById(id);
        if (record == null) {
            throw exception(WALLET_FLOW_NOT_EXISTS);
        }
        return BeanUtils.toBean(record, OrderDivideRecordRespVO.class);
    }

}
