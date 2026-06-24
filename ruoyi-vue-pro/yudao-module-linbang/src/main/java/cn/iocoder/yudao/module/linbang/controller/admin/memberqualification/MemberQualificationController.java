package cn.iocoder.yudao.module.linbang.controller.admin.memberqualification;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationRespVO;
import cn.iocoder.yudao.module.linbang.service.memberqualification.MemberQualificationService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "管理后台 - 用户资质")
@RestController
@RequestMapping("/member/qualification")
@Validated
public class MemberQualificationController {

    @Resource
    private MemberQualificationService memberQualificationService;

    @GetMapping("/page")
    @Operation(summary = "获得用户资质分页")
    @PreAuthorize("@ss.hasPermission('linbang:member:qualification:query')")
    public CommonResult<PageResult<MemberQualificationRespVO>> getQualificationPage(@Valid MemberQualificationPageReqVO pageReqVO) {
        return success(memberQualificationService.getQualificationPage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户资质详情")
    @PreAuthorize("@ss.hasPermission('linbang:member:qualification:query')")
    public CommonResult<MemberQualificationDetailRespVO> getQualification(@RequestParam("id") Long id) {
        return success(memberQualificationService.getQualificationDetail(id));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核用户资质")
    @PreAuthorize("@ss.hasPermission('linbang:member:qualification:audit')")
    public CommonResult<Boolean> auditQualification(@Valid @RequestBody MemberQualificationAuditReqVO reqVO) {
        memberQualificationService.auditQualification(reqVO);
        return success(true);
    }

}
