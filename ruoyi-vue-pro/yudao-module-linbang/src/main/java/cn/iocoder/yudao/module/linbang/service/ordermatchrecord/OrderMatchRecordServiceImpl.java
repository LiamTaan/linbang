package cn.iocoder.yudao.module.linbang.service.ordermatchrecord;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderacceptrecord.OrderAcceptRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.ordermatchrecord.OrderMatchRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.riskrule.RiskRuleMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 订单匹配记录 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class OrderMatchRecordServiceImpl implements OrderMatchRecordService {

    @Resource
    private OrderMatchRecordMapper orderMatchRecordMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private RiskRuleMapper riskRuleMapper;
    @Resource
    private OrderAcceptRecordMapper orderAcceptRecordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public Long createOrderMatchRecord(OrderMatchRecordSaveReqVO createReqVO) {
        // 插入
        OrderMatchRecordDO orderMatchRecord = BeanUtils.toBean(createReqVO, OrderMatchRecordDO.class);
        orderMatchRecordMapper.insert(orderMatchRecord);

        // 返回
        return orderMatchRecord.getId();
    }

    @Override
    public void updateOrderMatchRecord(OrderMatchRecordSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderMatchRecordExists(updateReqVO.getId());
        // 更新
        OrderMatchRecordDO updateObj = BeanUtils.toBean(updateReqVO, OrderMatchRecordDO.class);
        orderMatchRecordMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrderMatchRecord(Long id) {
        // 校验存在
        validateOrderMatchRecordExists(id);
        // 删除
        orderMatchRecordMapper.deleteById(id);
    }

    @Override
        public void deleteOrderMatchRecordListByIds(List<Long> ids) {
        // 删除
        orderMatchRecordMapper.deleteByIds(ids);
        }


    private void validateOrderMatchRecordExists(Long id) {
        if (orderMatchRecordMapper.selectById(id) == null) {
            throw exception(ORDER_MATCH_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public OrderMatchRecordDO getOrderMatchRecord(Long id) {
        return orderMatchRecordMapper.selectById(id);
    }

    @Override
    public OrderMatchRecordDetailRespVO getOrderMatchRecordDetail(Long id) {
        OrderMatchRecordDO record = orderMatchRecordMapper.selectById(id);
        if (record == null) {
            throw exception(ORDER_MATCH_RECORD_NOT_EXISTS);
        }
        OrderInfoDO order = record.getOrderId() == null ? null : orderInfoMapper.selectById(record.getOrderId());
        MemberUserDO orderUser = order == null || order.getUserId() == null ? null : memberUserMapper.selectById(order.getUserId());
        OrderUnitDO unit = record.getUnitId() == null ? null : orderUnitMapper.selectById(record.getUnitId());
        MerchantInfoDO merchant = record.getMerchantId() == null ? null : merchantInfoMapper.selectById(record.getMerchantId());
        RiskRuleDO rule = record.getMatchRuleCode() == null ? null : riskRuleMapper.selectOne(
                new LambdaQueryWrapperX<RiskRuleDO>().eq(RiskRuleDO::getRuleCode, record.getMatchRuleCode()));
        List<OrderAcceptRecordDO> acceptRecords = orderAcceptRecordMapper.selectList(new LambdaQueryWrapperX<OrderAcceptRecordDO>()
                .eq(record.getOrderId() != null, OrderAcceptRecordDO::getOrderId, record.getOrderId())
                .eq(record.getUnitId() != null, OrderAcceptRecordDO::getUnitId, record.getUnitId())
                .eq(record.getMerchantId() != null, OrderAcceptRecordDO::getMerchantId, record.getMerchantId())
                .orderByDesc(OrderAcceptRecordDO::getAcceptTime, OrderAcceptRecordDO::getId));
        return OrderMatchRecordDetailAssembler.build(record, order, orderUser, unit, merchant, rule, acceptRecords);
    }

    @Override
    public PageResult<OrderMatchRecordRespVO> getOrderMatchRecordPage(OrderMatchRecordPageReqVO pageReqVO) {
        List<Long> matchedOrderIds = resolveMatchedOrderIds(pageReqVO.getOrderNo());
        if (StrUtil.isNotBlank(pageReqVO.getOrderNo()) && CollUtil.isEmpty(matchedOrderIds)) {
            return PageResult.empty();
        }
        List<Long> matchedUnitIds = resolveMatchedUnitIds(pageReqVO.getUnitNo());
        if (StrUtil.isNotBlank(pageReqVO.getUnitNo()) && CollUtil.isEmpty(matchedUnitIds)) {
            return PageResult.empty();
        }
        PageResult<OrderMatchRecordDO> pageResult = orderMatchRecordMapper.selectPage(pageReqVO, matchedOrderIds, matchedUnitIds);
        List<OrderMatchRecordRespVO> list = BeanUtils.toBean(pageResult.getList(), OrderMatchRecordRespVO.class);
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

    private void fillDisplayInfo(List<OrderMatchRecordRespVO> list) {
        Set<Long> orderIds = convertSet(list, OrderMatchRecordRespVO::getOrderId);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Set<Long> unitIds = convertSet(list, OrderMatchRecordRespVO::getUnitId);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        Set<Long> merchantIds = convertSet(list, OrderMatchRecordRespVO::getMerchantId);
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
