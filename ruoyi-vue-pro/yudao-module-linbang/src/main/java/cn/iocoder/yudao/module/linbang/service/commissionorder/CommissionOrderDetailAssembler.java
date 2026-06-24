package cn.iocoder.yudao.module.linbang.service.commissionorder;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;

final class CommissionOrderDetailAssembler {

    private CommissionOrderDetailAssembler() {
    }

    static CommissionOrderDetailRespVO build(CommissionOrderDO commissionOrder, PromoterDO promoter, MemberUserDO user,
                                             MemberUserDO promoterUser, OrderInfoDO order, OrderUnitDO unit) {
        CommissionOrderDetailRespVO respVO = BeanUtils.toBean(commissionOrder, CommissionOrderDetailRespVO.class);
        if (promoter != null) {
            CommissionOrderDetailRespVO.PromoterRespVO promoterRespVO =
                    BeanUtils.toBean(promoter, CommissionOrderDetailRespVO.PromoterRespVO.class);
            if (promoterUser != null) {
                promoterRespVO.setUserNo(promoterUser.getUserNo());
                promoterRespVO.setUserNickname(promoterUser.getNickname());
                promoterRespVO.setUserMobile(promoterUser.getMobile());
            }
            respVO.setPromoter(promoterRespVO);
        }
        if (user != null) {
            respVO.setUser(BeanUtils.toBean(user, CommissionOrderDetailRespVO.UserRespVO.class));
        }
        if (order != null) {
            respVO.setSourceOrder(BeanUtils.toBean(order, CommissionOrderDetailRespVO.OrderRespVO.class));
        }
        if (unit != null) {
            respVO.setSourceUnit(BeanUtils.toBean(unit, CommissionOrderDetailRespVO.UnitRespVO.class));
        }
        return respVO;
    }
}
