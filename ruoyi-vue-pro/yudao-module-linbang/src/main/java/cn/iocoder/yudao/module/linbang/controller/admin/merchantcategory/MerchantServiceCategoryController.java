package cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.service.merchantcategory.MerchantServiceCategoryService;

@Tag(name = "管理后台 - 服务类目表")
@RestController
@RequestMapping("/linbang/merchant-service-category")
@Validated
public class MerchantServiceCategoryController {

    @Resource
    private MerchantServiceCategoryService merchantServiceCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建服务类目表")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-service-category:create')")
    public CommonResult<Long> createMerchantServiceCategory(@Valid @RequestBody MerchantServiceCategorySaveReqVO createReqVO) {
        return success(merchantServiceCategoryService.createMerchantServiceCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新服务类目表")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-service-category:update')")
    public CommonResult<Boolean> updateMerchantServiceCategory(@Valid @RequestBody MerchantServiceCategorySaveReqVO updateReqVO) {
        merchantServiceCategoryService.updateMerchantServiceCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除服务类目表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:merchant-service-category:delete')")
    public CommonResult<Boolean> deleteMerchantServiceCategory(@RequestParam("id") Long id) {
        merchantServiceCategoryService.deleteMerchantServiceCategory(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除服务类目表")
                @PreAuthorize("@ss.hasPermission('linbang:merchant-service-category:delete')")
    public CommonResult<Boolean> deleteMerchantServiceCategoryList(@RequestParam("ids") List<Long> ids) {
        merchantServiceCategoryService.deleteMerchantServiceCategoryListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得服务类目表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-service-category:query')")
    public CommonResult<MerchantServiceCategoryRespVO> getMerchantServiceCategory(@RequestParam("id") Long id) {
        MerchantServiceCategoryDO merchantServiceCategory = merchantServiceCategoryService.getMerchantServiceCategory(id);
        return success(BeanUtils.toBean(merchantServiceCategory, MerchantServiceCategoryRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得服务类目表分页")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-service-category:query')")
    public CommonResult<PageResult<MerchantServiceCategoryRespVO>> getMerchantServiceCategoryPage(@Valid MerchantServiceCategoryPageReqVO pageReqVO) {
        PageResult<MerchantServiceCategoryDO> pageResult = merchantServiceCategoryService.getMerchantServiceCategoryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MerchantServiceCategoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出服务类目表 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-service-category:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMerchantServiceCategoryExcel(@Valid MerchantServiceCategoryPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MerchantServiceCategoryDO> list = merchantServiceCategoryService.getMerchantServiceCategoryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "服务类目表.xls", "数据", MerchantServiceCategoryRespVO.class,
                        BeanUtils.toBean(list, MerchantServiceCategoryRespVO.class));
    }

}