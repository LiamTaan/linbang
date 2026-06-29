package cn.iocoder.yudao.module.linbang.service.promoteappeal;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealRespVO;

public interface PromoteAppealService {

    Long createAppAppeal(Long userId, AppPromoteAppealCreateReqVO reqVO);

    PageResult<AppPromoteAppealRespVO> getAppPage(Long userId, AppPromoteAppealPageReqVO reqVO);

    PageResult<PromoteAppealRespVO> getAdminPage(PromoteAppealPageReqVO reqVO);

    PromoteAppealRespVO getAdminAppeal(Long id);

    void auditAppeal(Long operatorId, PromoteAppealAuditReqVO reqVO);
}
