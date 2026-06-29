package cn.iocoder.yudao.module.linbang.service.ordersplitrule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRulePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRuleRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRuleSaveReqVO;

import javax.validation.Valid;

public interface OrderSplitRuleService {

    Long createOrderSplitRule(@Valid OrderSplitRuleSaveReqVO createReqVO);

    void updateOrderSplitRule(@Valid OrderSplitRuleSaveReqVO updateReqVO);

    void deleteOrderSplitRule(Long id);

    OrderSplitRuleRespVO getOrderSplitRule(Long id);

    PageResult<OrderSplitRuleRespVO> getOrderSplitRulePage(OrderSplitRulePageReqVO pageReqVO);

    OrderSplitPlan matchRule(OrderSplitPreviewContext context);

}
