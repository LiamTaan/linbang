package cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userfrozenfundrecord.vo.UserFrozenFundRecordRespVO;
import cn.iocoder.yudao.module.linbang.service.userfrozenfundrecord.UserFrozenFundRecordService;
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

@Tag(name = "管理后台 - 冻结资金记录")
@RestController
@RequestMapping("/risk/user-frozen-fund-record")
@Validated
public class UserFrozenFundRecordController {

    @Resource
    private UserFrozenFundRecordService userFrozenFundRecordService;

    @GetMapping("/page")
    @Operation(summary = "获取冻结资金记录分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:user-frozen-fund-record:query')")
    public CommonResult<PageResult<UserFrozenFundRecordRespVO>> getPage(@Valid UserFrozenFundRecordPageReqVO reqVO) {
        return success(userFrozenFundRecordService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取冻结资金记录详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:risk:user-frozen-fund-record:query')")
    public CommonResult<UserFrozenFundRecordDetailRespVO> get(@RequestParam("id") Long id) {
        return success(userFrozenFundRecordService.getDetail(id));
    }
}
