package cn.iocoder.yudao.module.linbang.service.promoter;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteInviteCodeBindReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;

public interface PromoterService {

    PromoterDO getPromoterByUserId(Long userId);

    PromoterDO getOrCreatePromoter(Long userId);

    void bindInviteCode(Long userId, AppPromoteInviteCodeBindReqVO reqVO);

    void handleOrderFinished(OrderInfoDO order, OrderUnitDO unit);

    PageResult<PromoterRespVO> getPromoterPage(PromoterPageReqVO reqVO);

    PromoterDetailRespVO getPromoterDetail(Long id);
}
