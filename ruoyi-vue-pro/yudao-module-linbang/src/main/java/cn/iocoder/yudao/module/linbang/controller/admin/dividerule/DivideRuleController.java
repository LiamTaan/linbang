package cn.iocoder.yudao.module.linbang.controller.admin.dividerule;

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
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo.*;
import cn.iocoder.yudao.module.linbang.service.dividerule.DivideRuleService;

@Tag(name = "管理后台 - 分账规则")
@RestController
@RequestMapping("/wallet/divide-rule")
@Validated
public class DivideRuleController {

    @Resource
    private DivideRuleService divideRuleService;

    @PostMapping("/create")
    @Operation(summary = "创建分账规则")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:divide-rule:create')")
    public CommonResult<Long> createDivideRule(@Valid @RequestBody DivideRuleSaveReqVO createReqVO) {
        return success(divideRuleService.createDivideRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新分账规则")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:divide-rule:update')")
    public CommonResult<Boolean> updateDivideRule(@Valid @RequestBody DivideRuleSaveReqVO updateReqVO) {
        divideRuleService.updateDivideRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除分账规则")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:wallet:divide-rule:delete')")
    public CommonResult<Boolean> deleteDivideRule(@RequestParam("id") Long id) {
        divideRuleService.deleteDivideRule(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除分账规则")
                @PreAuthorize("@ss.hasPermission('linbang:wallet:divide-rule:delete')")
    public CommonResult<Boolean> deleteDivideRuleList(@RequestParam("ids") List<Long> ids) {
        divideRuleService.deleteDivideRuleListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得分账规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:divide-rule:query')")
    public CommonResult<DivideRuleDetailRespVO> getDivideRule(@RequestParam("id") Long id) {
        return success(divideRuleService.getDivideRuleDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得分账规则分页")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:divide-rule:query')")
    public CommonResult<PageResult<DivideRuleRespVO>> getDivideRulePage(@Valid DivideRulePageReqVO pageReqVO) {
        return success(divideRuleService.getDivideRulePage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出分账规则 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:wallet:divide-rule:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDivideRuleExcel(@Valid DivideRulePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DivideRuleRespVO> list = divideRuleService.getDivideRulePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "分账规则.xls", "数据", DivideRuleRespVO.class, list);
    }

}
