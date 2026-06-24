package cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo.HelpFeedbackRespVO;
import cn.iocoder.yudao.module.linbang.service.helpfeedback.HelpFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 帮助反馈")
@RestController
@RequestMapping("/help/feedback")
@Validated
public class HelpFeedbackController {

    @Resource
    private HelpFeedbackService helpFeedbackService;

    @GetMapping("/page")
    @Operation(summary = "获得帮助反馈分页")
    @PreAuthorize("@ss.hasPermission('linbang:help:feedback:query')")
    public CommonResult<PageResult<HelpFeedbackRespVO>> getHelpFeedbackPage(@Valid HelpFeedbackPageReqVO reqVO) {
        return success(helpFeedbackService.getAdminHelpFeedbackPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得帮助反馈详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:help:feedback:query')")
    public CommonResult<HelpFeedbackDetailRespVO> getHelpFeedback(@RequestParam("id") Long id) {
        return success(helpFeedbackService.getHelpFeedbackDetail(id));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出帮助反馈")
    @PreAuthorize("@ss.hasPermission('linbang:help:feedback:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportHelpFeedbackExcel(@Valid HelpFeedbackPageReqVO reqVO, HttpServletResponse response)
            throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<HelpFeedbackRespVO> list = helpFeedbackService.getAdminHelpFeedbackPage(reqVO).getList();
        ExcelUtils.write(response, "帮助反馈.xls", "数据", HelpFeedbackRespVO.class, list);
    }
}
