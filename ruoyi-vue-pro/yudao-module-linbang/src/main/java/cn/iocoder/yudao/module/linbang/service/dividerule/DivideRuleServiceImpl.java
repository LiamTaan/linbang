package cn.iocoder.yudao.module.linbang.service.dividerule;

import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderdividerecord.OrderDivideRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderdividerecord.OrderDivideRecordMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.dividerule.DivideRuleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.linbang.dal.mysql.dividerule.DivideRuleMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 分账规则 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class DivideRuleServiceImpl implements DivideRuleService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    @Resource
    private DivideRuleMapper divideRuleMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private OrderDivideRecordMapper orderDivideRecordMapper;

    @Override
    public Long createDivideRule(DivideRuleSaveReqVO createReqVO) {
        validateAndNormalize(createReqVO);
        // 插入
        DivideRuleDO divideRule = BeanUtils.toBean(createReqVO, DivideRuleDO.class);
        divideRuleMapper.insert(divideRule);

        // 返回
        return divideRule.getId();
    }

    @Override
    public void updateDivideRule(DivideRuleSaveReqVO updateReqVO) {
        // 校验存在
        validateDivideRuleExists(updateReqVO.getId());
        validateAndNormalize(updateReqVO);
        // 更新
        DivideRuleDO updateObj = BeanUtils.toBean(updateReqVO, DivideRuleDO.class);
        divideRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteDivideRule(Long id) {
        // 校验存在
        validateDivideRuleExists(id);
        validateDivideRuleNotInUse(id);
        // 删除
        divideRuleMapper.deleteById(id);
    }

    @Override
    public void deleteDivideRuleListByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        ids.forEach(this::validateDivideRuleExists);
        ids.forEach(this::validateDivideRuleNotInUse);
        divideRuleMapper.deleteByIds(ids);
        }


    private void validateDivideRuleExists(Long id) {
        if (divideRuleMapper.selectById(id) == null) {
            throw exception(DIVIDE_RULE_NOT_EXISTS);
        }
    }

    private void validateAndNormalize(DivideRuleSaveReqVO reqVO) {
        reqVO.setRuleName(reqVO.getRuleName().trim());
        reqVO.setCityLevel(reqVO.getCityLevel().trim().toUpperCase(Locale.ROOT));
        reqVO.setStatus(reqVO.getStatus().trim().toUpperCase(Locale.ROOT));
        if (!Arrays.asList("ENABLE", "DISABLE").contains(reqVO.getStatus())) {
            throw exception(DIVIDE_RULE_INVALID, "状态仅支持 ENABLE 或 DISABLE");
        }
        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(reqVO.getCategoryId());
        if (category == null || !"ENABLE".equals(category.getStatus())) {
            throw exception(DIVIDE_RULE_INVALID, "服务类目不存在或未启用");
        }
        BigDecimal primaryTotal = reqVO.getMerchantRate().add(reqVO.getPlatformRate())
                .add(reqVO.getPartnerRate()).add(reqVO.getPromoterRate());
        if (primaryTotal.compareTo(ONE_HUNDRED) != 0) {
            throw exception(DIVIDE_RULE_INVALID, "服务商、平台、合作商和推广员比例之和必须等于 100%");
        }
        if (reqVO.getTaxWithholdRate().compareTo(reqVO.getMerchantRate()) > 0) {
            throw exception(DIVIDE_RULE_INVALID, "个税代扣比例不能超过服务商比例");
        }
        if (reqVO.getEffectiveTime() == null) {
            reqVO.setEffectiveTime(LocalDateTime.now());
        }
        if ("ENABLE".equals(reqVO.getStatus())) {
            LambdaQueryWrapperX<DivideRuleDO> query = new LambdaQueryWrapperX<>();
            query.eq(DivideRuleDO::getCityLevel, reqVO.getCityLevel());
            query.eq(DivideRuleDO::getCategoryId, reqVO.getCategoryId());
            query.eq(DivideRuleDO::getStatus, "ENABLE");
            query.eq(DivideRuleDO::getEffectiveTime, reqVO.getEffectiveTime());
            query.ne(reqVO.getId() != null, DivideRuleDO::getId, reqVO.getId());
            if (divideRuleMapper.selectCount(query) > 0) {
                throw exception(DIVIDE_RULE_INVALID, "同一城市等级、类目和生效时间只能存在一条启用规则");
            }
        }
    }

    private void validateDivideRuleNotInUse(Long id) {
        if (orderDivideRecordMapper.selectCount(new LambdaQueryWrapperX<OrderDivideRecordDO>()
                .eq(OrderDivideRecordDO::getDivideRuleId, id)) > 0) {
            throw exception(DIVIDE_RULE_IN_USE);
        }
    }

    @Override
    public DivideRuleDO getDivideRule(Long id) {
        return divideRuleMapper.selectById(id);
    }

    @Override
    public DivideRuleDetailRespVO getDivideRuleDetail(Long id) {
        DivideRuleDO divideRule = divideRuleMapper.selectById(id);
        if (divideRule == null) {
            throw exception(DIVIDE_RULE_NOT_EXISTS);
        }
        MerchantServiceCategoryDO category = divideRule.getCategoryId() == null ? null : merchantServiceCategoryMapper.selectById(divideRule.getCategoryId());
        return DivideRuleDetailAssembler.buildDetail(divideRule, category);
    }

    @Override
    public PageResult<DivideRuleRespVO> getDivideRulePage(DivideRulePageReqVO pageReqVO) {
        PageResult<DivideRuleDO> pageResult = divideRuleMapper.selectPage(pageReqVO);
        List<DivideRuleRespVO> list = BeanUtils.toBean(pageResult.getList(), DivideRuleRespVO.class);
        fillCategoryDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private void fillCategoryDisplayInfo(List<DivideRuleRespVO> list) {
        Set<Long> categoryIds = new HashSet<>();
        for (DivideRuleRespVO item : list) {
            if (item.getCategoryId() != null) {
                categoryIds.add(item.getCategoryId());
            }
        }
        if (categoryIds.isEmpty()) {
            return;
        }
        Map<Long, MerchantServiceCategoryDO> categoryMap = new HashMap<>();
        for (MerchantServiceCategoryDO category : merchantServiceCategoryMapper.selectBatchIds(categoryIds)) {
            categoryMap.put(category.getId(), category);
        }
        list.forEach(item -> {
            MerchantServiceCategoryDO category = categoryMap.get(item.getCategoryId());
            if (category != null) {
                item.setCategoryName(category.getCategoryName());
            }
        });
    }

}
