package cn.iocoder.yudao.module.linbang.service.ordersplitrule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRulePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRuleRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRuleSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordersplitrule.OrderSplitRuleDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.ordersplitrule.OrderSplitRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_SPLIT_PLAN_GENERATE_FAILED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_SPLIT_RULE_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_UNIT_AMOUNT_EXCEED_LIMIT;

@Service
@Validated
public class OrderSplitRuleServiceImpl implements OrderSplitRuleService {

    private static final BigDecimal DEFAULT_LIMIT = new BigDecimal("200.00");

    @Resource
    private OrderSplitRuleMapper orderSplitRuleMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;

    @Override
    public Long createOrderSplitRule(@Valid OrderSplitRuleSaveReqVO createReqVO) {
        OrderSplitRuleDO rule = buildRule(createReqVO);
        orderSplitRuleMapper.insert(rule);
        return rule.getId();
    }

    @Override
    public void updateOrderSplitRule(@Valid OrderSplitRuleSaveReqVO updateReqVO) {
        validateOrderSplitRuleExists(updateReqVO.getId());
        orderSplitRuleMapper.updateById(buildRule(updateReqVO));
    }

    @Override
    public void deleteOrderSplitRule(Long id) {
        validateOrderSplitRuleExists(id);
        orderSplitRuleMapper.deleteById(id);
    }

    @Override
    public OrderSplitRuleRespVO getOrderSplitRule(Long id) {
        OrderSplitRuleDO rule = validateOrderSplitRuleExists(id);
        return toRespVO(rule, loadCategoryName(rule.getCategoryId()));
    }

