package cn.iocoder.yudao.module.linbang.service.app.promote;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppInviteCodeRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteCenterRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.service.commissionorder.CommissionOrderService;
import cn.iocoder.yudao.module.linbang.service.promoter.PromoterService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Validated
public class AppPromoteServiceImpl implements AppPromoteService {

    @Resource
    private PromoterService promoterService;
    @Resource
    private CommissionOrderService commissionOrderService;

    @Override
    public AppPromoteCenterRespVO getPromoteCenter(Long userId) {
        PromoterDO promoter = promoterService.getOrCreatePromoter(userId);
        PageResult<CommissionOrderDO> recentPage = commissionOrderService.getAppCommissionOrderPage(promoter.getId(),
                new AppCommissionPageReqVO());
        List<CommissionOrderDO> commissionOrders = recentPage.getList();
        AppPromoteCenterRespVO respVO = BeanUtils.toBean(promoter, AppPromoteCenterRespVO.class);
        respVO.setPendingCommissionCount(countByStatus(commissionOrders, "PENDING"));
        respVO.setSettledCommissionCount(countByStatus(commissionOrders, "SETTLED"));
        respVO.setInvalidCommissionCount(countByStatus(commissionOrders, "INVALID"));
        respVO.setPendingCommissionAmount(sumAmountByStatus(commissionOrders, "PENDING"));
        respVO.setSettledCommissionAmount(sumAmountByStatus(commissionOrders, "SETTLED"));
        respVO.setRecentCommissionOrders(commissionOrders.stream()
                .limit(5)
                .map(item -> BeanUtils.toBean(item, AppPromoteCenterRespVO.RecentCommissionRespVO.class))
                .collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public PageResult<CommissionOrderDO> getCommissionPage(Long userId, AppCommissionPageReqVO reqVO) {
        PromoterDO promoter = promoterService.getOrCreatePromoter(userId);
        return commissionOrderService.getAppCommissionOrderPage(promoter.getId(), reqVO);
    }

    @Override
    public AppInviteCodeRespVO getInviteCode(Long userId) {
        PromoterDO promoter = promoterService.getOrCreatePromoter(userId);
        return new AppInviteCodeRespVO(promoter.getInviteCode(), promoter.getInviteUrl());
    }

    private Integer countByStatus(List<CommissionOrderDO> commissionOrders, String status) {
        return (int) commissionOrders.stream()
                .filter(item -> Objects.equals(item.getStatus(), status))
                .count();
    }

    private BigDecimal sumAmountByStatus(List<CommissionOrderDO> commissionOrders, String status) {
        return commissionOrders.stream()
                .filter(item -> Objects.equals(item.getStatus(), status))
                .map(CommissionOrderDO::getCommissionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
