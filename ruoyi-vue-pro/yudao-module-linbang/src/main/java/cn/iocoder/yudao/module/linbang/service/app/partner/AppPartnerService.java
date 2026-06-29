package cn.iocoder.yudao.module.linbang.service.app.partner;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerCoordinationCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerDisputePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerDisputeRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerEntryAuditPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerEntryAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerEntryAuditRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerInstructionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPriceReportCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPriceReportPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPriceReportRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPromoteStatRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerRegionRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerWorkbenchRespVO;

public interface AppPartnerService {

    AppPartnerWorkbenchRespVO getWorkbench(Long userId);

    AppPartnerRegionRespVO getRegionDetail(Long userId);

    PageResult<AppPartnerEntryAuditRespVO> getEntryAuditPage(Long userId, AppPartnerEntryAuditPageReqVO reqVO);

    AppPartnerEntryAuditRespVO getEntryAudit(Long userId, Long id);

    void auditEntry(Long userId, AppPartnerEntryAuditReqVO reqVO);

    PageResult<AppPartnerDisputeRespVO> getDisputePage(Long userId, AppPartnerDisputePageReqVO reqVO);

    AppPartnerDisputeRespVO getDispute(Long userId, String disputeType, Long disputeId);

    Long createCoordination(Long userId, AppPartnerCoordinationCreateReqVO reqVO);

    PageResult<AppPartnerPriceReportRespVO> getPriceReportPage(Long userId, AppPartnerPriceReportPageReqVO reqVO);

    AppPartnerPriceReportRespVO getPriceReport(Long userId, Long id);

    Long createPriceReport(Long userId, AppPartnerPriceReportCreateReqVO reqVO);

    void withdrawPriceReport(Long userId, Long id);

    AppPartnerPromoteStatRespVO getPromoteStat(Long userId);

    PageResult<AppMessageRecordRespVO> getInstructionPage(Long userId, AppPartnerInstructionPageReqVO reqVO);

    AppMessageRecordDetailRespVO getInstruction(Long userId, Long id);
}
