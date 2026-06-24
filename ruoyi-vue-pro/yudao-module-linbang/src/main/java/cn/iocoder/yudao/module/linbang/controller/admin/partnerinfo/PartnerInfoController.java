package cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoRespVO;
import cn.iocoder.yudao.module.linbang.service.partnerinfo.PartnerInfoService;
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
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 区域合作商")
@RestController
@RequestMapping("/partner/info")
@Validated
public class PartnerInfoController {

    @Resource
    private PartnerInfoService partnerInfoService;

    @GetMapping("/page")
    @Operation(summary = "获得合作商分页")
    @PreAuthorize("@ss.hasPermission('linbang:partner:info:query')")
    public CommonResult<PageResult<PartnerInfoRespVO>> getPartnerInfoPage(@Valid PartnerInfoPageReqVO reqVO) {
        return success(partnerInfoService.getPartnerInfoPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得合作商详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:partner:info:query')")
    public CommonResult<PartnerInfoDetailRespVO> getPartnerInfo(@RequestParam("id") Long id) {
        return success(partnerInfoService.getPartnerInfoDetail(id));
    }
}
