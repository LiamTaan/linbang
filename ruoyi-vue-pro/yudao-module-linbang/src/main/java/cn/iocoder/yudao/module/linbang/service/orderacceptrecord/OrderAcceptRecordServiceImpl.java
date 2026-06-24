package cn.iocoder.yudao.module.linbang.service.orderacceptrecord;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderacceptrecord.OrderAcceptRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.ordermatchrecord.OrderMatchRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 抢单记录 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class OrderAcceptRecordServiceImpl implements OrderAcceptRecordService {

    @Resource
    private OrderAcceptRecordMapper orderAcceptRecordMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private OrderMatchRecordMapper orderMatchRecordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public Long createOrderAcceptRecord(OrderAcceptRecordSaveReqVO createReqVO) {
        // 插入
        OrderAcceptRecordDO orderAcceptRecord = BeanUtils.toBean(createReqVO, OrderAcceptRecordDO.class);
        orderAcceptRecordMapper.insert(orderAcceptRecord);

        // 返回
        return orderAcceptRecord.getId();
    }

    @Override
    public void updateOrderAcceptRecord(OrderAcceptRecordSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderAcceptRecordExists(updateReqVO.getId());
        // 更新
        OrderAcceptRecordDO updateObj = BeanUtils.toBean(updateReqVO, OrderAcceptRecordDO.class);
        orderAcceptRecordMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrderAcceptRecord(Long id) {
        // 校验存在
        validateOrderAcceptRecordExists(id);
        // 删除
        orderAcceptRecordMapper.deleteById(id);
    }

    @Override
        public void deleteOrderAcceptRecordListByIds(List<Long> ids) {
        // 删除
        orderAcceptRecordMapper.deleteByIds(ids);
        }


    private void validateOrderAcceptRecordExists(Long id) {
        if (orderAcceptRecordMapper.selectById(id) == null) {
            throw exception(ORDER_ACCEPT_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public OrderAcceptRecordDO getOrderAcceptRecord(Long id) {
        return orderAcceptRecordMapper.selectById(id);
    }

    @Override
    public OrderAcceptRecordDetailRespVO getOrderAcceptRecordDetail(Long id) {
        OrderAcceptRecordDO record = orderAcceptRecordMapper.selectById(id);
        if (record == null) {
            throw exception(ORDER_ACCEPT_RECORD_NOT_EXISTS);
        }
        OrderInfoDO order = record.getOrderId() == null ? null : orderInfoMapper.selectById(record.getOrderId());
        MemberUserDO orderUser = order == null || order.getUserId() == null ? null : memberUserMapper.selectById(order.getUserId());
        OrderUnitDO unit = record.getUnitId() == null ? null : orderUnitMapper.selectById(record.getUnitId());
        MerchantInfoDO merchant = record.getMerchantId() == null ? null : merchantInfoMapper.selectById(record.getMerchantId());
        List<OrderMatchRecordDO> matchRecords = orderMatchRecordMapper.selectList(new LambdaQueryWrapperX<OrderMatchRecordDO>()
                .eq(record.getOrderId() != null, OrderMatchRecordDO::getOrderId, record.getOrderId())
                .eq(record.getUnitId() != null, OrderMatchRecordDO::getUnitId, record.getUnitId())
                .eq(record.getMerchantId() != null, OrderMatchRecordDO::getMerchantId, record.getMerchantId())
                .orderByDesc(OrderMatchRecordDO::getPushTime, OrderMatchRecordDO::getId));
        return OrderAcceptRecordDetailAssembler.build(record, order, orderUser, unit, merchant, matchRecords);
    }

    @Override
    public PageResult<OrderAcceptRecordRespVO> getOrderAcceptRecordPage(OrderAcceptRecordPageReqVO pageReqVO) {
        List<Long> matchedOrderIds = resolveMatchedOrderIds(pageReqVO.getOrderNo());
        if (StrUtil.isNotBlank(pageReqVO.getOrderNo()) && CollUtil.isEmpty(matchedOrderIds)) {
            return PageResult.empty();
        }
        List<Long> matchedUnitIds = resolveMatchedUnitIds(pageReqVO.getUnitNo());
        if (StrUtil.isNotBlank(pageReqVO.getUnitNo()) && CollUtil.isEmpty(matchedUnitIds)) {
            return PageResult.empty();
        }
        PageResult<OrderAcceptRecordDO> pageResult = orderAcceptRecordMapper.selectPage(pageReqVO, matchedOrderIds, matchedUnitIds);
        List<OrderAcceptRecordRespVO> list = BeanUtils.toBean(pageResult.getList(), OrderAcceptRecordRespVO.class);
        fillDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedOrderIds(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            return null;
        }
        return convertList(orderInfoMapper.selectListByOrderNo(orderNo), OrderInfoDO::getId);
    }

    private List<Long> resolveMatchedUnitIds(String unitNo) {
        if (StrUtil.isBlank(unitNo)) {
            return null;
        }
        return convertList(orderUnitMapper.selectListByUnitNo(unitNo), OrderUnitDO::getId);
    }

    private void fillDisplayInfo(List<OrderAcceptRecordRespVO> list) {
        Set<Long> orderIds = convertSet(list, OrderAcceptRecordRespVO::getOrderId);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Set<Long> unitIds = convertSet(list, OrderAcceptRecordRespVO::getUnitId);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        Set<Long> merchantIds = convertSet(list, OrderAcceptRecordRespVO::getMerchantId);
        Map<Long, MerchantInfoDO> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
        list.forEach(item -> {
            OrderInfoDO order = orderMap.get(item.getOrderId());
            if (order != null) {
                item.setOrderNo(order.getOrderNo());
            }
            OrderUnitDO unit = unitMap.get(item.getUnitId());
            if (unit != null) {
                item.setUnitNo(unit.getUnitNo());
            }
            MerchantInfoDO merchant = merchantMap.get(item.getMerchantId());
            if (merchant != null) {
                item.setMerchantName(merchant.getMerchantName());
                item.setMerchantContactName(merchant.getContactName());
                item.setMerchantContactMobile(merchant.getContactMobile());
            }
        });
    }

}
