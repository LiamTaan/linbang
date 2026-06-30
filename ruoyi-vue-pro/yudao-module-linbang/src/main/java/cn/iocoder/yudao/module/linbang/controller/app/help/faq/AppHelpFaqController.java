package cn.iocoder.yudao.module.linbang.controller.app.help.faq;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.help.faq.vo.AppHelpFaqPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.help.faq.vo.AppHelpFaqRespVO;
import cn.iocoder.yudao.module.linbang.service.helpfaq.HelpFaqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 常见问题")
@RestController
@RequestMapping("/help/faq")
@Validated
public class AppHelpFaqController {

    @Resource
    private HelpFaqService helpFaqService;

    @GetMapping("/list")
    @Operation(summary = "获取常见问题列表")
    @PermitAll
    public CommonResult<List<AppHelpFaqRespVO>> getHelpFaqList(@Valid AppHelpFaqPageReqVO reqVO) {
        return success(helpFaqService.getAppHelpFaqList(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取常见问题详情")
    @Parameter(name = "id", description = "常见问题 ID", required = true, example = "1")
    @PermitAll
    public CommonResult<AppHelpFaqRespVO> getHelpFaq(@RequestParam("id") Long id) {
        return success(helpFaqService.getAppHelpFaq(id));
    }
}
