package cn.iocoder.yudao.module.linbang.service.creditrule;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 信用分规则 Service 接口
 *
 * @author dawn
 */
public interface CreditRuleService {

    /**
     * 创建信用分规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCreditRule(@Valid CreditRuleSaveReqVO createReqVO);

    /**
     * 更新信用分规则
     *
     * @param updateReqVO 更新信息
     */
    void updateCreditRule(@Valid CreditRuleSaveReqVO updateReqVO);

    /**
     * 删除信用分规则
     *
     * @param id 编号
     */
    void deleteCreditRule(Long id);

    /**
    * 批量删除信用分规则
    *
    * @param ids 编号
    */
    void deleteCreditRuleListByIds(List<Long> ids);

    /**
     * 获得信用分规则
     *
     * @param id 编号
     * @return 信用分规则
     */
    CreditRuleDO getCreditRule(Long id);

    /**
     * 获得信用分规则详情
     *
     * @param id 编号
     * @return 信用分规则详情
     */
    CreditRuleDetailRespVO getCreditRuleDetail(Long id);

    /**
     * 获得信用分规则分页
     *
     * @param pageReqVO 分页查询
     * @return 信用分规则分页
     */
    PageResult<CreditRuleDO> getCreditRulePage(CreditRulePageReqVO pageReqVO);

}
