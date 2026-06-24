package cn.iocoder.yudao.module.linbang.service.riskrule;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 风控规则表 Service 接口
 *
 * @author dawn
 */
public interface RiskRuleService {

    /**
     * 创建风控规则表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createRiskRule(@Valid RiskRuleSaveReqVO createReqVO);

    /**
     * 更新风控规则表
     *
     * @param updateReqVO 更新信息
     */
    void updateRiskRule(@Valid RiskRuleSaveReqVO updateReqVO);

    /**
     * 删除风控规则表
     *
     * @param id 编号
     */
    void deleteRiskRule(Long id);

    /**
    * 批量删除风控规则表
    *
    * @param ids 编号
    */
    void deleteRiskRuleListByIds(List<Long> ids);

    /**
     * 获得风控规则表
     *
     * @param id 编号
     * @return 风控规则表
     */
    RiskRuleDO getRiskRule(Long id);

    /**
     * 获得风控规则详情
     *
     * @param id 编号
     * @return 风控规则详情
     */
    RiskRuleDetailRespVO getRiskRuleDetail(Long id);

    /**
     * 获得风控规则表分页
     *
     * @param pageReqVO 分页查询
     * @return 风控规则表分页
     */
    PageResult<RiskRuleDO> getRiskRulePage(RiskRulePageReqVO pageReqVO);

}
