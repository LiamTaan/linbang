package cn.iocoder.yudao.module.linbang.controller.app.member.qualification;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo.AppMemberQualificationRespVO;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberQualificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/page")
    @Operation(summary = "分页获取资质认证列表")
    public CommonResult<PageResult<AppMemberQualificationRespVO>> getQualificationPage() {
        return success(appMemberQualificationService.getQualificationPage(getLoginUserId()));
    }

}
