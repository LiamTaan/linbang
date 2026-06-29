package cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordRespVO;
import cn.iocoder.yudao.module.linbang.service.userrestrictrecord.UserRestrictRecordService;
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

@Tag(name = "管理后台 - 用户限制记录")
@RestController
@RequestMapping("/risk/user-restrict-record")
@Validated
public class UserRestrictRecordController {

    @Resource
    private UserRestrictRecordService userRestrictRecordService;

    @GetMapping("/page")
    @Operation(summary = "获取用户限制记录分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:user-restrict-record:query')")
    public CommonResult<PageResult<UserRestrictRecordRespVO>> getPage(@Valid UserRestrictRecordPageReqVO reqVO) {
        return success(userRestrictRecordService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取用户限制记录详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:risk:user-restrict-record:query')")
    public CommonResult<UserRestrictRecordDetailRespVO> get(@RequestParam("id") Long id) {
        return success(userRestrictRecordService.getDetail(id));
    }
}
