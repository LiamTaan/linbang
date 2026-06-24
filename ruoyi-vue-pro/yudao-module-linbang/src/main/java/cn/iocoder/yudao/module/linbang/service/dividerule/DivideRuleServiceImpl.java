package cn.iocoder.yudao.module.linbang.service.dividerule;

import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
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

    @Resource
    private DivideRuleMapper divideRuleMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;

    @Override
    public Long createDivideRule(DivideRuleSaveReqVO createReqVO) {
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
        // 更新
        DivideRuleDO updateObj = BeanUtils.toBean(updateReqVO, DivideRuleDO.class);
        divideRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteDivideRule(Long id) {
        // 校验存在
        validateDivideRuleExists(id);
        // 删除
        divideRuleMapper.deleteById(id);
    }

    @Override
        public void deleteDivideRuleListByIds(List<Long> ids) {
        // 删除
        divideRuleMapper.deleteByIds(ids);
        }


    private void validateDivideRuleExists(Long id) {
        if (divideRuleMapper.selectById(id) == null) {
            throw exception(DIVIDE_RULE_NOT_EXISTS);
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
