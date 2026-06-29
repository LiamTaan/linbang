package cn.iocoder.yudao.module.linbang.controller.admin.certexemption;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionRespVO;
import cn.iocoder.yudao.module.linbang.service.certexemption.CertExemptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 证件豁免申请")
@RestController
@RequestMapping("/member/cert-exemption")
@Validated
public class CertExemptionController {

    @Resource
    private CertExemptionService certExemptionService;

    @GetMapping("/page")
    @Operation(summary = "获得证件豁免申请分页")
    @PreAuthorize("@ss.hasPermission('linbang:member:cert-exemption:query')")
    public CommonResult<PageResult<CertExemptionRespVO>> getPage(@Valid CertExemptionPageReqVO reqVO) {
        return success(certExemptionService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得证件豁免申请详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:member:cert-exemption:query')")
    public CommonResult<CertExemptionDetailRespVO> getDetail(@RequestParam("id") Long id) {
        return success(certExemptionService.getDetail(id));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核证件豁免申请")
    @PreAuthorize("@ss.hasPermission('linbang:member:cert-exemption:audit')")
    public CommonResult<Boolean> audit(@Valid @RequestBody CertExemptionAuditReqVO reqVO) {
        certExemptionService.audit(reqVO);
        return success(true);
    }
}