    @Override
    public PageResult<OrderSplitRuleRespVO> getOrderSplitRulePage(OrderSplitRulePageReqVO pageReqVO) {
        PageResult<OrderSplitRuleDO> pageResult = orderSplitRuleMapper.selectPage(pageReqVO);
        Map<Long, String> categoryNameMap = buildCategoryNameMap(pageResult.getList());
        List<OrderSplitRuleRespVO> list = pageResult.getList().stream()
                .map(rule -> toRespVO(rule, categoryNameMap.get(rule.getCategoryId())))
                .collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public OrderSplitPlan matchRule(OrderSplitPreviewContext context) {
        BigDecimal orderAmount = OptionalValue.of(context.getOrderAmount(), BigDecimal.ZERO);
        int safeWorkerCount = context.getWorkerCount() == null || context.getWorkerCount() < 1 ? 1 : context.getWorkerCount();
        List<OrderSplitRuleDO> rules = orderSplitRuleMapper.selectList(new LambdaQueryWrapperX<OrderSplitRuleDO>()
                .eq(OrderSplitRuleDO::getStatus, "ENABLE")
                .orderByAsc(OrderSplitRuleDO::getSortNo, OrderSplitRuleDO::getId));
        for (OrderSplitRuleDO rule : rules) {
            if (!matchesRule(rule, context, safeWorkerCount)) {
                continue;
            }
            return buildRulePlan(rule, context, orderAmount, safeWorkerCount);
        }
        return buildDirectPlan(context, orderAmount, safeWorkerCount);
    }

    private OrderSplitPlan buildRulePlan(OrderSplitRuleDO rule, OrderSplitPreviewContext context,
                                         BigDecimal orderAmount, int safeWorkerCount) {
        if ("DIRECT".equalsIgnoreCase(rule.getSplitMode())) {
            return buildDirectPlan(context, orderAmount, safeWorkerCount);
        }
        int unitCount = resolveUnitCount(rule, context, orderAmount, safeWorkerCount);
        if (unitCount <= 1) {
            unitCount = Math.max(OptionalValue.of(rule.getDefaultUnitCount(), 2), 2);
        }
        BigDecimal limit = OptionalValue.of(rule.getUnitAmountLimit(), DEFAULT_LIMIT);
        List<BigDecimal> amounts = splitAmounts(orderAmount, unitCount, limit);
        List<OrderSplitPlan.OrderSplitUnitPlan> units = buildUnits(context, amounts, rule.getSplitMode(),
                safeWorkerCount, limit, parseUnitTemplate(rule.getUnitTemplate()));
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("ruleId", rule.getId());
        snapshot.put("ruleName", rule.getRuleName());
        snapshot.put("ruleCode", rule.getRuleCode());
        snapshot.put("matchMode", rule.getMatchMode());
        snapshot.put("categoryId", rule.getCategoryId());
        snapshot.put("applicablePricingModes", parsePricingModes(rule.getApplicablePricingModes()));
        snapshot.put("minOrderAmount", rule.getMinOrderAmount());
        snapshot.put("minQuantity", rule.getMinQuantity());
        snapshot.put("minWorkerCount", rule.getMinWorkerCount());
        snapshot.put("splitMode", rule.getSplitMode());
        snapshot.put("defaultUnitCount", rule.getDefaultUnitCount());
        snapshot.put("unitAmountLimit", limit);
        snapshot.put("unitTemplate", parseUnitTemplate(rule.getUnitTemplate()));
        snapshot.put("generatedUnitCount", unitCount);
        return OrderSplitPlan.builder()
                .matched(true)
                .splitRequired(unitCount > 1)
                .ruleId(rule.getId())
                .ruleName(rule.getRuleName())
                .ruleCode(rule.getRuleCode())
                .matchMode(rule.getMatchMode())
                .splitMode(rule.getSplitMode())
                .unitAmountLimit(limit)
                .unitCount(unitCount)
                .ruleSnapshot(JsonUtils.toJsonString(snapshot))
                .units(units)
                .build();
    }

    private OrderSplitPlan buildDirectPlan(OrderSplitPreviewContext context, BigDecimal orderAmount, int safeWorkerCount) {
        List<OrderSplitPlan.OrderSplitUnitPlan> units = buildUnits(context,
                Collections.singletonList(orderAmount), "DIRECT", safeWorkerCount, null, Collections.emptyMap());
        return OrderSplitPlan.builder()
                .matched(false)
                .splitRequired(false)
                .splitMode("DIRECT")
                .unitAmountLimit(null)
                .unitCount(1)
                .ruleSnapshot(null)
                .units(units)
                .build();
    }

    private List<OrderSplitPlan.OrderSplitUnitPlan> buildUnits(OrderSplitPreviewContext context, List<BigDecimal> amounts,
                                                               String splitMode, int safeWorkerCount, BigDecimal limit,
                                                               Map<String, String> template) {
        List<OrderSplitPlan.OrderSplitUnitPlan> units = new ArrayList<>();
        for (int i = 0; i < amounts.size(); i++) {
            boolean locked = i > 0;
            units.add(OrderSplitPlan.OrderSplitUnitPlan.builder()
                    .unitSeq(i + 1)
                    .unitTitle(buildUnitTitle(context.getRequireDesc(), i + 1, template))
                    .unitType(amounts.size() > 1 ? "AUTO_SPLIT" : "DIRECT")
                    .unitContent(buildUnitContent(splitMode, i + 1, template))
                    .unitProgress(amounts.size() > 1 ? (i + 1) + "/" + amounts.size() : "1/1")
                    .unitAmount(amounts.get(i))
                    .workerCount("BY_PERSON".equalsIgnoreCase(splitMode) ? 1 : safeWorkerCount)
                    .maxAmountLimit(limit)
                    .locked(locked)
                    .lockReason(locked ? resolveLockReason(template) : null)
                    .initStatus(locked ? "PENDING_CREATE" : "PENDING_ACCEPT")
                    .build());
        }
        return units;
    }

    private String buildUnitTitle(String requireDesc, int seq, Map<String, String> template) {
        String templatePrefix = template.get("titlePrefix");
        if (StrUtil.isNotBlank(templatePrefix)) {
            return templatePrefix + "-" + seq;
        }
        String prefix = StrUtil.blankToDefault(StrUtil.trim(requireDesc), "订单单元");
        if (prefix.length() > 48) {
            prefix = prefix.substring(0, 48);
        }
        return prefix + "-" + seq;
    }

    private String buildUnitContent(String splitMode, int seq, Map<String, String> template) {
        String contentTemplate = template.get("contentTemplate");
        if (StrUtil.isNotBlank(contentTemplate)) {
            return contentTemplate.replace("{seq}", String.valueOf(seq))
                    .replace("{splitMode}", StrUtil.blankToDefault(splitMode, "DIRECT"));
        }
        if ("BY_PROGRESS".equalsIgnoreCase(splitMode)) {
            return "进度单元" + seq;
        }
        if ("BY_PROCESS".equalsIgnoreCase(splitMode)) {
            return "工序单元" + seq;
        }
        if ("BY_CONTENT".equalsIgnoreCase(splitMode)) {
            return "内容单元" + seq;
        }
        if ("BY_PERSON".equalsIgnoreCase(splitMode)) {
            return "人员单元" + seq;
        }
        return "订单单元" + seq;
    }

    private List<BigDecimal> splitAmounts(BigDecimal orderAmount, int unitCount, BigDecimal limit) {
        if (unitCount <= 0) {
            throw exception(ORDER_SPLIT_PLAN_GENERATE_FAILED);
        }
        if (unitCount == 1) {
            if (limit != null && orderAmount.compareTo(limit) > 0) {
                throw exception(ORDER_UNIT_AMOUNT_EXCEED_LIMIT);
            }
            return Collections.singletonList(orderAmount);
        }
        List<BigDecimal> results = new ArrayList<>();
        BigDecimal remaining = orderAmount;
        for (int i = 0; i < unitCount; i++) {
            int left = unitCount - i;
            BigDecimal amount = remaining.divide(BigDecimal.valueOf(left), 2, RoundingMode.DOWN);
            if (i == unitCount - 1) {
                amount = remaining;
            }
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw exception(ORDER_SPLIT_PLAN_GENERATE_FAILED);
            }
            if (limit != null && amount.compareTo(limit) > 0) {
                throw exception(ORDER_UNIT_AMOUNT_EXCEED_LIMIT);
            }
            results.add(amount);
            remaining = remaining.subtract(amount);
        }
        return results;
    }

