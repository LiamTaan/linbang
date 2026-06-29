package cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation.vo.UserRiskRelationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation.vo.UserRiskRelationRespVO;
import cn.iocoder.yudao.module.linbang.service.userriskrelation.UserRiskRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 关联账号监控")
@RestController
@RequestMapping("/risk/user-risk-relation")
@Validated
public class UserRiskRelationController {

    @Resource
    private UserRiskRelationService userRiskRelationService;

    @GetMapping("/page")
    @Operation(summary = "获取关联账号监控分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:user-risk-relation:query')")
    public CommonResult<PageResult<UserRiskRelationRespVO>> getPage(@Valid UserRiskRelationPageReqVO reqVO) {
        return success(userRiskRelationService.getPage(reqVO));
    }
}
