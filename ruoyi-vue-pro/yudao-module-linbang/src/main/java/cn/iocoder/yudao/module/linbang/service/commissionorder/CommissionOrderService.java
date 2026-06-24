package cn.iocoder.yudao.module.linbang.service.commissionorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;

public interface CommissionOrderService {

    PageResult<CommissionOrderRespVO> getCommissionOrderPage(CommissionOrderPageReqVO reqVO);

    CommissionOrderDetailRespVO getCommissionOrderDetail(Long id);

    PageResult<CommissionOrderDO> getAppCommissionOrderPage(Long promoterId, AppCommissionPageReqVO reqVO);
}
