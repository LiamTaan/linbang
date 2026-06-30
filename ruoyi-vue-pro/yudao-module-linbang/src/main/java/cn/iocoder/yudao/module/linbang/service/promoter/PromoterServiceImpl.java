package cn.iocoder.yudao.module.linbang.service.promoter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteInviteCodeBindReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.dividerule.DivideRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterrelation.PromoterRelationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.commissionorder.CommissionOrderMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.dividerule.DivideRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterrelation.PromoterRelationMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTER_NOT_EXISTS;

@Service
@Validated
public class PromoterServiceImpl implements PromoterService {

    @Resource
    private PromoterMapper promoterMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private CommissionOrderMapper commissionOrderMapper;
    @Resource
    private PromoterRelationMapper promoterRelationMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private DivideRuleMapper divideRuleMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Override
    public PromoterDO getPromoterByUserId(Long userId) {
        return promoterMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PromoterDO getOrCreatePromoter(Long userId) {
        PromoterDO promoter = promoterMapper.selectByUserId(userId);
        if (promoter != null) {
            return promoter;
        }
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        String inviteCode = "LB" + IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
        promoter = PromoterDO.builder()
                .userId(userId)
                .levelCode("NORMAL")
                .inviteCode(inviteCode)
                .inviteUrl("/app/promote?code=" + inviteCode)
                .bindUserCount(0)
                .convertCount(0)
                .totalCommissionAmount(BigDecimal.ZERO)
                .availableCommissionAmount(BigDecimal.ZERO)
                .status("ENABLE")
                .build();
        promoterMapper.insert(promoter);
        return promoter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindInviteCode(Long userId, AppPromoteInviteCodeBindReqVO reqVO) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        PromoterDO promoter = promoterMapper.selectByInviteCode(reqVO.getInviteCode());
        if (promoter == null) {
            throw exception(PROMOTER_NOT_EXISTS);
        }
        if (Objects.equals(promoter.getUserId(), userId)) {
            return;
        }
        PromoterRelationDO existed = promoterRelationMapper.selectByPromoterIdAndUserId(promoter.getId(), userId);
        if (existed != null) {
            return;
        }
        promoterRelationMapper.insert(PromoterRelationDO.builder()
                .promoterId(promoter.getId())
                .userId(userId)
                .bindTime(LocalDateTime.now())
                .convertStatus("BOUND")
                .build());
        promoterMapper.updateById(PromoterDO.builder()
                .id(promoter.getId())
                .bindUserCount(defaultInt(promoter.getBindUserCount()) + 1)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderFinished(OrderInfoDO order, OrderUnitDO unit) {
        if (order == null || unit == null || order.getUserId() == null || unit.getId() == null) {
            return;
        }
        PromoterRelationDO relation = promoterRelationMapper.selectOne(new LambdaQueryWrapperX<PromoterRelationDO>()
                .eq(PromoterRelationDO::getUserId, order.getUserId())
                .orderByAsc(PromoterRelationDO::getId)
                .last("LIMIT 1"));
        if (relation == null || relation.getPromoterId() == null) {
            return;
        }
        CommissionOrderDO existed = commissionOrderMapper.selectOne(new LambdaQueryWrapperX<CommissionOrderDO>()
                .eq(CommissionOrderDO::getPromoterId, relation.getPromoterId())
                .eq(CommissionOrderDO::getUserId, order.getUserId())
                .eq(CommissionOrderDO::getSourceOrderId, order.getId())
                .eq(CommissionOrderDO::getSourceUnitId, unit.getId())
                .last("LIMIT 1"));
        if (existed != null) {
            return;
        }
        DivideRuleDO divideRule = resolveDivideRule(order.getCategoryId());
        if (divideRule == null || divideRule.getPromoterRate() == null
                || divideRule.getPromoterRate().compareTo(BigDecimal.ZERO) <= 0
                || order.getOrderAmount() == null || order.getOrderAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal commissionAmount = order.getOrderAmount()
                .multiply(divideRule.getPromoterRate())
                .setScale(2, RoundingMode.HALF_UP);
        if (commissionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        commissionOrderMapper.insert(CommissionOrderDO.builder()
                .commissionNo("LBCM" + IdUtil.getSnowflakeNextIdStr())
                .promoterId(relation.getPromoterId())
                .userId(order.getUserId())
                .sourceOrderId(order.getId())
                .sourceUnitId(unit.getId())
                .commissionType("ORDER")
                .commissionAmount(commissionAmount)
                .status("PENDING")
                .build());
        PromoterDO promoter = promoterMapper.selectById(relation.getPromoterId());
        if (promoter != null) {
            promoterMapper.updateById(PromoterDO.builder()
                    .id(promoter.getId())
                    .convertCount(nextConvertCount(relation, promoter))
                    .totalCommissionAmount(defaultAmount(promoter.getTotalCommissionAmount()).add(commissionAmount))
                    .availableCommissionAmount(defaultAmount(promoter.getAvailableCommissionAmount()).add(commissionAmount))
                    .build());
            messagePushDispatchService.dispatchSingle("FINANCE_COMMISSION_SETTLED", "佣金结算通知", "COMMISSION",
                    order.getId(), promoter.getUserId(), "推广佣金已入账，请留意收益变化");
        }
        if (relation.getFirstOrderId() == null || !"CONVERTED".equalsIgnoreCase(relation.getConvertStatus())) {
            promoterRelationMapper.updateById(PromoterRelationDO.builder()
                    .id(relation.getId())
                    .firstOrderId(order.getId())
                    .convertStatus("CONVERTED")
                    .build());
        }
    }

    @Override
    public PageResult<PromoterRespVO> getPromoterPage(PromoterPageReqVO reqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<PromoterDO> pageResult = promoterMapper.selectPage(reqVO, matchedUserIds);
        List<PromoterRespVO> list = BeanUtils.toBean(pageResult.getList(), PromoterRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public PromoterDetailRespVO getPromoterDetail(Long id) {
        PromoterDO promoter = promoterMapper.selectById(id);
        if (promoter == null) {
            throw exception(PROMOTER_NOT_EXISTS);
        }
        MemberUserDO user = promoter.getUserId() == null ? null : memberUserMapper.selectById(promoter.getUserId());
        List<PromoterRelationDO> relations = promoterRelationMapper.selectList(new LambdaQueryWrapperX<PromoterRelationDO>()
                .eq(PromoterRelationDO::getPromoterId, id)
                .orderByDesc(PromoterRelationDO::getId));
        List<CommissionOrderDO> commissionOrders = commissionOrderMapper.selectList(new LambdaQueryWrapperX<CommissionOrderDO>()
                .eq(CommissionOrderDO::getPromoterId, id)
                .orderByDesc(CommissionOrderDO::getId));
        Set<Long> relatedUserIds = new HashSet<>();
        relations.forEach(item -> {
            if (item.getUserId() != null) {
                relatedUserIds.add(item.getUserId());
            }
        });
        commissionOrders.forEach(item -> {
            if (item.getUserId() != null) {
                relatedUserIds.add(item.getUserId());
            }
        });
        Set<Long> orderIds = new HashSet<>();
        relations.forEach(item -> {
            if (item.getFirstOrderId() != null) {
                orderIds.add(item.getFirstOrderId());
            }
        });
        commissionOrders.forEach(item -> {
            if (item.getSourceOrderId() != null) {
                orderIds.add(item.getSourceOrderId());
            }
        });
        Set<Long> unitIds = convertSet(commissionOrders, CommissionOrderDO::getSourceUnitId,
                item -> item.getSourceUnitId() != null);
        Map<Long, MemberUserDO> relatedUserMap = relatedUserIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(relatedUserIds), MemberUserDO::getId);
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        return PromoterDetailAssembler.build(promoter, user, relations, commissionOrders, relatedUserMap, orderMap, unitMap);
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<PromoterRespVO> list) {
        Set<Long> userIds = convertSet(list, PromoterRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? java.util.Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
                return;
            }
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
        });
    }

    private DivideRuleDO resolveDivideRule(Long categoryId) {
        DivideRuleDO matched = divideRuleMapper.selectOne(new LambdaQueryWrapperX<DivideRuleDO>()
                .eq(DivideRuleDO::getStatus, "ENABLE")
                .eqIfPresent(DivideRuleDO::getCategoryId, categoryId)
                .orderByDesc(DivideRuleDO::getEffectiveTime, DivideRuleDO::getId)
                .last("LIMIT 1"));
        if (matched != null) {
            return matched;
        }
        return divideRuleMapper.selectOne(new LambdaQueryWrapperX<DivideRuleDO>()
                .eq(DivideRuleDO::getStatus, "ENABLE")
                .orderByDesc(DivideRuleDO::getEffectiveTime, DivideRuleDO::getId)
                .last("LIMIT 1"));
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private Integer nextConvertCount(PromoterRelationDO relation, PromoterDO promoter) {
        if (relation.getFirstOrderId() != null || "CONVERTED".equalsIgnoreCase(relation.getConvertStatus())) {
            return promoter.getConvertCount();
        }
        return defaultInt(promoter.getConvertCount()) + 1;
    }
}
