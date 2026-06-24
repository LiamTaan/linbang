package cn.iocoder.yudao.module.linbang.service.dividerule;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.dividerule.DivideRuleDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 分账规则 Service 接口
 *
 * @author dawn
 */
public interface DivideRuleService {

    /**
     * 创建分账规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDivideRule(@Valid DivideRuleSaveReqVO createReqVO);

    /**
     * 更新分账规则
     *
     * @param updateReqVO 更新信息
     */
    void updateDivideRule(@Valid DivideRuleSaveReqVO updateReqVO);

    /**
     * 删除分账规则
     *
     * @param id 编号
     */
    void deleteDivideRule(Long id);

    /**
    * 批量删除分账规则
    *
    * @param ids 编号
    */
    void deleteDivideRuleListByIds(List<Long> ids);

    /**
     * 获得分账规则
     *
     * @param id 编号
     * @return 分账规则
     */
    DivideRuleDO getDivideRule(Long id);

    /**
     * 获得分账规则详情
     *
     * @param id 编号
     * @return 分账规则详情
     */
    DivideRuleDetailRespVO getDivideRuleDetail(Long id);

    /**
     * 获得分账规则分页
     *
     * @param pageReqVO 分页查询
     * @return 分账规则分页
     */
    PageResult<DivideRuleRespVO> getDivideRulePage(DivideRulePageReqVO pageReqVO);

}
