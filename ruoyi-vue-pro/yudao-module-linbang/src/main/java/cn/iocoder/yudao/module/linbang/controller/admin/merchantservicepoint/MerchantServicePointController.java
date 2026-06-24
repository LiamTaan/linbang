package cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointSaveReqVO;
import cn.iocoder.yudao.module.linbang.service.merchantservicepoint.MerchantServicePointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 服务点")
@RestController
@RequestMapping("/merchant/service-point")
@Validated
public class MerchantServicePointController {

    @Resource
    private MerchantServicePointService merchantServicePointService;

    @PostMapping("/create")
    @Operation(summary = "创建服务点")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:service-point:create')")
    public CommonResult<Long> createMerchantServicePoint(@Valid @RequestBody MerchantServicePointSaveReqVO createReqVO) {
        return success(merchantServicePointService.createMerchantServicePoint(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新服务点")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:service-point:update')")
    public CommonResult<Boolean> updateMerchantServicePoint(@Valid @RequestBody MerchantServicePointSaveReqVO updateReqVO) {
        merchantServicePointService.updateMerchantServicePoint(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除服务点")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:merchant:service-point:delete')")
    public CommonResult<Boolean> deleteMerchantServicePoint(@RequestParam("id") Long id) {
        merchantServicePointService.deleteMerchantServicePoint(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除服务点")
    @Parameter(name = "ids", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:merchant:service-point:delete')")
    public CommonResult<Boolean> deleteMerchantServicePointList(@RequestParam("ids") List<Long> ids) {
        merchantServicePointService.deleteMerchantServicePointListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得服务点详情")
    @Parameter(name = "id", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:service-point:query')")
    public CommonResult<MerchantServicePointDetailRespVO> getMerchantServicePoint(@RequestParam("id") Long id) {
        return success(merchantServicePointService.getMerchantServicePointDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得服务点分页")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:service-point:query')")
    public CommonResult<PageResult<MerchantServicePointRespVO>> getMerchantServicePointPage(@Valid MerchantServicePointPageReqVO pageReqVO) {
        return success(merchantServicePointService.getMerchantServicePointPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出服务点 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:service-point:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMerchantServicePointExcel(@Valid MerchantServicePointPageReqVO pageReqVO,
                                                HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MerchantServicePointRespVO> list = merchantServicePointService.getMerchantServicePointPage(pageReqVO).getList();
        ExcelUtils.write(response, "服务点.xls", "数据", MerchantServicePointRespVO.class, list);
    }
}
