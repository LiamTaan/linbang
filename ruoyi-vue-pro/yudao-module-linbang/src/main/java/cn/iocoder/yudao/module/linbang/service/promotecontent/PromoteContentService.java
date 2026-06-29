package cn.iocoder.yudao.module.linbang.service.promotecontent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentOfflineReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteContentCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteContentPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteContentRespVO;

public interface PromoteContentService {

    Long createAppContent(Long userId, AppPromoteContentCreateReqVO reqVO);

    PageResult<AppPromoteContentRespVO> getAppContentPage(Long userId, AppPromoteContentPageReqVO reqVO);

    AppPromoteContentRespVO getAppContent(Long userId, Long id);

    PageResult<PromoteContentRespVO> getAdminPage(PromoteContentPageReqVO reqVO);

    PromoteContentRespVO getAdminContent(Long id);

    void auditContent(Long operatorId, PromoteContentAuditReqVO reqVO);

    void offlineContent(Long operatorId, PromoteContentOfflineReqVO reqVO);
}
