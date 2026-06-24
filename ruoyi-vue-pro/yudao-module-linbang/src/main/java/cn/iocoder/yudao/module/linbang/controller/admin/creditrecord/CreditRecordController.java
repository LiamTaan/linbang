package cn.iocoder.yudao.module.linbang.controller.admin.creditrecord;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordRespVO;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

@Tag(name = "管理后台 - 信用记录")
@RestController
@RequestMapping("/review/credit-record")
@Validated
public class CreditRecordController {

    @Resource
    private CreditRecordService creditRecordService;

    @GetMapping("/page")
    @Operation(summary = "获得信用记录分页")
    @PreAuthorize("@ss.hasPermission('linbang:review:credit-record:query')")
    public CommonResult<PageResult<CreditRecordRespVO>> getCreditRecordPage(@Valid CreditRecordPageReqVO pageReqVO) {
        return success(creditRecordService.getCreditRecordPage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得信用记录详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:review:credit-record:query')")
    public CommonResult<CreditRecordDetailRespVO> getCreditRecord(@RequestParam("id") Long id) {
        return success(creditRecordService.getCreditRecordDetail(id));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出信用记录 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:review:credit-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCreditRecordExcel(@Valid CreditRecordPageReqVO pageReqVO,
                                        HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CreditRecordRespVO> list = creditRecordService.getCreditRecordPage(pageReqVO).getList();
        ExcelUtils.write(response, "信用记录.xls", "数据", CreditRecordRespVO.class, list);
    }
}
