package cn.iocoder.yudao.module.linbang.controller.admin.riskrule;

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

import cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;
import cn.iocoder.yudao.module.linbang.service.riskrule.RiskRuleService;

@Tag(name = "管理后台 - 风控规则表")
@RestController
@RequestMapping("/risk/rule")
@Validated
public class RiskRuleController {

    @Resource
    private RiskRuleService riskRuleService;

    @PostMapping("/create")
    @Operation(summary = "创建风控规则表")
    @PreAuthorize("@ss.hasPermission('linbang:risk-rule:create')")
    public CommonResult<Long> createRiskRule(@Valid @RequestBody RiskRuleSaveReqVO createReqVO) {
        return success(riskRuleService.createRiskRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新风控规则表")
    @PreAuthorize("@ss.hasPermission('linbang:risk-rule:update')")
    public CommonResult<Boolean> updateRiskRule(@Valid @RequestBody RiskRuleSaveReqVO updateReqVO) {
        riskRuleService.updateRiskRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除风控规则表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:risk-rule:delete')")
    public CommonResult<Boolean> deleteRiskRule(@RequestParam("id") Long id) {
        riskRuleService.deleteRiskRule(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除风控规则表")
                @PreAuthorize("@ss.hasPermission('linbang:risk-rule:delete')")
    public CommonResult<Boolean> deleteRiskRuleList(@RequestParam("ids") List<Long> ids) {
        riskRuleService.deleteRiskRuleListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得风控规则表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:risk-rule:query')")
    public CommonResult<RiskRuleDetailRespVO> getRiskRule(@RequestParam("id") Long id) {
        return success(riskRuleService.getRiskRuleDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得风控规则表分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk-rule:query')")
    public CommonResult<PageResult<RiskRuleRespVO>> getRiskRulePage(@Valid RiskRulePageReqVO pageReqVO) {
        PageResult<RiskRuleDO> pageResult = riskRuleService.getRiskRulePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RiskRuleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出风控规则表 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:risk-rule:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportRiskRuleExcel(@Valid RiskRulePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RiskRuleDO> list = riskRuleService.getRiskRulePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "风控规则表.xls", "数据", RiskRuleRespVO.class,
                        BeanUtils.toBean(list, RiskRuleRespVO.class));
    }

}
