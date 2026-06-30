package cn.iocoder.yudao.module.linbang.controller.admin.helpfaq;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo.HelpFaqSaveReqVO;
import cn.iocoder.yudao.module.linbang.service.helpfaq.HelpFaqService;
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

@Tag(name = "管理后台 - 常见问题")
@RestController
@RequestMapping("/help/faq")
@Validated
public class HelpFaqController {

    @Resource
    private HelpFaqService helpFaqService;

    @PostMapping("/create")
    @Operation(summary = "创建常见问题")
    @PreAuthorize("@ss.hasPermission('linbang:help:faq:create')")
    public CommonResult<Long> createHelpFaq(@Valid @RequestBody HelpFaqSaveReqVO reqVO) {
        return success(helpFaqService.createHelpFaq(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新常见问题")
    @PreAuthorize("@ss.hasPermission('linbang:help:faq:update')")
    public CommonResult<Boolean> updateHelpFaq(@Valid @RequestBody HelpFaqSaveReqVO reqVO) {
        helpFaqService.updateHelpFaq(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除常见问题")
    @Parameter(name = "id", description = "常见问题 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('linbang:help:faq:delete')")
    public CommonResult<Boolean> deleteHelpFaq(@RequestParam("id") Long id) {
        helpFaqService.deleteHelpFaq(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除常见问题")
    @Parameter(name = "ids", description = "常见问题 ID 列表", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:help:faq:delete')")
    public CommonResult<Boolean> deleteHelpFaqList(@RequestParam("ids") List<Long> ids) {
        helpFaqService.deleteHelpFaqList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得常见问题详情")
    @Parameter(name = "id", description = "常见问题 ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('linbang:help:faq:query')")
    public CommonResult<HelpFaqRespVO> getHelpFaq(@RequestParam("id") Long id) {
        return success(helpFaqService.getHelpFaq(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得常见问题分页")
    @PreAuthorize("@ss.hasPermission('linbang:help:faq:query')")
    public CommonResult<PageResult<HelpFaqRespVO>> getHelpFaqPage(@Valid HelpFaqPageReqVO reqVO) {
        return success(helpFaqService.getHelpFaqPage(reqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出常见问题")
    @PreAuthorize("@ss.hasPermission('linbang:help:faq:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportHelpFaqExcel(@Valid HelpFaqPageReqVO reqVO, HttpServletResponse response) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<HelpFaqRespVO> list = helpFaqService.getHelpFaqPage(reqVO).getList();
        ExcelUtils.write(response, "常见问题.xls", "数据", HelpFaqRespVO.class, list);
    }
}
