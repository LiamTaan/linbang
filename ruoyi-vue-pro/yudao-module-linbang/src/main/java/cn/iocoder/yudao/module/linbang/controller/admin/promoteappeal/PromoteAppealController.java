package cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealRespVO;
import cn.iocoder.yudao.module.linbang.service.promoteappeal.PromoteAppealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 推广申诉")
@RestController
@RequestMapping("/promote/appeal")
@Validated
public class PromoteAppealController {

    @Resource
    private PromoteAppealService promoteAppealService;

    @GetMapping("/page")
    @Operation(summary = "获取推广申诉分页")
    @PreAuthorize("@ss.hasPermission('linbang:promote:appeal:query')")
    public CommonResult<PageResult<PromoteAppealRespVO>> getPage(@Valid PromoteAppealPageReqVO reqVO) {
        return success(promoteAppealService.getAdminPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取推广申诉详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:promote:appeal:query')")
    public CommonResult<PromoteAppealRespVO> get(@RequestParam("id") Long id) {
        return success(promoteAppealService.getAdminAppeal(id));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核推广申诉")
    @PreAuthorize("@ss.hasPermission('linbang:promote:appeal:audit')")
    public CommonResult<Boolean> audit(@Valid @RequestBody PromoteAppealAuditReqVO reqVO) {
        promoteAppealService.auditAppeal(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }
}
