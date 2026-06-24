package cn.iocoder.yudao.module.linbang.controller.admin.blacklist;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistRespVO;
import cn.iocoder.yudao.module.linbang.service.blacklist.BlacklistService;
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
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 黑名单")
@RestController
@RequestMapping("/risk/blacklist")
@Validated
public class BlacklistController {

    @Resource
    private BlacklistService blacklistService;

    @GetMapping("/get")
    @Operation(summary = "获得黑名单详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:risk:blacklist:query')")
    public CommonResult<BlacklistDetailRespVO> getBlacklist(@RequestParam("id") Long id) {
        return success(blacklistService.getBlacklistDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得黑名单分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:blacklist:query')")
    public CommonResult<PageResult<BlacklistRespVO>> getBlacklistPage(@Valid BlacklistPageReqVO reqVO) {
        return success(blacklistService.getBlacklistPage(reqVO));
    }
}
