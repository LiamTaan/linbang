package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo.SensitiveImageScanResultPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo.SensitiveImageScanResultRespVO;
import cn.iocoder.yudao.module.linbang.service.sensitiveimagescanresult.SensitiveImageScanResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 图片 OCR 审核明细")
@RestController
@RequestMapping("/risk/sensitive-image-scan-result")
@Validated
public class SensitiveImageScanResultController {

    @Resource
    private SensitiveImageScanResultService sensitiveImageScanResultService;

    @GetMapping("/page")
    @Operation(summary = "获取图片 OCR 审核明细分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:sensitive-image-scan-result:query')")
    public CommonResult<PageResult<SensitiveImageScanResultRespVO>> getPage(@Valid SensitiveImageScanResultPageReqVO reqVO) {
        return success(sensitiveImageScanResultService.getPage(reqVO));
    }
}
