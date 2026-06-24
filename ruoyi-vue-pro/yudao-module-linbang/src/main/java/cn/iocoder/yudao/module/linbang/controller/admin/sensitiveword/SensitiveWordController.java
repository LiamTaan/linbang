package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword;

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

import cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveword.SensitiveWordDO;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveWordService;

@Tag(name = "管理后台 - 敏感词表")
@RestController
@RequestMapping("/risk/sensitive-word")
@Validated
public class SensitiveWordController {

    @Resource
    private SensitiveWordService sensitiveWordService;

    @PostMapping("/create")
    @Operation(summary = "创建敏感词表")
    @PreAuthorize("@ss.hasPermission('linbang:sensitive-word:create')")
    public CommonResult<Long> createSensitiveWord(@Valid @RequestBody SensitiveWordSaveReqVO createReqVO) {
        return success(sensitiveWordService.createSensitiveWord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新敏感词表")
    @PreAuthorize("@ss.hasPermission('linbang:sensitive-word:update')")
    public CommonResult<Boolean> updateSensitiveWord(@Valid @RequestBody SensitiveWordSaveReqVO updateReqVO) {
        sensitiveWordService.updateSensitiveWord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除敏感词表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:sensitive-word:delete')")
    public CommonResult<Boolean> deleteSensitiveWord(@RequestParam("id") Long id) {
        sensitiveWordService.deleteSensitiveWord(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除敏感词表")
                @PreAuthorize("@ss.hasPermission('linbang:sensitive-word:delete')")
    public CommonResult<Boolean> deleteSensitiveWordList(@RequestParam("ids") List<Long> ids) {
        sensitiveWordService.deleteSensitiveWordListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得敏感词表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:sensitive-word:query')")
    public CommonResult<SensitiveWordDetailRespVO> getSensitiveWord(@RequestParam("id") Long id) {
        return success(sensitiveWordService.getSensitiveWordDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得敏感词表分页")
    @PreAuthorize("@ss.hasPermission('linbang:sensitive-word:query')")
    public CommonResult<PageResult<SensitiveWordRespVO>> getSensitiveWordPage(@Valid SensitiveWordPageReqVO pageReqVO) {
        PageResult<SensitiveWordDO> pageResult = sensitiveWordService.getSensitiveWordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SensitiveWordRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出敏感词表 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:sensitive-word:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSensitiveWordExcel(@Valid SensitiveWordPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SensitiveWordDO> list = sensitiveWordService.getSensitiveWordPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "敏感词表.xls", "数据", SensitiveWordRespVO.class,
                        BeanUtils.toBean(list, SensitiveWordRespVO.class));
    }

}
