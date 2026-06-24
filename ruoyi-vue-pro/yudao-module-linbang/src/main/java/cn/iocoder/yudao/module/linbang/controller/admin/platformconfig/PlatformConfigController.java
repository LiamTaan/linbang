package cn.iocoder.yudao.module.linbang.controller.admin.platformconfig;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo.PlatformConfigSaveReqVO;
import cn.iocoder.yudao.module.linbang.service.platformconfig.PlatformConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

@Tag(name = "管理后台 - 平台配置")
@RestController
@RequestMapping("/platform-config/setting")
@Validated
public class PlatformConfigController {

    @Resource
    private PlatformConfigService platformConfigService;

    @GetMapping("/page")
    @Operation(summary = "获取平台配置分页")
    @PreAuthorize("@ss.hasPermission('linbang:platform-config:setting:query')")
    public CommonResult<PageResult<PlatformConfigRespVO>> getPlatformConfigPage(@Valid PlatformConfigPageReqVO reqVO) {
        return success(platformConfigService.getPlatformConfigPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取平台配置详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:platform-config:setting:query')")
    public CommonResult<PlatformConfigDetailRespVO> getPlatformConfig(@RequestParam("id") Long id) {
        return success(platformConfigService.getPlatformConfigDetail(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建平台配置")
    @PreAuthorize("@ss.hasPermission('linbang:platform-config:setting:create')")
    public CommonResult<Long> createPlatformConfig(@Valid @RequestBody PlatformConfigSaveReqVO reqVO) {
        return success(platformConfigService.createPlatformConfig(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新平台配置")
    @PreAuthorize("@ss.hasPermission('linbang:platform-config:setting:update')")
    public CommonResult<Boolean> updatePlatformConfig(@Valid @RequestBody PlatformConfigSaveReqVO reqVO) {
        platformConfigService.updatePlatformConfig(reqVO);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出平台配置")
    @PreAuthorize("@ss.hasPermission('linbang:platform-config:setting:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPlatformConfigExcel(@Valid PlatformConfigPageReqVO reqVO, HttpServletResponse response)
            throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PlatformConfigRespVO> list = platformConfigService.getPlatformConfigPage(reqVO).getList();
        ExcelUtils.write(response, "平台配置.xls", "数据", PlatformConfigRespVO.class, list);
    }
}
