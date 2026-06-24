package cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportRespVO;
import cn.iocoder.yudao.module.linbang.service.merchantpricereport.MerchantPriceReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 价格申报")
@RestController
@RequestMapping("/partner/price-report")
@Validated
public class MerchantPriceReportController {

    @Resource
    private MerchantPriceReportService merchantPriceReportService;

    @GetMapping("/page")
    @Operation(summary = "获得价格申报分页")
    @PreAuthorize("@ss.hasPermission('linbang:partner:price-report:query')")
    public CommonResult<PageResult<MerchantPriceReportRespVO>> getMerchantPriceReportPage(@Valid MerchantPriceReportPageReqVO reqVO) {
        return success(merchantPriceReportService.getMerchantPriceReportPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得价格申报详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:partner:price-report:query')")
    public CommonResult<MerchantPriceReportDetailRespVO> getMerchantPriceReport(@RequestParam("id") Long id) {
        return success(merchantPriceReportService.getMerchantPriceReportDetail(id));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核价格申报")
    @PreAuthorize("@ss.hasPermission('linbang:partner:price-report:audit')")
    public CommonResult<Boolean> auditMerchantPriceReport(@Valid @RequestBody MerchantPriceReportAuditReqVO reqVO) {
        merchantPriceReportService.auditMerchantPriceReport(reqVO);
        return success(true);
    }
}
