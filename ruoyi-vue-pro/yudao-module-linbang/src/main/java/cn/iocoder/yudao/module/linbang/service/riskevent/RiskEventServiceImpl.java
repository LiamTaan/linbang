package cn.iocoder.yudao.module.linbang.service.riskevent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventBizMatchCondition;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderabnormal.OrderAbnormalDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskevent.RiskEventDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderabnormal.OrderAbnormalMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.riskrule.RiskRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.riskevent.RiskEventMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.RISK_EVENT_NOT_EXISTS;

@Service
@Validated
public class RiskEventServiceImpl implements RiskEventService {

    private static final String BIZ_TYPE_ORDER = "ORDER";
    private static final String BIZ_TYPE_ORDER_INFO = "ORDER_INFO";
    private static final String BIZ_TYPE_ORDER_UNIT = "ORDER_UNIT";
    private static final String BIZ_TYPE_UNIT = "UNIT";
    private static final String BIZ_TYPE_ORDER_ABNORMAL = "ORDER_ABNORMAL";
    private static final String BIZ_TYPE_ABNORMAL = "ABNORMAL";
    private static final String BIZ_TYPE_COMPLAINT = "COMPLAINT";
    private static final String BIZ_TYPE_APPEAL = "APPEAL";
    private static final String BIZ_TYPE_WITHDRAW = "WITHDRAW";
    private static final String BIZ_TYPE_WITHDRAW_APPLY = "WITHDRAW_APPLY";
    private static final String BIZ_TYPE_USER = "USER";

    @Resource
    private RiskEventMapper riskEventMapper;
    @Resource
    private RiskRuleMapper riskRuleMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderAbnormalMapper orderAbnormalMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private AppealMapper appealMapper;
    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;

    @Override
    public RiskEventDetailRespVO getRiskEventDetail(Long id) {
        RiskEventDO riskEvent = riskEventMapper.selectById(id);
        if (riskEvent == null) {
            throw exception(RISK_EVENT_NOT_EXISTS);
        }

        RiskRuleDO riskRule = riskEvent.getHitRuleCode() == null ? null : riskRuleMapper.selectOne(
                new LambdaQueryWrapperX<RiskRuleDO>().eq(RiskRuleDO::getRuleCode, riskEvent.getHitRuleCode()));
        RiskEventDetailContext context = loadContext(riskEvent);
        List<OrderOperateLogDO> orderOperateLogs = loadOrderOperateLogs(context);

        RiskEventDetailRespVO respVO = BeanUtils.toBean(riskEvent, RiskEventDetailRespVO.class);
        respVO.setHitRule(RiskEventDetailAssembler.buildRiskRule(riskRule));
        respVO.setOrder(RiskEventDetailAssembler.buildOrder(context.order, context.orderUser, context.orderMerchant));
        respVO.setUnit(RiskEventDetailAssembler.buildUnit(context.unit, context.unitMerchant));
        respVO.setAbnormal(RiskEventDetailAssembler.buildAbnormal(context.abnormal));
        respVO.setComplaint(RiskEventDetailAssembler.buildComplaint(context.complaint));
        respVO.setAppeal(RiskEventDetailAssembler.buildAppeal(context.appeal));
        respVO.setWithdraw(RiskEventDetailAssembler.buildWithdraw(context.withdraw));
        respVO.setOrderOperateLogs(RiskEventDetailAssembler.buildLogs(orderOperateLogs));
        return respVO;
    }