    private int resolveUnitCount(OrderSplitRuleDO rule, OrderSplitPreviewContext context,
                                 BigDecimal orderAmount, int safeWorkerCount) {
        BigDecimal limit = OptionalValue.of(rule.getUnitAmountLimit(), DEFAULT_LIMIT);
        int amountCount = limit.compareTo(BigDecimal.ZERO) > 0
                ? orderAmount.divide(limit, 0, RoundingMode.UP).intValue()
                : 1;
        int quantityCount = 1;
        if (rule.getMinQuantity() != null && rule.getMinQuantity().compareTo(BigDecimal.ZERO) > 0
                && context.getQuantity() != null && context.getQuantity().compareTo(rule.getMinQuantity()) >= 0) {
            quantityCount = context.getQuantity().divide(rule.getMinQuantity(), 0, RoundingMode.UP).intValue();
        }
        int workerCount = "BY_PERSON".equalsIgnoreCase(rule.getSplitMode())
                ? safeWorkerCount : 1;
        return Math.max(Math.max(amountCount, quantityCount), Math.max(workerCount, OptionalValue.of(rule.getDefaultUnitCount(), 1)));
    }

    private boolean matchesRule(OrderSplitRuleDO rule, OrderSplitPreviewContext context, int safeWorkerCount) {
        if (!matchesPricingMode(rule.getApplicablePricingModes(), context.getPricingMode())) {
            return false;
        }
        List<Boolean> conditions = new ArrayList<>();
        if (rule.getMinOrderAmount() != null) {
            conditions.add(context.getOrderAmount() != null && context.getOrderAmount().compareTo(rule.getMinOrderAmount()) >= 0);
        }
        if (rule.getMinQuantity() != null) {
            conditions.add(context.getQuantity() != null && context.getQuantity().compareTo(rule.getMinQuantity()) >= 0);
        }
        if (rule.getMinWorkerCount() != null) {
            conditions.add(safeWorkerCount >= rule.getMinWorkerCount());
        }
        if (rule.getCategoryId() != null) {
            conditions.add(matchesCategory(rule.getCategoryId(), context.getCategoryId()));
        }
        if (conditions.isEmpty()) {
            return false;
        }
        if ("ALL".equalsIgnoreCase(rule.getMatchMode())) {
            return conditions.stream().allMatch(Boolean.TRUE::equals);
        }
        return conditions.stream().anyMatch(Boolean.TRUE::equals);
    }

    private boolean matchesCategory(Long ruleCategoryId, Long orderCategoryId) {
        if (ruleCategoryId == null || orderCategoryId == null) {
            return false;
        }
        if (Objects.equals(ruleCategoryId, orderCategoryId)) {
            return true;
        }
        return resolveCategoryAndDescendantIds(ruleCategoryId).contains(orderCategoryId);
    }

    private Set<Long> resolveCategoryAndDescendantIds(Long categoryId) {
        if (categoryId == null) {
            return Collections.emptySet();
        }
        List<MerchantServiceCategoryDO> categories = merchantServiceCategoryMapper.selectList();
        if (CollUtil.isEmpty(categories)) {
            return Collections.singleton(categoryId);
        }
        Map<Long, List<MerchantServiceCategoryDO>> childrenMap = categories.stream()
                .filter(item -> item.getParentId() != null)
                .collect(Collectors.groupingBy(MerchantServiceCategoryDO::getParentId));
        Set<Long> results = new LinkedHashSet<>();
        collectCategoryIds(categoryId, childrenMap, results);
        return results;
    }

    private void collectCategoryIds(Long categoryId, Map<Long, List<MerchantServiceCategoryDO>> childrenMap, Set<Long> results) {
        if (categoryId == null || !results.add(categoryId)) {
            return;
        }
        for (MerchantServiceCategoryDO child : childrenMap.getOrDefault(categoryId, Collections.emptyList())) {
            collectCategoryIds(child.getId(), childrenMap, results);
        }
    }

