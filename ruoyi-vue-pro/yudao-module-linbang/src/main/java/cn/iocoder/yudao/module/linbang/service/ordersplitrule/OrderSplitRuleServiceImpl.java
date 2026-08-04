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
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordersplitrule.OrderSplitRuleDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.ordersplitrule.OrderSplitRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Locale;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_SPLIT_PLAN_GENERATE_FAILED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_SPLIT_RULE_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_SPLIT_RULE_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_SPLIT_RULE_IN_USE;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_UNIT_AMOUNT_EXCEED_LIMIT;

@Service
@Validated
public class OrderSplitRuleServiceImpl implements OrderSplitRuleService {

    private static final BigDecimal DEFAULT_LIMIT = new BigDecimal("200.00");
    private static final int MAX_GENERATED_UNIT_COUNT = 100;
    private static final String GLOBAL_AMOUNT_RULE_CODE = "GLOBAL_AMOUNT_GE_200";
    private static final String GLOBAL_AMOUNT_RULE_NAME = "平台金额满 200 自动拆单";
    private static final String GLOBAL_AMOUNT_RULE_SUMMARY = "平台硬性规则：订单金额满 200 元后自动拆分";
    private static final Set<String> MATCH_MODES = new LinkedHashSet<>(Arrays.asList("ANY", "ALL"));
    private static final Set<String> SPLIT_MODES = new LinkedHashSet<>(
            Arrays.asList("DIRECT", "BY_PROGRESS", "BY_PROCESS", "BY_CONTENT", "BY_PERSON"));
    private static final Set<String> RULE_STATUSES = new LinkedHashSet<>(Arrays.asList("ENABLE", "DISABLE"));
    private static final Set<String> PRICING_MODES = new LinkedHashSet<>(
            Arrays.asList("FIXED_PRICE", "CONTRACT", "OUTSOURCING", "HOURLY", "BY_UNIT"));
    private static final Set<String> UNIT_TEMPLATE_KEYS = new LinkedHashSet<>(
            Arrays.asList("titlePrefix", "contentTemplate", "lockReasonTemplate"));

    @Resource
    private OrderSplitRuleMapper orderSplitRuleMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Override
    public Long createOrderSplitRule(@Valid OrderSplitRuleSaveReqVO createReqVO) {
        validateRuleReq(createReqVO);
        OrderSplitRuleDO rule = buildRule(createReqVO);
        orderSplitRuleMapper.insert(rule);
        return rule.getId();
    }

    @Override
    public void updateOrderSplitRule(@Valid OrderSplitRuleSaveReqVO updateReqVO) {
        validateOrderSplitRuleExists(updateReqVO.getId());
        validateRuleReq(updateReqVO);
        orderSplitRuleMapper.updateById(buildRule(updateReqVO));
    }

    @Override
    public void deleteOrderSplitRule(Long id) {
        validateOrderSplitRuleExists(id);
        if (orderInfoMapper.selectCount(OrderInfoDO::getSplitRuleId, id) > 0) {
            throw exception(ORDER_SPLIT_RULE_IN_USE);
        }
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
        int mandatoryAmountUnitCount = resolveMandatoryAmountSplitUnitCount(orderAmount);
        List<OrderSplitRuleDO> rules = orderSplitRuleMapper.selectList(new LambdaQueryWrapperX<OrderSplitRuleDO>()
                .eq(OrderSplitRuleDO::getStatus, "ENABLE")
                .orderByAsc(OrderSplitRuleDO::getSortNo, OrderSplitRuleDO::getId));
        Map<Long, List<MerchantServiceCategoryDO>> categoryChildrenMap = buildCategoryChildrenMap(rules);
        for (OrderSplitRuleDO rule : rules) {
            if (!matchesRule(rule, context, safeWorkerCount, categoryChildrenMap)) {
                continue;
            }
            return buildRulePlan(rule, context, orderAmount, safeWorkerCount, mandatoryAmountUnitCount);
        }
        if (mandatoryAmountUnitCount > 1) {
            return buildGlobalAmountMandatoryPlan(context, orderAmount, safeWorkerCount, mandatoryAmountUnitCount);
        }
        return buildDirectPlan(context, orderAmount, safeWorkerCount);
    }