    @Override
    public PageResult<RiskEventRespVO> getRiskEventPage(RiskEventPageReqVO reqVO) {
        List<RiskEventBizMatchCondition> bizMatchConditions = resolveBizMatchConditions(reqVO);
        if (StrUtil.isNotBlank(reqVO.getBizKeyword()) && CollUtil.isEmpty(bizMatchConditions)) {
            return PageResult.empty();
        }
        PageResult<RiskEventDO> pageResult = riskEventMapper.selectPage(reqVO, bizMatchConditions);
        List<RiskEventRespVO> list = BeanUtils.toBean(pageResult.getList(), RiskEventRespVO.class);
        fillBizDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<RiskEventBizMatchCondition> resolveBizMatchConditions(RiskEventPageReqVO reqVO) {
        if (StrUtil.isBlank(reqVO.getBizKeyword())) {
            return null;
        }
        String bizType = StrUtil.trimToEmpty(reqVO.getBizType()).toUpperCase();
        if (StrUtil.isNotBlank(bizType)) {
            return resolveBizMatchConditionsByType(bizType, reqVO.getBizKeyword());
        }
        List<RiskEventBizMatchCondition> conditions = new ArrayList<>();
        addIfPresent(conditions, BIZ_TYPE_ORDER, resolveOrderIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_ORDER_INFO, resolveOrderIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_ORDER_UNIT, resolveUnitIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_UNIT, resolveUnitIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_ORDER_ABNORMAL, resolveAbnormalIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_ABNORMAL, resolveAbnormalIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_COMPLAINT, resolveComplaintIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_APPEAL, resolveAppealIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_WITHDRAW, resolveWithdrawIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_WITHDRAW_APPLY, resolveWithdrawIds(reqVO.getBizKeyword()));
        addIfPresent(conditions, BIZ_TYPE_USER, resolveUserIds(reqVO.getBizKeyword()));
        return conditions;
    }

    private List<RiskEventBizMatchCondition> resolveBizMatchConditionsByType(String bizType, String bizKeyword) {
        List<RiskEventBizMatchCondition> conditions = new ArrayList<>();
        switch (bizType) {
            case BIZ_TYPE_ORDER:
            case BIZ_TYPE_ORDER_INFO:
                addIfPresent(conditions, bizType, resolveOrderIds(bizKeyword));
                break;
            case BIZ_TYPE_ORDER_UNIT:
            case BIZ_TYPE_UNIT:
                addIfPresent(conditions, bizType, resolveUnitIds(bizKeyword));
                break;
            case BIZ_TYPE_ORDER_ABNORMAL:
            case BIZ_TYPE_ABNORMAL:
                addIfPresent(conditions, bizType, resolveAbnormalIds(bizKeyword));
                break;
            case BIZ_TYPE_COMPLAINT:
                addIfPresent(conditions, bizType, resolveComplaintIds(bizKeyword));
                break;
            case BIZ_TYPE_APPEAL:
                addIfPresent(conditions, bizType, resolveAppealIds(bizKeyword));
                break;
            case BIZ_TYPE_WITHDRAW:
            case BIZ_TYPE_WITHDRAW_APPLY:
                addIfPresent(conditions, bizType, resolveWithdrawIds(bizKeyword));
                break;
            case BIZ_TYPE_USER:
                addIfPresent(conditions, bizType, resolveUserIds(bizKeyword));
                break;
            default:
                if (NumberUtil.isLong(bizKeyword)) {
                    addIfPresent(conditions, bizType, Collections.singletonList(Long.parseLong(bizKeyword)));
                }
                break;
        }
        return conditions;
    }

    private void addIfPresent(List<RiskEventBizMatchCondition> conditions, String bizType, Collection<Long> bizIds) {
        if (CollUtil.isNotEmpty(bizIds)) {
            conditions.add(new RiskEventBizMatchCondition(bizType, bizIds));
        }
    }

    private List<Long> resolveOrderIds(String bizKeyword) {
        return convertList(orderInfoMapper.selectListByOrderNo(bizKeyword), OrderInfoDO::getId);
    }

    private List<Long> resolveUnitIds(String bizKeyword) {
        return convertList(orderUnitMapper.selectListByUnitNo(bizKeyword), OrderUnitDO::getId);
    }

    private List<Long> resolveAbnormalIds(String bizKeyword) {
        return convertList(orderAbnormalMapper.selectListByAbnormalNo(bizKeyword), OrderAbnormalDO::getId);
    }

    private List<Long> resolveComplaintIds(String bizKeyword) {
        return convertList(complaintMapper.selectListByComplaintNo(bizKeyword), ComplaintDO::getId);
    }

    private List<Long> resolveAppealIds(String bizKeyword) {
        return convertList(appealMapper.selectListByAppealNo(bizKeyword), AppealDO::getId);
    }

    private List<Long> resolveWithdrawIds(String bizKeyword) {
        return convertList(walletWithdrawMapper.selectListByWithdrawNo(bizKeyword), WalletWithdrawDO::getId);
    }

    private List<Long> resolveUserIds(String bizKeyword) {
        return convertList(memberUserMapper.selectListByKeyword(bizKeyword), MemberUserDO::getId);
    }

    private void fillBizDisplayInfo(List<RiskEventRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Map<Long, OrderInfoDO> orderMap = convertMap(
                orderInfoMapper.selectBatchIds(collectBizIds(list, BIZ_TYPE_ORDER, BIZ_TYPE_ORDER_INFO)),
                OrderInfoDO::getId);
        Map<Long, OrderUnitDO> unitMap = convertMap(
                orderUnitMapper.selectBatchIds(collectBizIds(list, BIZ_TYPE_ORDER_UNIT, BIZ_TYPE_UNIT)),
                OrderUnitDO::getId);
        Map<Long, OrderAbnormalDO> abnormalMap = convertMap(
                orderAbnormalMapper.selectBatchIds(collectBizIds(list, BIZ_TYPE_ORDER_ABNORMAL, BIZ_TYPE_ABNORMAL)),
                OrderAbnormalDO::getId);
        Map<Long, ComplaintDO> complaintMap = convertMap(
                complaintMapper.selectBatchIds(collectBizIds(list, BIZ_TYPE_COMPLAINT)),
                ComplaintDO::getId);
        Map<Long, AppealDO> appealMap = convertMap(
                appealMapper.selectBatchIds(collectBizIds(list, BIZ_TYPE_APPEAL)),
                AppealDO::getId);
        Map<Long, WalletWithdrawDO> withdrawMap = convertMap(
                walletWithdrawMapper.selectBatchIds(collectBizIds(list, BIZ_TYPE_WITHDRAW, BIZ_TYPE_WITHDRAW_APPLY)),
                WalletWithdrawDO::getId);
        Map<Long, MemberUserDO> userMap = convertMap(
                memberUserMapper.selectListByIds(collectBizIds(list, BIZ_TYPE_USER)),
                MemberUserDO::getId);

        list.forEach(item -> item.setBizDisplay(buildBizDisplay(item, orderMap, unitMap, abnormalMap,
                complaintMap, appealMap, withdrawMap, userMap)));
    }

    private Set<Long> collectBizIds(List<RiskEventRespVO> list, String... bizTypes) {
        Collection<String> bizTypeList = java.util.Arrays.asList(bizTypes);
        return convertSet(list, RiskEventRespVO::getBizId,
                item -> item.getBizId() != null && item.getBizType() != null
                        && bizTypeList.contains(item.getBizType().toUpperCase()));
    }

    private String buildBizDisplay(RiskEventRespVO item, Map<Long, OrderInfoDO> orderMap,
                                   Map<Long, OrderUnitDO> unitMap,
                                   Map<Long, OrderAbnormalDO> abnormalMap,
                                   Map<Long, ComplaintDO> complaintMap,
                                   Map<Long, AppealDO> appealMap,
                                   Map<Long, WalletWithdrawDO> withdrawMap,
                                   Map<Long, MemberUserDO> userMap) {
        if (item.getBizId() == null) {
            return "-";
        }
        String bizType = StrUtil.trimToEmpty(item.getBizType()).toUpperCase();
        switch (bizType) {
            case BIZ_TYPE_ORDER:
            case BIZ_TYPE_ORDER_INFO:
                OrderInfoDO order = orderMap.get(item.getBizId());
                return order != null ? StrUtil.blankToDefault(order.getOrderNo(), "订单ID：" + item.getBizId()) : "订单ID：" + item.getBizId();
            case BIZ_TYPE_ORDER_UNIT:
            case BIZ_TYPE_UNIT:
                OrderUnitDO unit = unitMap.get(item.getBizId());
                return unit != null ? StrUtil.blankToDefault(unit.getUnitNo(), "单元ID：" + item.getBizId()) : "单元ID：" + item.getBizId();
            case BIZ_TYPE_ORDER_ABNORMAL:
            case BIZ_TYPE_ABNORMAL:
                OrderAbnormalDO abnormal = abnormalMap.get(item.getBizId());
                return abnormal != null ? StrUtil.blankToDefault(abnormal.getAbnormalNo(), "异常单ID：" + item.getBizId()) : "异常单ID：" + item.getBizId();
            case BIZ_TYPE_COMPLAINT:
                ComplaintDO complaint = complaintMap.get(item.getBizId());
                return complaint != null ? StrUtil.blankToDefault(complaint.getComplaintNo(), "投诉ID：" + item.getBizId()) : "投诉ID：" + item.getBizId();
            case BIZ_TYPE_APPEAL:
                AppealDO appeal = appealMap.get(item.getBizId());
                return appeal != null ? StrUtil.blankToDefault(appeal.getAppealNo(), "申诉ID：" + item.getBizId()) : "申诉ID：" + item.getBizId();
            case BIZ_TYPE_WITHDRAW:
            case BIZ_TYPE_WITHDRAW_APPLY:
                WalletWithdrawDO withdraw = withdrawMap.get(item.getBizId());
                return withdraw != null ? StrUtil.blankToDefault(withdraw.getWithdrawNo(), "提现ID：" + item.getBizId()) : "提现ID：" + item.getBizId();
            case BIZ_TYPE_USER:
                MemberUserDO user = userMap.get(item.getBizId());
                if (user == null) {
                    return "用户ID：" + item.getBizId();
                }
                String summary = String.join(" / ", buildNonBlankParts(user.getNickname(), user.getMobile(), user.getUserNo()));
                return StrUtil.blankToDefault(summary, "用户ID：" + item.getBizId());
            default:
                return "业务ID：" + item.getBizId();
        }
    }

    private List<String> buildNonBlankParts(String... values) {
        List<String> parts = new ArrayList<>();
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                parts.add(value);
            }
        }
        return parts;
    }

    private RiskEventDetailContext loadContext(RiskEventDO riskEvent) {
        RiskEventDetailContext context = new RiskEventDetailContext();
        String bizType = riskEvent.getBizType();
        Long bizId = riskEvent.getBizId();
        if (bizId == null) {
            return context;
        }
        if ("ORDER".equalsIgnoreCase(bizType) || "ORDER_INFO".equalsIgnoreCase(bizType)) {
            context.order = orderInfoMapper.selectById(bizId);
        } else if ("ORDER_UNIT".equalsIgnoreCase(bizType) || "UNIT".equalsIgnoreCase(bizType)) {
            context.unit = orderUnitMapper.selectById(bizId);
            if (context.unit != null) {
                context.order = orderInfoMapper.selectById(context.unit.getOrderId());
            }
        } else if ("ORDER_ABNORMAL".equalsIgnoreCase(bizType) || "ABNORMAL".equalsIgnoreCase(bizType)) {
            context.abnormal = orderAbnormalMapper.selectById(bizId);
            if (context.abnormal != null) {
                context.order = context.abnormal.getOrderId() == null ? null : orderInfoMapper.selectById(context.abnormal.getOrderId());
                context.unit = context.abnormal.getUnitId() == null ? null : orderUnitMapper.selectById(context.abnormal.getUnitId());
            }
        } else if ("COMPLAINT".equalsIgnoreCase(bizType)) {
            context.complaint = complaintMapper.selectById(bizId);
            if (context.complaint != null) {
                context.order = context.complaint.getOrderId() == null ? null : orderInfoMapper.selectById(context.complaint.getOrderId());
                context.unit = context.complaint.getUnitId() == null ? null : orderUnitMapper.selectById(context.complaint.getUnitId());
            }
        } else if ("APPEAL".equalsIgnoreCase(bizType)) {
            context.appeal = appealMapper.selectById(bizId);
            if (context.appeal != null) {
                context.order = context.appeal.getOrderId() == null ? null : orderInfoMapper.selectById(context.appeal.getOrderId());
                context.unit = context.appeal.getUnitId() == null ? null : orderUnitMapper.selectById(context.appeal.getUnitId());
            }
        } else if ("WITHDRAW".equalsIgnoreCase(bizType) || "WITHDRAW_APPLY".equalsIgnoreCase(bizType)) {
            context.withdraw = walletWithdrawMapper.selectById(bizId);
        } else if ("USER".equalsIgnoreCase(bizType)) {
            context.orderUser = memberUserMapper.selectById(bizId);
        }
        if (context.unit != null && context.order == null && context.unit.getOrderId() != null) {
            context.order = orderInfoMapper.selectById(context.unit.getOrderId());
        }
        if (context.order != null && context.order.getUserId() != null) {
            context.orderUser = memberUserMapper.selectById(context.order.getUserId());
        }
        if (context.order != null && context.order.getMerchantId() != null) {
            context.orderMerchant = merchantInfoMapper.selectById(context.order.getMerchantId());
        }
        if (context.unit != null && context.unit.getMerchantId() != null) {
            context.unitMerchant = merchantInfoMapper.selectById(context.unit.getMerchantId());
        }
        return context;
    }

    private List<OrderOperateLogDO> loadOrderOperateLogs(RiskEventDetailContext context) {
        if (context.order == null && context.unit == null) {
            return Collections.emptyList();
        }
        return orderOperateLogMapper.selectList(new LambdaQueryWrapperX<OrderOperateLogDO>()
                .eq(context.order != null, OrderOperateLogDO::getOrderId, context.order != null ? context.order.getId() : null)
                .eq(context.unit != null, OrderOperateLogDO::getUnitId, context.unit != null ? context.unit.getId() : null)
                .orderByDesc(OrderOperateLogDO::getOperateTime, OrderOperateLogDO::getId));
    }

    private static class RiskEventDetailContext {
        private OrderInfoDO order;
        private MemberUserDO orderUser;
        private MerchantInfoDO orderMerchant;
        private OrderUnitDO unit;
        private MerchantInfoDO unitMerchant;
        private OrderAbnormalDO abnormal;
        private ComplaintDO complaint;
        private AppealDO appeal;
        private WalletWithdrawDO withdraw;
    }
}