    private OrderSplitRuleDO validateOrderSplitRuleExists(Long id) {
        OrderSplitRuleDO rule = orderSplitRuleMapper.selectById(id);
        if (rule == null) {
            throw exception(ORDER_SPLIT_RULE_NOT_EXISTS);
        }
        return rule;
    }

    private OrderSplitRuleRespVO toRespVO(OrderSplitRuleDO rule, String categoryName) {
        OrderSplitRuleRespVO respVO = new OrderSplitRuleRespVO();
        respVO.setId(rule.getId());
        respVO.setRuleName(rule.getRuleName());
        respVO.setRuleCode(rule.getRuleCode());
        respVO.setMatchMode(rule.getMatchMode());
        respVO.setCategoryId(rule.getCategoryId());
        respVO.setCategoryName(categoryName);
        respVO.setMinOrderAmount(rule.getMinOrderAmount());
        respVO.setMinQuantity(rule.getMinQuantity());
        respVO.setMinWorkerCount(rule.getMinWorkerCount());
        respVO.setSplitMode(rule.getSplitMode());
        respVO.setDefaultUnitCount(rule.getDefaultUnitCount());
        respVO.setUnitAmountLimit(rule.getUnitAmountLimit());
        respVO.setSortNo(rule.getSortNo());
        respVO.setStatus(rule.getStatus());
        respVO.setRemark(rule.getRemark());
        respVO.setCreateTime(rule.getCreateTime());
        respVO.setApplicablePricingModes(parsePricingModes(rule.getApplicablePricingModes()));
        respVO.setUnitTemplate(parseUnitTemplate(rule.getUnitTemplate()));
        return respVO;
    }

    private Map<Long, String> buildCategoryNameMap(List<OrderSplitRuleDO> rules) {
        Set<Long> categoryIds = rules.stream().map(OrderSplitRuleDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return merchantServiceCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(MerchantServiceCategoryDO::getId, MerchantServiceCategoryDO::getCategoryName));
    }

    private String loadCategoryName(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(categoryId);
        return category != null ? category.getCategoryName() : null;
    }

    private OrderSplitRuleDO buildRule(OrderSplitRuleSaveReqVO reqVO) {
        OrderSplitRuleDO rule = new OrderSplitRuleDO();
        rule.setId(reqVO.getId());
        rule.setRuleName(reqVO.getRuleName());
        rule.setRuleCode(reqVO.getRuleCode());
        rule.setMatchMode(reqVO.getMatchMode());
        rule.setCategoryId(reqVO.getCategoryId());
        rule.setMinOrderAmount(reqVO.getMinOrderAmount());
        rule.setMinQuantity(reqVO.getMinQuantity());
        rule.setMinWorkerCount(reqVO.getMinWorkerCount());
        rule.setSplitMode(reqVO.getSplitMode());
        rule.setDefaultUnitCount(reqVO.getDefaultUnitCount());
        rule.setUnitAmountLimit(reqVO.getUnitAmountLimit());
        rule.setSortNo(reqVO.getSortNo());
        rule.setStatus(reqVO.getStatus());
        rule.setRemark(reqVO.getRemark());
        rule.setApplicablePricingModes(JsonUtils.toJsonString(reqVO.getApplicablePricingModes()));
        rule.setUnitTemplate(JsonUtils.toJsonString(reqVO.getUnitTemplate()));
        return rule;
    }

    private List<String> parsePricingModes(String json) {
        if (StrUtil.isBlank(json)) {
            return Collections.emptyList();
        }
        List<String> pricingModes = JsonUtils.parseArray(json, String.class);
        return CollUtil.isEmpty(pricingModes) ? Collections.emptyList() : pricingModes;
    }

    private Map<String, String> parseUnitTemplate(String json) {
        if (StrUtil.isBlank(json)) {
            return Collections.emptyMap();
        }
        Map<String, String> template = JsonUtils.parseObject(json, Map.class);
        return template == null ? Collections.emptyMap() : template;
    }

    private boolean matchesPricingMode(String applicablePricingModesJson, String pricingMode) {
        List<String> pricingModes = parsePricingModes(applicablePricingModesJson);
        if (pricingModes.isEmpty()) {
            return true;
        }
        return StrUtil.isNotBlank(pricingMode) && pricingModes.contains(pricingMode);
    }

    private String resolveLockReason(Map<String, String> template) {
        return StrUtil.blankToDefault(template.get("lockReasonTemplate"), "待前序单元完成");
    }

    private static final class OptionalValue {
        private OptionalValue() {
        }

        private static <T> T of(T value, T defaultValue) {
            return value != null ? value : defaultValue;
        }
    }

}
