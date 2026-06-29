package cn.iocoder.yudao.module.linbang.controller.app.member.qualification;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppCertExemptionCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppCertExemptionRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationReminderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationReminderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationSummaryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberQualificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 资质认证")
@RestController
@RequestMapping("/member/qualification")
@Validated
public class AppMemberQualificationController {

    @Resource
    private AppMemberQualificationService appMemberQualificationService;

    @PostMapping("/create")
    @Operation(summary = "提交资质认证")
    public CommonResult<Long> createQualification(@Valid @RequestBody AppMemberQualificationCreateReqVO reqVO) {
        return success(appMemberQualificationService.createQualification(getLoginUserId(), reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新资质认证")
    public CommonResult<Boolean> updateQualification(@Valid @RequestBody AppMemberQualificationUpdateReqVO reqVO) {
        appMemberQualificationService.updateQualification(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取资质认证列表")
    public CommonResult<PageResult<AppMemberQualificationRespVO>> getQualificationPage() {
        return success(appMemberQualificationService.getQualificationPage(getLoginUserId()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取资质详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppMemberQualificationRespVO> getQualification(@RequestParam("id") Long id) {
        return success(appMemberQualificationService.getQualification(getLoginUserId(), id));
    }

    @GetMapping("/summary/get")
    @Operation(summary = "获取认证与证件汇总")
    public CommonResult<AppMemberQualificationSummaryRespVO> getQualificationSummary() {
        return success(appMemberQualificationService.getQualificationSummary(getLoginUserId()));
    }

    @GetMapping("/reminder/page")
    @Operation(summary = "分页获取证件到期提醒")
    public CommonResult<PageResult<AppMemberQualificationReminderRespVO>> getReminderPage(@Valid AppMemberQualificationReminderPageReqVO reqVO) {
        return success(appMemberQualificationService.getReminderPage(getLoginUserId(), reqVO));
    }

    @PostMapping("/cert-exemption/create")
    @Operation(summary = "提交证件豁免申请")
    public CommonResult<Long> createCertExemption(@Valid @RequestBody AppCertExemptionCreateReqVO reqVO) {
        return success(appMemberQualificationService.createCertExemption(getLoginUserId(), reqVO));
    }

    @GetMapping("/cert-exemption/page")
    @Operation(summary = "分页获取证件豁免申请")
    public CommonResult<PageResult<AppCertExemptionRespVO>> getCertExemptionPage() {
        return success(appMemberQualificationService.getCertExemptionPage(getLoginUserId()));
    }

}
