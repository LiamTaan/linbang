package cn.iocoder.yudao.module.linbang.controller.admin.memberuser;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;

@Tag(name = "管理后台 - 用户主表")
@RestController
@RequestMapping("/linbang/member-user")
@Validated
public class MemberUserController {

    @Resource
    private MemberUserService memberUserService;

    @PostMapping("/create")
    @Operation(summary = "创建用户主表")
    @PreAuthorize("@ss.hasPermission('linbang:member-user:create')")
    public CommonResult<Long> createMemberUser(@Valid @RequestBody MemberUserSaveReqVO createReqVO) {
        return success(memberUserService.createMemberUser(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户主表")
    @PreAuthorize("@ss.hasPermission('linbang:member-user:update')")
    public CommonResult<Boolean> updateMemberUser(@Valid @RequestBody MemberUserSaveReqVO updateReqVO) {
        memberUserService.updateMemberUser(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户主表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:member-user:delete')")
    public CommonResult<Boolean> deleteMemberUser(@RequestParam("id") Long id) {
        memberUserService.deleteMemberUser(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除用户主表")
                @PreAuthorize("@ss.hasPermission('linbang:member-user:delete')")
    public CommonResult<Boolean> deleteMemberUserList(@RequestParam("ids") List<Long> ids) {
        memberUserService.deleteMemberUserListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户主表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:member-user:query')")
    public CommonResult<MemberUserDetailRespVO> getMemberUser(@RequestParam("id") Long id) {
        return success(memberUserService.getMemberUserDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户主表分页")
    @PreAuthorize("@ss.hasPermission('linbang:member-user:query')")
    public CommonResult<PageResult<MemberUserRespVO>> getMemberUserPage(@Valid MemberUserPageReqVO pageReqVO) {
        PageResult<MemberUserDO> pageResult = memberUserService.getMemberUserPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MemberUserRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户主表 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:member-user:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMemberUserExcel(@Valid MemberUserPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MemberUserDO> list = memberUserService.getMemberUserPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户主表.xls", "数据", MemberUserRespVO.class,
                        BeanUtils.toBean(list, MemberUserRespVO.class));
    }

}
