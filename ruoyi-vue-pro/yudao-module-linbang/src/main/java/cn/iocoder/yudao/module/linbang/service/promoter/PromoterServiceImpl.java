package cn.iocoder.yudao.module.linbang.service.promoter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteInviteCodeBindReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.constants.PromoterLevelConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.dividerule.DivideRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterrelation.PromoterRelationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoteroperationlog.PromoterOperationLogDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.commissionorder.CommissionOrderMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.dividerule.DivideRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterrelation.PromoterRelationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoteroperationlog.PromoterOperationLogMapper;
import org.springframework.dao.DuplicateKeyException;
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
    private PromoterOperationLogMapper promoterOperationLogMapper;
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
                .levelCode(PromoterLevelConstants.LEVEL_CODE_PRIMARY)
                .inviteCode(inviteCode)
                .inviteUrl("/pages/index/index?inviteCode=" + inviteCode + "&sourceChannel=SHARE_CARD")
                .bindUserCount(0)
                .convertCount(0)
                .totalCommissionAmount(BigDecimal.ZERO)
                .availableCommissionAmount(BigDecimal.ZERO)
                .status("ENABLE")
                .build();
        try {
            promoterMapper.insert(promoter);
            return promoter;
        } catch (DuplicateKeyException ex) {
            PromoterDO concurrent = promoterMapper.selectByUserIdForUpdate(userId);
            if (concurrent == null) {
                throw ex;
            }
            return concurrent;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindInviteCode(Long userId, AppPromoteInviteCodeBindReqVO reqVO) {
        MemberUserDO user = memberUserMapper.selectById(userId);
        if (user == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        String inviteCode = StrUtil.trim(reqVO.getInviteCode()).toUpperCase();
        PromoterDO promoter = promoterMapper.selectByInviteCode(inviteCode);
        if (promoter == null) {
            throw exception(PROMOTER_NOT_EXISTS);
        }
        if (!"ENABLE".equalsIgnoreCase(promoter.getStatus())) {
            throw exception(cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTER_DISABLED);
        }
        if (Objects.equals(promoter.getUserId(), userId)) {
            throw exception(cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTER_INVITE_SELF_BIND);
        }
        PromoterRelationDO existed = promoterRelationMapper.selectByUserId(userId);
        if (existed != null) {
            if (Objects.equals(existed.getPromoterId(), promoter.getId())) {
                return;
            }
            throw exception(cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTER_INVITE_ALREADY_BOUND);
        }
        try {
            promoterRelationMapper.insert(PromoterRelationDO.builder()
                    .promoterId(promoter.getId())
                    .userId(userId)
                    .bindTime(LocalDateTime.now())
                    .convertStatus("BOUND")
                    .inviteCode(inviteCode)
                    .sourceChannel(StrUtil.blankToDefault(reqVO.getSourceChannel(), "UNKNOWN"))
                    .sourcePage(StrUtil.sub(reqVO.getSourcePage(), 0, 255))
                    .build());
        } catch (DuplicateKeyException ex) {
            PromoterRelationDO concurrentRelation = promoterRelationMapper.selectByUserIdForUpdate(userId);
            if (concurrentRelation == null) {
                throw ex;
            }
            if (concurrentRelation != null && Objects.equals(concurrentRelation.getPromoterId(), promoter.getId())) {
                return;
            }
            throw exception(cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PROMOTER_INVITE_ALREADY_BOUND);
        }
        syncPromoterMetrics(promoter.getId());
        saveOperationLog(promoter.getId(), userId, "RELATION", userId, "BIND", null, "BOUND",
                "邀请码=" + inviteCode + "，来源=" + StrUtil.blankToDefault(reqVO.getSourceChannel(), "UNKNOWN"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderFinished(OrderInfoDO order, OrderUnitDO unit) {
        if (order == null || unit == null || order.getUserId() == null || unit.getId() == null) {
            return;
        }
        PromoterRelationDO relation = promoterRelationMapper.selectByUserId(order.getUserId());
        if (relation == null || relation.getPromoterId() == null) {
            return;
        }
        boolean firstConversion = relation.getFirstOrderId() == null
                || !"CONVERTED".equalsIgnoreCase(relation.getConvertStatus());
        if (firstConversion) {
            promoterRelationMapper.updateById(PromoterRelationDO.builder()
                    .id(relation.getId())
                    .firstOrderId(order.getId())
                    .convertStatus("CONVERTED")
                    .build());
            syncPromoterMetrics(relation.getPromoterId());
            saveOperationLog(relation.getPromoterId(), order.getUserId(), "ORDER", order.getId(),
                    "CONVERT", relation.getConvertStatus(), "CONVERTED", "完成首笔有效交易");
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
        try {
            CommissionOrderDO commissionOrder = CommissionOrderDO.builder()
                    .commissionNo("LBCM" + IdUtil.getSnowflakeNextIdStr())
                    .promoterId(relation.getPromoterId())
                    .userId(order.getUserId())
                    .sourceOrderId(order.getId())
                    .sourceUnitId(unit.getId())
                    .commissionType("ORDER")
                    .commissionAmount(commissionAmount)
                    .status("PENDING")
                    .build();
            commissionOrderMapper.insert(commissionOrder);
            saveOperationLog(relation.getPromoterId(), order.getUserId(), "COMMISSION", commissionOrder.getId(),
                    "COMMISSION_CREATE", null, "PENDING", "佣金金额=" + commissionAmount);
        } catch (DuplicateKeyException ex) {
            return;
        }
        promoterMapper.updateCommissionAmounts(relation.getPromoterId(), commissionAmount, BigDecimal.ZERO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderRefunded(OrderInfoDO order, OrderUnitDO unit) {
        if (order == null || order.getId() == null) {
            return;
        }
        List<CommissionOrderDO> commissionOrders = commissionOrderMapper.selectList(
                new LambdaQueryWrapperX<CommissionOrderDO>()
                        .eq(CommissionOrderDO::getSourceOrderId, order.getId())
                        .eqIfPresent(CommissionOrderDO::getSourceUnitId, unit == null ? null : unit.getId())
                        .ne(CommissionOrderDO::getStatus, "REFUNDED"));
        for (CommissionOrderDO commissionOrder : commissionOrders) {
            int updated = commissionOrderMapper.update(null, new LambdaUpdateWrapper<CommissionOrderDO>()
                    .eq(CommissionOrderDO::getId, commissionOrder.getId())
                    .eq(CommissionOrderDO::getStatus, commissionOrder.getStatus())
                    .ne(CommissionOrderDO::getStatus, "REFUNDED")
                    .set(CommissionOrderDO::getStatus, "REFUNDED"));
            if (updated == 0) {
                continue;
            }
            saveOperationLog(commissionOrder.getPromoterId(), commissionOrder.getUserId(), "COMMISSION",
                    commissionOrder.getId(), "COMMISSION_REFUND", commissionOrder.getStatus(), "REFUNDED",
                    "来源订单退款，佣金冲正");
            BigDecimal amount = defaultAmount(commissionOrder.getCommissionAmount());
            promoterMapper.updateCommissionAmounts(commissionOrder.getPromoterId(), amount.negate(),
                    "SETTLED".equalsIgnoreCase(commissionOrder.getStatus()) ? amount.negate() : BigDecimal.ZERO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PromoterDO syncPromoterMetrics(Long promoterId) {
        PromoterDO promoter = promoterMapper.selectById(promoterId);
        if (promoter == null) {
            throw exception(PROMOTER_NOT_EXISTS);
        }
        int bindCount = Math.toIntExact(promoterRelationMapper.selectCount(new LambdaQueryWrapperX<PromoterRelationDO>()
                .eq(PromoterRelationDO::getPromoterId, promoterId)));
        int convertCount = Math.toIntExact(promoterRelationMapper.selectCount(new LambdaQueryWrapperX<PromoterRelationDO>()
                .eq(PromoterRelationDO::getPromoterId, promoterId)
                .eq(PromoterRelationDO::getConvertStatus, "CONVERTED")));
        String levelCode = resolveLevelCode(bindCount);
        if (!Objects.equals(promoter.getBindUserCount(), bindCount)
                || !Objects.equals(promoter.getConvertCount(), convertCount)
                || !Objects.equals(promoter.getLevelCode(), levelCode)) {
            promoterMapper.updateById(PromoterDO.builder().id(promoterId)
                    .bindUserCount(bindCount).convertCount(convertCount).levelCode(levelCode).build());
            promoter.setBindUserCount(bindCount);
            promoter.setConvertCount(convertCount);
            promoter.setLevelCode(levelCode);
        }
        return promoter;
    }

    @Override
    public PageResult<PromoterRespVO> getPromoterPage(PromoterPageReqVO reqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<PromoterDO> pageResult = promoterMapper.selectPage(reqVO, matchedUserIds);
        List<PromoterRespVO> list = BeanUtils.toBean(pageResult.getList(), PromoterRespVO.class);
        list.forEach(item -> {
            PromoterDO synced = syncPromoterMetrics(item.getId());
            item.setLevelCode(synced.getLevelCode());
            item.setBindUserCount(synced.getBindUserCount());
            item.setConvertCount(synced.getConvertCount());
            item.setPendingConvertCount(Math.max(0,
                    defaultInt(synced.getBindUserCount()) - defaultInt(synced.getConvertCount())));
        });
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public PromoterDetailRespVO getPromoterDetail(Long id) {
        PromoterDO promoter = promoterMapper.selectById(id);
        if (promoter == null) {
            throw exception(PROMOTER_NOT_EXISTS);
        }
        promoter = syncPromoterMetrics(id);
        MemberUserDO user = promoter.getUserId() == null ? null : memberUserMapper.selectById(promoter.getUserId());
        List<PromoterRelationDO> relations = promoterRelationMapper.selectList(new LambdaQueryWrapperX<PromoterRelationDO>()
                .eq(PromoterRelationDO::getPromoterId, id)
                .orderByDesc(PromoterRelationDO::getId));
        List<CommissionOrderDO> commissionOrders = commissionOrderMapper.selectList(new LambdaQueryWrapperX<CommissionOrderDO>()
                .eq(CommissionOrderDO::getPromoterId, id)
                .orderByDesc(CommissionOrderDO::getId));
        List<PromoterOperationLogDO> operationLogs = promoterOperationLogMapper.selectList(
                new LambdaQueryWrapperX<PromoterOperationLogDO>()
                        .eq(PromoterOperationLogDO::getPromoterId, id)
                        .orderByDesc(PromoterOperationLogDO::getId)
                        .last("LIMIT 20"));
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
        return PromoterDetailAssembler.build(promoter, user, relations, commissionOrders, relatedUserMap, orderMap,
                unitMap, operationLogs);
    }

    @Override
    public void updatePromoterStatus(PromoterStatusUpdateReqVO reqVO) {
        PromoterDO promoter = promoterMapper.selectById(reqVO.getId());
        if (promoter == null) {
            throw exception(PROMOTER_NOT_EXISTS);
        }
        promoterMapper.updateById(PromoterDO.builder().id(reqVO.getId()).status(reqVO.getStatus()).build());
        saveOperationLog(promoter.getId(), promoter.getUserId(), "PROMOTER", promoter.getId(), "STATUS_CHANGE",
                promoter.getStatus(), reqVO.getStatus(), "管理端变更推广员状态");
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

    private BigDecimal nonNegative(BigDecimal amount) {
        return amount.max(BigDecimal.ZERO);
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String resolveLevelCode(int bindCount) {
        if (bindCount >= PromoterLevelConstants.PROMOTER_SENIOR_THRESHOLD) {
            return PromoterLevelConstants.LEVEL_CODE_SENIOR;
        }
        if (bindCount >= PromoterLevelConstants.PROMOTER_MIDDLE_THRESHOLD) {
            return PromoterLevelConstants.LEVEL_CODE_MIDDLE;
        }
        return PromoterLevelConstants.LEVEL_CODE_PRIMARY;
    }

    private void saveOperationLog(Long promoterId, Long userId, String bizType, Long bizId, String operationType,
                                  String beforeStatus, String afterStatus, String remark) {
        promoterOperationLogMapper.insert(PromoterOperationLogDO.builder()
                .promoterId(promoterId).userId(userId).bizType(bizType).bizId(bizId)
                .operationType(operationType).beforeStatus(beforeStatus).afterStatus(afterStatus).remark(remark)
                .build());
    }
}
