package cn.iocoder.yudao.module.linbang.service.merchantpricereport;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportRespVO;

import javax.validation.Valid;

public interface MerchantPriceReportService {

    PageResult<MerchantPriceReportRespVO> getMerchantPriceReportPage(MerchantPriceReportPageReqVO reqVO);

    MerchantPriceReportDetailRespVO getMerchantPriceReportDetail(Long id);

    void auditMerchantPriceReport(@Valid MerchantPriceReportAuditReqVO reqVO);
}
