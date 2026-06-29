package cn.iocoder.yudao.module.linbang.service.app.promote;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppInviteCodeRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteCenterRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteInviteCodeBindReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteTemplatePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteTemplateRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteTeamStatsRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;

public interface AppPromoteService {

    AppPromoteCenterRespVO getPromoteCenter(Long userId);

    PageResult<CommissionOrderDO> getCommissionPage(Long userId, AppCommissionPageReqVO reqVO);

    AppInviteCodeRespVO getInviteCode(Long userId);

    void bindInviteCode(Long userId, AppPromoteInviteCodeBindReqVO reqVO);

    AppPromoteTeamStatsRespVO getTeamStats(Long userId);

    PageResult<AppPromoteTemplateRespVO> getTemplatePage(Long userId, AppPromoteTemplatePageReqVO reqVO);

    AppPromoteTemplateRespVO getTemplate(Long userId, Long id);
}
