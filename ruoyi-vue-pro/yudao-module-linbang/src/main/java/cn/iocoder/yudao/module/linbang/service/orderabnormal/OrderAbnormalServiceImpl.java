package cn.iocoder.yudao.module.linbang.service.orderabnormal;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo.OrderAbnormalDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo.OrderMarkAbnormalReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderabnormal.OrderAbnormalDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.linbang.dal.mysql.orderabnormal.OrderAbnormalMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.riskrule.RiskRuleMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 异常订单 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class OrderAbnormalServiceImpl implements OrderAbnormalService {

    @Resource
    private OrderAbnormalMapper orderAbnormalMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private RiskRuleMapper riskRuleMapper;

    @Override
    public Long createOrderAbnormal(OrderAbnormalSaveReqVO createReqVO) {
        // 插入
        OrderAbnormalDO orderAbnormal = BeanUtils.toBean(createReqVO, OrderAbnormalDO.class);
        orderAbnormalMapper.insert(orderAbnormal);

        // 返回
        return orderAbnormal.getId();
    }

    @Override
    public void updateOrderAbnormal(OrderAbnormalSaveReqVO updateReqVO) {
        // 校验存在
        validateOrderAbnormalExists(updateReqVO.getId());
        // 更新
        OrderAbnormalDO updateObj = BeanUtils.toBean(updateReqVO, OrderAbnormalDO.class);
        orderAbnormalMapper.updateById(updateObj);
    }

    @Override
    public void deleteOrderAbnormal(Long id) {
        // 校验存在
        validateOrderAbnormalExists(id);
        // 删除
        orderAbnormalMapper.deleteById(id);
    }

    @Override
        public void deleteOrderAbnormalListByIds(List<Long> ids) {
        // 删除
        orderAbnormalMapper.deleteByIds(ids);
        }


    private void validateOrderAbnormalExists(Long id) {
        if (orderAbnormalMapper.selectById(id) == null) {
            throw exception(ORDER_ABNORMAL_NOT_EXISTS);
        }
    }

    @Override
    public OrderAbnormalDO getOrderAbnormal(Long id) {
        return orderAbnormalMapper.selectById(id);
    }

    @Override
    public OrderAbnormalDetailRespVO getOrderAbnormalDetail(Long id) {
        OrderAbnormalDO abnormal = orderAbnormalMapper.selectById(id);
        if (abnormal == null) {
            throw exception(ORDER_ABNORMAL_NOT_EXISTS);
        }

        OrderInfoDO order = abnormal.getOrderId() == null ? null : orderInfoMapper.selectById(abnormal.getOrderId());
        OrderUnitDO unit = abnormal.getUnitId() == null ? null : orderUnitMapper.selectById(abnormal.getUnitId());
        RiskRuleDO riskRule = abnormal.getHitRuleCode() == null ? null : riskRuleMapper.selectOne(
                new LambdaQueryWrapperX<RiskRuleDO>().eq(RiskRuleDO::getRuleCode, abnormal.getHitRuleCode()));
        List<OrderOperateLogDO> operateLogs = orderOperateLogMapper.selectList(new LambdaQueryWrapperX<OrderOperateLogDO>()
                .eq(abnormal.getOrderId() != null, OrderOperateLogDO::getOrderId, abnormal.getOrderId())
                .eq(abnormal.getUnitId() != null, OrderOperateLogDO::getUnitId, abnormal.getUnitId())
                .orderByDesc(OrderOperateLogDO::getOperateTime, OrderOperateLogDO::getId));

        OrderAbnormalDetailRespVO respVO = BeanUtils.toBean(abnormal, OrderAbnormalDetailRespVO.class);
        respVO.setOrder(OrderAbnormalDetailAssembler.buildOrder(order));
        respVO.setUnit(OrderAbnormalDetailAssembler.buildUnit(unit));
        respVO.setHitRule(OrderAbnormalDetailAssembler.buildRiskRule(riskRule));
        respVO.setOperateLogs(OrderAbnormalDetailAssembler.buildLogs(operateLogs));
        return respVO;
    }

    @Override
    public Long markOrderAbnormal(OrderMarkAbnormalReqVO reqVO) {
        OrderAbnormalDO orderAbnormal = new OrderAbnormalDO();
        orderAbnormal.setOrderId(reqVO.getOrderId());
        orderAbnormal.setUnitId(reqVO.getUnitId());
        orderAbnormal.setAbnormalNo("ABN" + System.currentTimeMillis());
        orderAbnormal.setAbnormalType(reqVO.getAbnormalType());
        orderAbnormal.setRiskLevel(reqVO.getRiskLevel());
        orderAbnormal.setHitRuleCode(reqVO.getHitRuleCode());
        orderAbnormal.setHandleStatus("PENDING");
        orderAbnormal.setRemark(reqVO.getRemark());
        orderAbnormal.setHandleBy(SecurityFrameworkUtils.getLoginUserId());
        orderAbnormalMapper.insert(orderAbnormal);
        return orderAbnormal.getId();
    }

    @Override
    public PageResult<OrderAbnormalRespVO> getOrderAbnormalPage(OrderAbnormalPageReqVO pageReqVO) {
        List<Long> matchedOrderIds = resolveMatchedOrderIds(pageReqVO.getOrderNo());
        if (StrUtil.isNotBlank(pageReqVO.getOrderNo()) && CollUtil.isEmpty(matchedOrderIds)) {
            return PageResult.empty();
        }
        List<Long> matchedUnitIds = resolveMatchedUnitIds(pageReqVO.getUnitNo());
        if (StrUtil.isNotBlank(pageReqVO.getUnitNo()) && CollUtil.isEmpty(matchedUnitIds)) {
            return PageResult.empty();
        }
        PageResult<OrderAbnormalDO> pageResult = orderAbnormalMapper.selectPage(pageReqVO, matchedOrderIds, matchedUnitIds);
        List<OrderAbnormalRespVO> list = BeanUtils.toBean(pageResult.getList(), OrderAbnormalRespVO.class);
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

    private void fillDisplayInfo(List<OrderAbnormalRespVO> list) {
        Set<Long> orderIds = convertSet(list, OrderAbnormalRespVO::getOrderId);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Set<Long> unitIds = convertSet(list, OrderAbnormalRespVO::getUnitId);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        list.forEach(item -> {
            OrderInfoDO order = orderMap.get(item.getOrderId());
            if (order != null) {
                item.setOrderNo(order.getOrderNo());
            }
            OrderUnitDO unit = unitMap.get(item.getUnitId());
            if (unit != null) {
                item.setUnitNo(unit.getUnitNo());
            }
        });
    }

}
