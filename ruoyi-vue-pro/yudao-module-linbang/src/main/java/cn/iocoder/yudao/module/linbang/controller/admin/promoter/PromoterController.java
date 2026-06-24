package cn.iocoder.yudao.module.linbang.controller.admin.promoter;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterRespVO;
import cn.iocoder.yudao.module.linbang.service.promoter.PromoterService;
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

@Tag(name = "管理后台 - 推广员")
@RestController
@RequestMapping("/promote/user")
@Validated
public class PromoterController {

    @Resource
    private PromoterService promoterService;

    @GetMapping("/page")
    @Operation(summary = "获得推广员分页")
    @PreAuthorize("@ss.hasPermission('linbang:promote:user:query')")
    public CommonResult<PageResult<PromoterRespVO>> getPromoterPage(@Valid PromoterPageReqVO reqVO) {
        return success(promoterService.getPromoterPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得推广员详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:promote:user:query')")
    public CommonResult<PromoterDetailRespVO> getPromoter(@RequestParam("id") Long id) {
        return success(promoterService.getPromoterDetail(id));
    }
}