    private OrderSplitPlan buildRulePlan(OrderSplitRuleDO rule, OrderSplitPreviewContext context,
                                         BigDecimal orderAmount, int safeWorkerCount, int mandatoryAmountUnitCount) {
        String resolvedSplitMode = resolveSplitMode(rule, context);
        String effectiveSplitMode = mandatoryAmountUnitCount > 1 && "DIRECT".equalsIgnoreCase(resolvedSplitMode)
                ? resolveGlobalAmountSplitMode(context)
                : resolvedSplitMode;
        List<String> triggerReasons = buildTriggerReasons(rule, context, orderAmount, safeWorkerCount);
        appendMandatoryAmountTriggerReason(triggerReasons, orderAmount);
        int suggestedUnitCount = resolveUnitCount(rule, context, orderAmount, safeWorkerCount);
        if (suggestedUnitCount <= 1) {
            suggestedUnitCount = Math.max(OptionalValue.of(rule.getDefaultUnitCount(), 2), 2);
        }
        suggestedUnitCount = Math.max(suggestedUnitCount, mandatoryAmountUnitCount);
        suggestedUnitCount = validateGeneratedUnitCount(suggestedUnitCount);
        String ruleSummary = buildRuleSummary(triggerReasons, effectiveSplitMode, suggestedUnitCount);
        if (!Boolean.TRUE.equals(context.getAutoSplitEnabled()) && mandatoryAmountUnitCount <= 1) {
            List<OrderSplitPlan.OrderSplitUnitPlan> units = buildUnits(context,
                    Collections.singletonList(orderAmount), "DIRECT", safeWorkerCount, null, Collections.emptyMap());
            return OrderSplitPlan.builder()
                    .matched(true)
                    .splitRequired(false)
                    .ruleId(rule.getId())
                    .ruleName(rule.getRuleName())
                    .ruleCode(rule.getRuleCode())
                    .matchMode(rule.getMatchMode())
                    .splitMode(effectiveSplitMode)
                    .unitAmountLimit(OptionalValue.of(rule.getUnitAmountLimit(), DEFAULT_LIMIT))
                    .unitCount(1)
                    .quantityUnitLabel(context.getQuantityUnitLabel())
                    .quantitySplitEnabled(context.getQuantitySplitEnabled())
                    .splitTriggerReasons(triggerReasons)
                    .splitRuleSummary(ruleSummary + "；当前订单未开启自动拆单，因此本次仅提示不自动拆分。")
                    .ruleSnapshot(null)
                    .units(units)
                    .build();
        }
        if ("DIRECT".equalsIgnoreCase(effectiveSplitMode)) {
            return buildDirectPlan(context, orderAmount, safeWorkerCount, true, triggerReasons, ruleSummary);
        }
        int unitCount = suggestedUnitCount;
        BigDecimal limit = OptionalValue.of(rule.getUnitAmountLimit(), DEFAULT_LIMIT);
        List<BigDecimal> amounts = splitAmounts(orderAmount, unitCount, limit);
        List<OrderSplitPlan.OrderSplitUnitPlan> units = buildUnits(context, amounts, effectiveSplitMode,
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
        snapshot.put("splitMode", effectiveSplitMode);
        snapshot.put("defaultUnitCount", rule.getDefaultUnitCount());
        snapshot.put("unitAmountLimit", limit);
        snapshot.put("unitTemplate", parseUnitTemplate(rule.getUnitTemplate()));
        snapshot.put("quantityUnitLabel", context.getQuantityUnitLabel());
        snapshot.put("quantitySplitEnabled", context.getQuantitySplitEnabled());
        snapshot.put("engineeringCategoryFlag", context.getEngineeringCategoryFlag());
        snapshot.put("splitTriggerReasons", triggerReasons);
        snapshot.put("splitRuleSummary", ruleSummary);
        snapshot.put("generatedUnitCount", unitCount);
        return OrderSplitPlan.builder()
                .matched(true)
                .splitRequired(unitCount > 1)
                .ruleId(rule.getId())
                .ruleName(rule.getRuleName())
                .ruleCode(rule.getRuleCode())
                .matchMode(rule.getMatchMode())
                .splitMode(effectiveSplitMode)
                .unitAmountLimit(limit)
                .unitCount(unitCount)
                .quantityUnitLabel(context.getQuantityUnitLabel())
                .quantitySplitEnabled(context.getQuantitySplitEnabled())
                .splitTriggerReasons(triggerReasons)
                .splitRuleSummary(ruleSummary)
                .ruleSnapshot(JsonUtils.toJsonString(snapshot))
                .units(units)
                .build();
    }

    private OrderSplitPlan buildGlobalAmountMandatoryPlan(OrderSplitPreviewContext context, BigDecimal orderAmount,
                                                          int safeWorkerCount, int mandatoryAmountUnitCount) {
        String splitMode = resolveGlobalAmountSplitMode(context);
        List<String> triggerReasons = new ArrayList<>();
        appendMandatoryAmountTriggerReason(triggerReasons, orderAmount);
        String ruleSummary = GLOBAL_AMOUNT_RULE_SUMMARY + "；预计生成 " + mandatoryAmountUnitCount + " 个单元。";
        List<BigDecimal> amounts = splitAmounts(orderAmount, mandatoryAmountUnitCount, DEFAULT_LIMIT);
        List<OrderSplitPlan.OrderSplitUnitPlan> units = buildUnits(context, amounts, splitMode,
                safeWorkerCount, DEFAULT_LIMIT, Collections.emptyMap());
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("ruleCode", GLOBAL_AMOUNT_RULE_CODE);
        snapshot.put("ruleName", GLOBAL_AMOUNT_RULE_NAME);
        snapshot.put("minOrderAmount", DEFAULT_LIMIT);
        snapshot.put("unitAmountLimit", DEFAULT_LIMIT);
        snapshot.put("splitMode", splitMode);
        snapshot.put("generatedUnitCount", mandatoryAmountUnitCount);
        snapshot.put("splitTriggerReasons", triggerReasons);
        snapshot.put("splitRuleSummary", ruleSummary);
        return OrderSplitPlan.builder()
                .matched(true)
                .splitRequired(true)
                .ruleId(null)
                .ruleName(GLOBAL_AMOUNT_RULE_NAME)
                .ruleCode(GLOBAL_AMOUNT_RULE_CODE)
                .matchMode("ANY")
                .splitMode(splitMode)
                .unitAmountLimit(DEFAULT_LIMIT)
                .unitCount(mandatoryAmountUnitCount)
                .quantityUnitLabel(context.getQuantityUnitLabel())
                .quantitySplitEnabled(context.getQuantitySplitEnabled())
                .splitTriggerReasons(triggerReasons)
                .splitRuleSummary(ruleSummary)
                .ruleSnapshot(JsonUtils.toJsonString(snapshot))
                .units(units)
                .build();
    }

    private OrderSplitPlan buildDirectPlan(OrderSplitPreviewContext context, BigDecimal orderAmount, int safeWorkerCount) {
        return buildDirectPlan(context, orderAmount, safeWorkerCount, false, Collections.emptyList(),
                "当前订单按直单处理，不生成拆分单元。");
    }

    private OrderSplitPlan buildDirectPlan(OrderSplitPreviewContext context, BigDecimal orderAmount, int safeWorkerCount,
                                           boolean matched, List<String> triggerReasons, String ruleSummary) {
        List<OrderSplitPlan.OrderSplitUnitPlan> units = buildUnits(context,
                Collections.singletonList(orderAmount), "DIRECT", safeWorkerCount, null, Collections.emptyMap());
        return OrderSplitPlan.builder()
                .matched(matched)
                .splitRequired(false)
                .splitMode("DIRECT")
                .unitAmountLimit(null)
                .unitCount(1)
                .quantityUnitLabel(context.getQuantityUnitLabel())
                .quantitySplitEnabled(context.getQuantitySplitEnabled())
                .splitTriggerReasons(triggerReasons)
                .splitRuleSummary(ruleSummary)
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
        if (unitCount <= 0 || unitCount > MAX_GENERATED_UNIT_COUNT) {
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
                ? toGeneratedUnitCount(orderAmount.divide(limit, 0, RoundingMode.UP))
                : 1;
        int quantityCount = 1;
        if (Boolean.TRUE.equals(context.getQuantitySplitEnabled())
                && rule.getMinQuantity() != null && rule.getMinQuantity().compareTo(BigDecimal.ZERO) > 0
                && context.getQuantity() != null && context.getQuantity().compareTo(rule.getMinQuantity()) >= 0) {
            quantityCount = toGeneratedUnitCount(
                    context.getQuantity().divide(rule.getMinQuantity(), 0, RoundingMode.UP));
        }
        int workerCount = "BY_PERSON".equalsIgnoreCase(rule.getSplitMode())
                ? safeWorkerCount : 1;
        int defaultUnitCount = OptionalValue.of(rule.getDefaultUnitCount(), 1);
        if (!Boolean.TRUE.equals(context.getEngineeringCategoryFlag())) {
            defaultUnitCount = Math.min(defaultUnitCount, 1);
        }
        return validateGeneratedUnitCount(
                Math.max(Math.max(amountCount, quantityCount), Math.max(workerCount, defaultUnitCount)));
    }

    private int resolveMandatoryAmountSplitUnitCount(BigDecimal orderAmount) {
        if (orderAmount == null || orderAmount.compareTo(DEFAULT_LIMIT) < 0) {
            return 1;
        }
        int amountCount = toGeneratedUnitCount(orderAmount.divide(DEFAULT_LIMIT, 0, RoundingMode.UP));
        return Math.max(amountCount, 2);
    }

    private int toGeneratedUnitCount(BigDecimal calculatedCount) {
        if (calculatedCount == null || calculatedCount.compareTo(BigDecimal.ONE) < 0
                || calculatedCount.compareTo(BigDecimal.valueOf(MAX_GENERATED_UNIT_COUNT)) > 0) {
            throw exception(ORDER_SPLIT_PLAN_GENERATE_FAILED);
        }
        try {
            return calculatedCount.intValueExact();
        } catch (ArithmeticException ex) {
            throw exception(ORDER_SPLIT_PLAN_GENERATE_FAILED);
        }
    }

    private int validateGeneratedUnitCount(int unitCount) {
        if (unitCount <= 0 || unitCount > MAX_GENERATED_UNIT_COUNT) {
            throw exception(ORDER_SPLIT_PLAN_GENERATE_FAILED);
        }
        return unitCount;
    }

    private boolean matchesRule(OrderSplitRuleDO rule, OrderSplitPreviewContext context, int safeWorkerCount,
                                Map<Long, List<MerchantServiceCategoryDO>> categoryChildrenMap) {
        if (!matchesPricingMode(rule.getApplicablePricingModes(), context.getPricingMode())) {
            return false;
        }
        List<Boolean> conditions = new ArrayList<>();
        if (rule.getMinOrderAmount() != null) {
            conditions.add(context.getOrderAmount() != null && context.getOrderAmount().compareTo(rule.getMinOrderAmount()) >= 0);
        }
        if (rule.getMinQuantity() != null && Boolean.TRUE.equals(context.getQuantitySplitEnabled())) {
            conditions.add(context.getQuantity() != null && context.getQuantity().compareTo(rule.getMinQuantity()) >= 0);
        }
        if (rule.getMinWorkerCount() != null) {
            conditions.add(safeWorkerCount >= rule.getMinWorkerCount());
        }
        if (rule.getCategoryId() != null) {
            conditions.add(matchesCategory(rule.getCategoryId(), context.getCategoryId(), categoryChildrenMap));
        }
        if (conditions.isEmpty()) {
            return false;
        }
        if ("ALL".equalsIgnoreCase(rule.getMatchMode())) {
            return conditions.stream().allMatch(Boolean.TRUE::equals);
        }
        return conditions.stream().anyMatch(Boolean.TRUE::equals);
    }

    private String resolveSplitMode(OrderSplitRuleDO rule, OrderSplitPreviewContext context) {
        if (StrUtil.isNotBlank(rule.getSplitMode()) && !"DIRECT".equalsIgnoreCase(rule.getSplitMode())) {
            return rule.getSplitMode();
        }
        if (StrUtil.isNotBlank(context.getSplitDefaultMode())) {
            return context.getSplitDefaultMode();
        }
        return StrUtil.blankToDefault(rule.getSplitMode(), "DIRECT");
    }

    private List<String> buildTriggerReasons(OrderSplitRuleDO rule, OrderSplitPreviewContext context,
                                             BigDecimal orderAmount, int safeWorkerCount) {
        List<String> reasons = new ArrayList<>();
        if (rule.getMinOrderAmount() != null && orderAmount != null
                && orderAmount.compareTo(rule.getMinOrderAmount()) >= 0) {
            reasons.add("金额达到阈值 " + rule.getMinOrderAmount() + " 元");
        }
        if (Boolean.TRUE.equals(context.getQuantitySplitEnabled())
                && rule.getMinQuantity() != null && context.getQuantity() != null
                && context.getQuantity().compareTo(rule.getMinQuantity()) >= 0) {
            String unit = StrUtil.blankToDefault(context.getQuantityUnitLabel(), "单位");
            reasons.add("数量达到阈值 " + rule.getMinQuantity() + unit);
        }
        if (rule.getMinWorkerCount() != null && safeWorkerCount >= rule.getMinWorkerCount()) {
            reasons.add("服务人数达到阈值 " + rule.getMinWorkerCount() + " 人");
        }
        if (Boolean.TRUE.equals(context.getEngineeringCategoryFlag())
                && OptionalValue.of(rule.getDefaultUnitCount(), 0) > 1) {
            reasons.add("工程类按默认单元数兜底拆分");
        }
        if (reasons.isEmpty() && rule.getCategoryId() != null && context.getCategoryId() != null) {
            reasons.add("命中类目拆单规则");
        }
        return reasons;
    }

    private String buildRuleSummary(List<String> triggerReasons, String splitMode, int suggestedUnitCount) {
        String reasons = CollUtil.isEmpty(triggerReasons) ? "命中拆单规则" : String.join("；", triggerReasons);
        return reasons + "；拆分方式为 " + splitMode + "；预计生成 " + suggestedUnitCount + " 个单元。";
    }

    private void appendMandatoryAmountTriggerReason(List<String> triggerReasons, BigDecimal orderAmount) {
        if (orderAmount == null || orderAmount.compareTo(DEFAULT_LIMIT) < 0) {
            return;
        }
        String reason = "金额达到平台硬性拆单阈值 200 元";
        if (!triggerReasons.contains(reason)) {
            triggerReasons.add(0, reason);
        }
    }

    private String resolveGlobalAmountSplitMode(OrderSplitPreviewContext context) {
        if (StrUtil.isNotBlank(context.getSplitDefaultMode()) && !"DIRECT".equalsIgnoreCase(context.getSplitDefaultMode())) {
            return context.getSplitDefaultMode();
        }
        return "BY_CONTENT";
    }

    private boolean matchesCategory(Long ruleCategoryId, Long orderCategoryId,
                                    Map<Long, List<MerchantServiceCategoryDO>> categoryChildrenMap) {
        if (ruleCategoryId == null || orderCategoryId == null) {
            return false;
        }
        if (Objects.equals(ruleCategoryId, orderCategoryId)) {
            return true;
        }
        Set<Long> categoryIds = new LinkedHashSet<>();
        collectCategoryIds(ruleCategoryId, categoryChildrenMap, categoryIds);
        return categoryIds.contains(orderCategoryId);
    }

    private Map<Long, List<MerchantServiceCategoryDO>> buildCategoryChildrenMap(List<OrderSplitRuleDO> rules) {
        if (rules.stream().noneMatch(rule -> rule.getCategoryId() != null)) {
            return Collections.emptyMap();
        }
        List<MerchantServiceCategoryDO> categories = merchantServiceCategoryMapper.selectList();
        if (CollUtil.isEmpty(categories)) {
            return Collections.emptyMap();
        }
        return categories.stream()
                .filter(item -> item.getParentId() != null)
                .collect(Collectors.groupingBy(MerchantServiceCategoryDO::getParentId));
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
        rule.setRuleCode(normalize(reqVO.getRuleCode()));
        rule.setMatchMode(normalize(reqVO.getMatchMode()));
        rule.setCategoryId(reqVO.getCategoryId());
        rule.setMinOrderAmount(reqVO.getMinOrderAmount());
        rule.setMinQuantity(reqVO.getMinQuantity());
        rule.setMinWorkerCount(reqVO.getMinWorkerCount());
        rule.setSplitMode(normalize(reqVO.getSplitMode()));
        rule.setDefaultUnitCount(reqVO.getDefaultUnitCount());
        rule.setUnitAmountLimit(reqVO.getUnitAmountLimit());
        rule.setSortNo(reqVO.getSortNo());
        rule.setStatus(normalize(reqVO.getStatus()));
        rule.setRemark(reqVO.getRemark());
        List<String> pricingModes = reqVO.getApplicablePricingModes() == null ? null
                : reqVO.getApplicablePricingModes().stream().map(this::normalize).distinct().collect(Collectors.toList());
        rule.setApplicablePricingModes(JsonUtils.toJsonString(pricingModes));
        rule.setUnitTemplate(JsonUtils.toJsonString(reqVO.getUnitTemplate()));
        return rule;
    }

    private void validateRuleReq(OrderSplitRuleSaveReqVO reqVO) {
        String ruleCode = normalize(reqVO.getRuleCode());
        LambdaQueryWrapperX<OrderSplitRuleDO> duplicateQuery = new LambdaQueryWrapperX<OrderSplitRuleDO>()
                .eq(OrderSplitRuleDO::getRuleCode, ruleCode);
        if (reqVO.getId() != null) {
            duplicateQuery.ne(OrderSplitRuleDO::getId, reqVO.getId());
        }
        if (orderSplitRuleMapper.selectCount(duplicateQuery) > 0) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "规则编码已存在");
        }
        if (!MATCH_MODES.contains(normalize(reqVO.getMatchMode()))) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "命中模式仅支持 ANY 或 ALL");
        }
        if (!SPLIT_MODES.contains(normalize(reqVO.getSplitMode()))) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "拆分方式不受支持");
        }
        if (!RULE_STATUSES.contains(normalize(reqVO.getStatus()))) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "状态仅支持 ENABLE 或 DISABLE");
        }
        if (reqVO.getMinOrderAmount() != null && reqVO.getMinOrderAmount().compareTo(new BigDecimal("100000000")) > 0) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "触发金额超过允许上限");
        }
        if (reqVO.getMinQuantity() != null && reqVO.getMinQuantity().compareTo(new BigDecimal("1000000")) > 0) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "触发数量超过允许上限");
        }
        if (reqVO.getUnitAmountLimit() == null || reqVO.getUnitAmountLimit().compareTo(BigDecimal.ZERO) <= 0
                || reqVO.getUnitAmountLimit().compareTo(new BigDecimal("100000000")) > 0) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "单元金额上限必须在 0 到 100000000 元之间");
        }
        if (reqVO.getCategoryId() != null) {
            MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(reqVO.getCategoryId());
            if (category == null || !"ENABLE".equalsIgnoreCase(category.getStatus())) {
                throw exception(ORDER_SPLIT_RULE_INVALID, "服务类目不存在或未启用");
            }
        }
        if (reqVO.getMinOrderAmount() == null && reqVO.getMinQuantity() == null
                && reqVO.getMinWorkerCount() == null && reqVO.getCategoryId() == null) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "至少配置一个金额、数量、人数或类目触发条件");
        }
        if (reqVO.getApplicablePricingModes() != null) {
            for (String pricingMode : reqVO.getApplicablePricingModes()) {
                if (!PRICING_MODES.contains(normalize(pricingMode))) {
                    throw exception(ORDER_SPLIT_RULE_INVALID, "存在不受支持的计价方式");
                }
            }
        }
        validateUnitTemplate(reqVO.getUnitTemplate());
    }

    private void validateUnitTemplate(Map<String, String> template) {
        if (template == null) {
            return;
        }
        if (template.size() > UNIT_TEMPLATE_KEYS.size() || !UNIT_TEMPLATE_KEYS.containsAll(template.keySet())) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "单元模板包含不受支持的键");
        }
        int totalLength = 0;
        for (String value : template.values()) {
            if (value == null || value.length() > 500) {
                throw exception(ORDER_SPLIT_RULE_INVALID, "单元模板值不能为空且不能超过 500 个字符");
            }
            totalLength += value.length();
        }
        if (totalLength > 1000) {
            throw exception(ORDER_SPLIT_RULE_INVALID, "单元模板总长度不能超过 1000 个字符");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
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
