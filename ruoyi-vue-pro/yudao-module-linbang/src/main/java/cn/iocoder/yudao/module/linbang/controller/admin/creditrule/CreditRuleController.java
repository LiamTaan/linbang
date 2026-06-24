package cn.iocoder.yudao.module.linbang.controller.admin.creditrule;

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

import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;
import cn.iocoder.yudao.module.linbang.service.creditrule.CreditRuleService;

@Tag(name = "管理后台 - 信用分规则")
@RestController
@RequestMapping("/review/credit-rule")
@Validated
public class CreditRuleController {

    @Resource
    private CreditRuleService creditRuleService;

    @PostMapping("/create")
    @Operation(summary = "创建信用分规则")
    @PreAuthorize("@ss.hasPermission('linbang:review:credit-rule:create')")
    public CommonResult<Long> createCreditRule(@Valid @RequestBody CreditRuleSaveReqVO createReqVO) {
        return success(creditRuleService.createCreditRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新信用分规则")
    @PreAuthorize("@ss.hasPermission('linbang:review:credit-rule:update')")
    public CommonResult<Boolean> updateCreditRule(@Valid @RequestBody CreditRuleSaveReqVO updateReqVO) {
        creditRuleService.updateCreditRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除信用分规则")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:review:credit-rule:delete')")
    public CommonResult<Boolean> deleteCreditRule(@RequestParam("id") Long id) {
        creditRuleService.deleteCreditRule(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除信用分规则")
                @PreAuthorize("@ss.hasPermission('linbang:review:credit-rule:delete')")
    public CommonResult<Boolean> deleteCreditRuleList(@RequestParam("ids") List<Long> ids) {
        creditRuleService.deleteCreditRuleListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得信用分规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:review:credit-rule:query')")
    public CommonResult<CreditRuleDetailRespVO> getCreditRule(@RequestParam("id") Long id) {
        return success(creditRuleService.getCreditRuleDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得信用分规则分页")
    @PreAuthorize("@ss.hasPermission('linbang:review:credit-rule:query')")
    public CommonResult<PageResult<CreditRuleRespVO>> getCreditRulePage(@Valid CreditRulePageReqVO pageReqVO) {
        PageResult<CreditRuleDO> pageResult = creditRuleService.getCreditRulePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CreditRuleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出信用分规则 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:review:credit-rule:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCreditRuleExcel(@Valid CreditRulePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CreditRuleDO> list = creditRuleService.getCreditRulePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "信用分规则.xls", "数据", CreditRuleRespVO.class,
                        BeanUtils.toBean(list, CreditRuleRespVO.class));
    }

}
