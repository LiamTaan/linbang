package cn.iocoder.yudao.module.linbang.controller.app.reminder;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderHistoryPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderHistoryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.reminder.AppReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@Tag(name = "用户 App - 闹钟提醒")
@RestController
@RequestMapping("/reminder")
@Validated
public class AppReminderController {

    @Resource
    private AppReminderService appReminderService;

    @PostMapping("/create")
    @Operation(summary = "创建提醒")
    public CommonResult<Long> createReminder(@Valid @RequestBody AppReminderCreateReqVO reqVO) {
        return success(appReminderService.createReminder(getLoginUserId(), reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改提醒")
    public CommonResult<Boolean> updateReminder(@Valid @RequestBody AppReminderUpdateReqVO reqVO) {
        appReminderService.updateReminder(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除提醒")
    @Parameter(name = "id", required = true)
    public CommonResult<Boolean> deleteReminder(@RequestParam("id") Long id) {
        appReminderService.deleteReminder(getLoginUserId(), id);
        return success(Boolean.TRUE);
    }

    @GetMapping("/get")
    @Operation(summary = "获取提醒详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppReminderRespVO> getReminder(@RequestParam("id") Long id) {
        return success(appReminderService.getReminder(getLoginUserId(), id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取提醒列表")
    public CommonResult<PageResult<AppReminderRespVO>> getReminderPage(@Valid AppReminderPageReqVO reqVO) {
        return success(appReminderService.getReminderPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/history/page")
    @Operation(summary = "分页获取提醒历史")
    public CommonResult<PageResult<AppReminderHistoryRespVO>> getReminderHistoryPage(@Valid AppReminderHistoryPageReqVO reqVO) {
        return success(appReminderService.getReminderHistoryPage(getLoginUserId(), reqVO));
    }
}
