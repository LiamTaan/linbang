package cn.iocoder.yudao.module.linbang.controller.app.help.feedback;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo.AppHelpFeedbackCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo.AppHelpFeedbackPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo.AppHelpFeedbackRespVO;
import cn.iocoder.yudao.module.linbang.service.helpfeedback.HelpFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 帮助反馈")
@RestController
@RequestMapping("/help/feedback")
@Validated
public class AppHelpFeedbackController {

    @Resource
    private HelpFeedbackService helpFeedbackService;

    @PostMapping("/create")
    @Operation(summary = "创建帮助反馈")
    public CommonResult<Long> createHelpFeedback(@Valid @RequestBody AppHelpFeedbackCreateReqVO reqVO) {
        return success(helpFeedbackService.createHelpFeedback(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获取帮助反馈分页")
    public CommonResult<PageResult<AppHelpFeedbackRespVO>> getHelpFeedbackPage(@Valid AppHelpFeedbackPageReqVO reqVO) {
        return success(BeanUtils.toBean(helpFeedbackService.getAppHelpFeedbackPage(getLoginUserId(), reqVO),
                AppHelpFeedbackRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获取帮助反馈详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppHelpFeedbackRespVO> getHelpFeedback(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(helpFeedbackService.getAppHelpFeedback(getLoginUserId(), id),
                AppHelpFeedbackRespVO.class));
    }
}
